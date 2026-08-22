package coupang.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan Lee
 * @version $ RateLimiterManager, v 0.1 2026/8/22 16:43 Ryan Lee Exp $
 * @Description
 */
public class RateLimiterManager {

    public static Map<String, RateLimiter> map = new ConcurrentHashMap<>();

    /**
     * 绑定接口路径和实例
     *
     * @param pathKey 接口路径
     */
    public void bind(String pathKey,  RateLimiter rateLimiter) {

        RateLimiter rateLimiter1 = map.get(pathKey);
        if (null != rateLimiter1) {
            throw new RuntimeException("该接口路径已经有绑定对应的限流器了");
        }
        map.put(pathKey, rateLimiter);
    }

    public void reBind( String pathKey,  RateLimiter rateLimiter) {
        map.put(pathKey, rateLimiter);
    }

    /**
     * 限流判断
     * @param pathKey
     * @return
     */
    public Boolean tryAcquire( String pathKey) {
        RateLimiter rateLimiter = map.get(pathKey);
        if (null == rateLimiter) {
            throw new RuntimeException("该路径没有对应的限流器，请先绑定限流器");
        }
        return rateLimiter.tryAcquire();
    }

}
