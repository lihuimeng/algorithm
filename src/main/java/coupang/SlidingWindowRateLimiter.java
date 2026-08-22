package coupang;

import java.util.Map;
import java.util.TreeMap;

/**
 * 线程安全的滑动窗口限流器。
 *
 * <p>{@code windowSize} 表示时间窗口大小，单位为毫秒；{@code bucketSize}
 * 表示该窗口内允许通过的最大请求数。例如：
 * {@code new SlidingWindowRateLimiter(1_000, 100)} 表示任意连续 1 秒内最多
 * 允许 100 个请求通过。</p>
 *
 * <p>使用时间戳统计每毫秒内的请求数，而不是为每个请求单独创建对象。每次
 * 尝试获取许可时都会清理过期时间戳，因此只保留可能影响后续判断的请求记录。</p>
 */
public class SlidingWindowRateLimiter {

    /** 滑动窗口大小，单位为毫秒。 */
    private final long windowSize;

    /** 滑动窗口内允许通过的最大请求数。 */
    private final int bucketSize;

    /** 按请求通过时的毫秒时间戳分组记录请求数。 */
    private final TreeMap<Long, Integer> window = new TreeMap<>();

    /** 当前仍处于滑动窗口内的请求总数。 */
    private long requestCount;

    public SlidingWindowRateLimiter(long windowSize, int bucketSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize 必须大于 0");
        }
        if (bucketSize <= 0) {
            throw new IllegalArgumentException("bucketSize 必须大于 0");
        }
        this.windowSize = windowSize;
        this.bucketSize = bucketSize;
    }

    /**
     * 尝试消耗一个请求许可。
     *
     * @return 请求被允许时返回 {@code true}，窗口已达到上限时返回 {@code false}
     */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        removeExpired(now);

        if (requestCount >= bucketSize) {
            return false;
        }

        window.merge(now, 1, Integer::sum);
        requestCount++;
        return true;
    }

    /**
     * 清理当前窗口左边界及之前的过期请求。
     * 时间戳等于 {@code now - windowSize} 的请求不再属于当前窗口。
     */
    private void removeExpired(long now) {
        long expiry = now - windowSize;
        while (!window.isEmpty() && window.firstKey() <= expiry) {
            Map.Entry<Long, Integer> expired = window.pollFirstEntry();
            requestCount -= expired.getValue();
        }
    }
}
