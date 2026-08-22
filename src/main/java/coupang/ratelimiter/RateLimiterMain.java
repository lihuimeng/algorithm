package coupang.ratelimiter;

public class RateLimiterMain {

    public static void main(String[] args) throws Exception {
        RateLimiterManager rateLimiterManager = new RateLimiterManager();

        RateLimiter rateLimiter = RateLimiterFactory.create(RateLimiterFactory.RateLimiterTypeEnum.SLIDING_WINDOW, 1000, 100);
        String pathKey1 = "token/接口1";
        rateLimiterManager.bind(pathKey1, rateLimiter);
        RateLimiter rateLimiter2 = RateLimiterFactory.create(RateLimiterFactory.RateLimiterTypeEnum.TOKEN_BUCKET, 1000, 100);
        String pathKey2 = "token/接口2";
        rateLimiterManager.bind(pathKey2, rateLimiter2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                int falseCnt = 0;
                for (int i = 0; i < 1000; i++) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Boolean b = rateLimiterManager.tryAcquire(pathKey1);
                    if (!b) {
                        falseCnt++;
                    }
//                    System.out.println(pathKey1 + ":" + i + ":" + b);
                }
                System.out.println("falseCnt:" + falseCnt);
                System.out.println("time:" + (System.currentTimeMillis() - l));
            }
        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 500; i++) {
//                    try {
//                        Thread.sleep(11);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Boolean b = rateLimiterManager.tryAcquire(pathKey2);
//                    System.out.println(pathKey2 + ":" + b);
//                }
//
//                for (int i = 0; i < 500; i++) {
//                    try {
//                        Thread.sleep(7);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Boolean b = rateLimiterManager.tryAcquire(pathKey2);
//                    System.out.println(pathKey2 + ":" + b);
//                }
//            }
//        }).start();


    }

    private static void testFactory() {
        RateLimiter tokenLimiter = RateLimiterFactory.create(
                RateLimiterFactory.RateLimiterTypeEnum.TOKEN_BUCKET, 1_000, 2);
        RateLimiter slidingLimiter = RateLimiterFactory.create(
                RateLimiterFactory.RateLimiterTypeEnum.SLIDING_WINDOW, 1_000, 2);

        System.out.println("factory token class = " + tokenLimiter.getClass().getSimpleName());
        System.out.println("factory sliding class = " + slidingLimiter.getClass().getSimpleName());
    }

    private static void testTokenBucket() throws InterruptedException {
        System.out.println("== token bucket ==");
        RateLimiter limiter = RateLimiterFactory.create(
                RateLimiterFactory.RateLimiterTypeEnum.TOKEN_BUCKET, 1_000, 2);

        System.out.println("immediate 1 = " + limiter.tryAcquire());
        System.out.println("immediate 2 = " + limiter.tryAcquire());
        System.out.println("immediate 3 = " + limiter.tryAcquire());

        Thread.sleep(1_100);

        System.out.println("after wait 1 = " + limiter.tryAcquire());
        System.out.println("after wait 2 = " + limiter.tryAcquire());
        System.out.println("after wait 3 = " + limiter.tryAcquire());
    }

}
