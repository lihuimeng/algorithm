package coupang.ratelimiter;

import java.util.Arrays;

/**
 * @author Ryan Lee
 * @version $ SlidingWindowRateLimiter, v 0.1 2026/8/22 12:20 Ryan Lee Exp $
 * @Description 滑动窗口，window用的是环形数组
 */
public class SlidingWindowRateLimiter extends RateLimiter {

    /**
     * 定义一个限流桶，窗口由固定数量的桶组成，窗口越大，每个桶的时间跨度越大
     */
    public static class RateLimiterBucket {
        //次数
        private long bucketId;

        private int requestCnt;

        public RateLimiterBucket(long bucketId, int requestCnt) {
            this.bucketId = bucketId;
            this.requestCnt = requestCnt;
        }

        public long getBucketId() {
            return bucketId;
        }

        public int getRequestCnt() {
            return requestCnt;
        }

        public void setBucketId(long bucketId) {
            this.bucketId = bucketId;
        }

        public void setRequestCnt(int requestCnt) {
            this.requestCnt = requestCnt;
        }
    }

    //时间窗口，窗口内请求次数不能超过参数指定次数
    private final RateLimiterBucket[] window;

    //窗口内桶的数量
    public static final int bucketCnt = 50;

    //每个桶的时间跨度
    private long bucketSize;


    //构造方法，初始化window，计算每个桶的时间跨度大小
    private SlidingWindowRateLimiter(long timeWindow, Integer limitCnt) {
        super(timeWindow, limitCnt);
        this.window = new RateLimiterBucket[bucketCnt];
        this.bucketSize = Math.max(super.timeWindow / bucketCnt, 1);
    }

    public static SlidingWindowRateLimiter getInstance(long timeWindow, Integer limitCnt) {
        if (timeWindow < bucketCnt) {
            throw new RuntimeException("窗口大小不能小于" + bucketCnt);
        }
        return new SlidingWindowRateLimiter(timeWindow, limitCnt);
    }


    @Override
    protected Boolean tryAcquire() {
        synchronized (mutex) {
            long l = System.currentTimeMillis();
            //计算当前桶的id，id代表了当前时间等于桶大小的倍数
            long bucketId = l / bucketSize;
            //计算当前桶的下标
            int bucketIndex = (int) (bucketId % bucketCnt);

            //计算有效的最小窗口id
            long latestBucketId = bucketId - bucketCnt + 1;

            Long totalCnt = sumReqCnt(latestBucketId);

            if (totalCnt >= limitCnt) {
                return false;
            }

            //增加统计数量
            addReq(bucketIndex, bucketId);
        }
        return true;
    }

    @Override
    protected void update(long timeWindow, Integer limitCnt) {
        synchronized (mutex) {
            this.bucketSize = super.timeWindow / bucketCnt;
            Arrays.fill(window, null);
        }
    }

    private void addReq(int bucketIndex, long bucketId) {

        //计算最远的一个生效的桶id，每个桶id会放到同一个index下，如果请求qps比较大，一般来说会根据index递增
        //因为防止某个桶是空的或者还是上一轮的请求id，所以不能直接拿着桶数据加，必须过滤掉当前时间id往前数桶数量的id
        long latestBucketId = bucketId - bucketCnt + 1;
        RateLimiterBucket rateLimiterBucket = window[bucketIndex];
        if (null == rateLimiterBucket || rateLimiterBucket.getBucketId() < latestBucketId) {
            rateLimiterBucket = new RateLimiterBucket(bucketId, 1);
            window[bucketIndex] = rateLimiterBucket;
            return;
        }
        rateLimiterBucket.setRequestCnt(rateLimiterBucket.getRequestCnt() + 1);
    }


    private Long sumReqCnt(long latestBucketId) {
        if (null == window || window.length==0) {
            return 0L;
        }

        return Arrays.stream(window).filter(bucket -> null != bucket && bucket.getBucketId() >= latestBucketId)
                .mapToLong(RateLimiterBucket::getRequestCnt).sum();
    }





}
