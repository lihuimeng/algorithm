package coupang.ratelimiter;

/**
 * @author Ryan Lee
 * @version $ RateLimiterFactory, v 0.1 2026/8/22 16:29 Ryan Lee Exp $
 * @Description
 */
public class RateLimiterFactory {

    public enum RateLimiterTypeEnum{
        SLIDING_WINDOW,
        TOKEN_BUCKET,
        ;
    }

    public static RateLimiter create(RateLimiterTypeEnum rateLimiterTypeEnum, long timeWindow, Integer limitCnt) {
        switch(rateLimiterTypeEnum) {
            case TOKEN_BUCKET:
                return new TokenRateLimiter(timeWindow, limitCnt);
            case SLIDING_WINDOW:
                return new SlidingWindowRateLimiter(timeWindow, limitCnt);
            default:
                throw new RuntimeException("参数错误，无对应的限流器实例");
        }
    }


}
