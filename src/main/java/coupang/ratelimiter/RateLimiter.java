package coupang.ratelimiter;

/**
 * @author Ryan Lee 限流器，支持多个限流器互相切换，不同接口指定不同限流规则限流值
 * @version $ SimpleRateLimiter, v 0.1 2026/8/22 12:04 Ryan Lee Exp $
 * @Description
 */
public abstract class RateLimiter {

    protected long timeWindow;

    protected Integer limitCnt;

    protected RateLimiter(long timeWindow, Integer limitCnt) {
        this.timeWindow = timeWindow;
        this.limitCnt = limitCnt;
    }

    protected  abstract  Boolean tryAcquire();

    public void updateWindow(long timeWindow, Integer limitCnt) {
        this.timeWindow = timeWindow;
        this.limitCnt = limitCnt;
        update(timeWindow, limitCnt);
    }

    protected abstract void update(long timeWindow, Integer limitCnt);

}
