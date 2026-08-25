package coupang.mq;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName SimpleMessageQueue
 * @Description 手写消息队列   实现生产者 消费者，消息具有ttl过期时间，过期消息必须释放并无法消费
 */
public class SimpleMessageQueue {
    private static class Message {
        final String data;
        final long expireTime;

        public Message(String data, long ttl_millis) {
            this.data = data;
            this.expireTime = System.currentTimeMillis() + ttl_millis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= this.expireTime;
        }
    }

    private final LinkedList<Message> queue = new LinkedList<>();
    public final ReentrantLock lock = new ReentrantLock();
    public final Condition notEmpty = lock.newCondition();
    public final int maxSize;

    public SimpleMessageQueue(int maxSize) {
        this.maxSize = maxSize;
        //清理线程  过期消息
        Thread thread = new Thread(() -> {
            this.cleanExpiredMsg();
        });
        //Thread thread = new Thread(this::cleanExpiredMsg);
        thread.setDaemon(true);
        thread.start();
    }

    public boolean send(String msgText, long ttlMillis) {
        try {
            lock.lock();
            if (queue.size() >= maxSize) {
                return false;//队列已满
            }
            queue.add(new Message(msgText, ttlMillis));
            //消息进来 等待消费
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public String consume() {
        try {
            lock.lock();
            while (queue.isEmpty()) {
//                消息队列为空,await等待并释放锁await
                notEmpty.await();
            }
            while (!queue.isEmpty()) {
                Message message = queue.pollFirst();
                //如果没有过期 返回消息data
                if (!message.isExpired()) {
                    return message.data;
                }
            }
            /**/
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private void cleanExpiredMsg() {
        while (true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    queue.removeIf(Message::isExpired);
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleMessageQueue queueDemmo = new SimpleMessageQueue(3);
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    queueDemmo.send("msg-" + i, 10000);
                    System.out.println("生产者生产消息 msg:" + i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    String msg = queueDemmo.consume();
                    if (msg != null) {
                        System.out.println("消费者消息 msg:" + msg);
                    } else {
                        System.out.println("无消息可消费");
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        producer.start();
        Thread.sleep(1000);
        consumer.start();
    }
}
