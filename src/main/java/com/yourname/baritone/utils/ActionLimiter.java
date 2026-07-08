package com.yourname.baritone.utils;

public final class ActionLimiter {
    private long lastActionNs;

    public boolean tryAcquire(int perSecond) {
        long now = System.nanoTime();
        long minDelay = 1_000_000_000L / Math.max(1, perSecond);
        if (now - lastActionNs < minDelay) {
            return false;
        }
        lastActionNs = now;
        return true;
    }
}
