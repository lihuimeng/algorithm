package coupang.ratelimiter;

/**
 * @author Ryan Lee
 * @version $ TokenRateLimiter, v 0.1 2026/8/22 13:54 Ryan Lee Exp $
 * @Description
 */
public class TokenRateLimiter extends RateLimiter{

    //每次令牌发放数量
    private long issueCount;

    //每次令牌发放时间间隔
    private long issueTimeSlot;

    //最近一次发放令牌时间
    private long lastIssueTime;

    //当前令牌数量
    private long currentTokenCnt;

    private TokenRateLimiter(long timeWindow, Integer limitCnt) {
        super(timeWindow, limitCnt);
        //如果qps大于1000
        setTokenSpeed(timeWindow, limitCnt);
        lastIssueTime = System.currentTimeMillis()/issueTimeSlot;
    }

    private void setTokenSpeed(long timeWindow, Integer limitCnt) {
        long cnt = limitCnt / timeWindow;
        if (cnt >= 1) {
            issueTimeSlot = 1;
            issueCount = cnt;
        }else {
            issueCount = 1;
            issueTimeSlot = timeWindow / limitCnt;
        }
    }


    public static TokenRateLimiter getInstance(long timeWindow, Integer limitCnt) {
        if (timeWindow < 1000) {
            throw new RuntimeException("timeWindow不能小于1000");
        }

        if (limitCnt < 1) {
            throw new RuntimeException("limitCnt不能小于1");
        }
        return new TokenRateLimiter(timeWindow, limitCnt);
    }

    @Override
    protected  Boolean tryAcquire() {
        synchronized (mutex) {
            //根据当前时间计算和最近一次发放令牌的时间差计算当前令牌数量
            long currentTimeSlot = System.currentTimeMillis()/issueTimeSlot;
            long l = currentTimeSlot - this.lastIssueTime;
            currentTokenCnt = Math.min(l * issueCount + currentTokenCnt, limitCnt);
            if (currentTokenCnt <= 0) {
                return false;
            }
            currentTokenCnt--;
            lastIssueTime = currentTimeSlot;
        }
        return true;
    }

    @Override
    protected void update(long timeWindow, Integer limitCnt) {
        synchronized (mutex) {
            setTokenSpeed(timeWindow, limitCnt);
            lastIssueTime = System.currentTimeMillis() / issueTimeSlot;
            currentTokenCnt = Math.min(currentTokenCnt, limitCnt);
        }
    }

}
