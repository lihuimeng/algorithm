package coupang.ratelimiter;

import java.time.LocalDateTime;

/**
 * @author Ryan Lee
 * @version $ TokenRateLimiter, v 0.1 2026/8/22 13:54 Ryan Lee Exp $
 * @Description
 */
public class TokenRateLimiter extends RateLimiter{

    //发牌速率 令牌数量/秒,默认每秒最少一个令牌
    private int tokenCntSecond;

    //最近一次发放令牌时间秒数
    private long lastIssueTimeSecond;

    //当前令牌数量
    private int currentTokenCnt;

    protected TokenRateLimiter(long timeWindow, Integer limitCnt) {
        super(timeWindow, limitCnt);
        this.tokenCntSecond = getTokenCntSecond(timeWindow, limitCnt);
        lastIssueTimeSecond = System.currentTimeMillis()/1000;
    }

    private static int getTokenCntSecond(long timeWindow, Integer limitCnt) {
        return (int) (limitCnt * 1000 / timeWindow);
    }

    @Override
    protected  Boolean tryAcquire() {
        synchronized (mutex) {
            //根据当前时间计算和最近一次发放令牌的时间差计算当前令牌数量
            long currentTimeSecond = System.currentTimeMillis()/1000;
            long l = (int)(currentTimeSecond - this.lastIssueTimeSecond);
            currentTokenCnt = (int)Math.min(l * tokenCntSecond + currentTokenCnt, limitCnt);
            if (currentTokenCnt <= 0) {
                return false;
            }
            currentTokenCnt--;
            lastIssueTimeSecond = currentTimeSecond;
        }
        return true;
    }

    @Override
    protected void update(long timeWindow, Integer limitCnt) {
        synchronized (mutex) {
            this.tokenCntSecond = getTokenCntSecond(timeWindow, limitCnt);
        }
    }

}
