package com.vince.geminimi.hooks;

final class PowerKeyTimingPolicy {
    static final long GEMINI_AT_MS = 3_000L;
    static final long POWER_MENU_AT_MS = 5_000L;

    private PowerKeyTimingPolicy() {}

    static long delayUntil(long downAt, long now, long targetAfterDown) {
        if (downAt <= 0L) return 0L;
        long elapsed = Math.max(0L, now - downAt);
        return Math.max(0L, targetAfterDown - elapsed);
    }
}
