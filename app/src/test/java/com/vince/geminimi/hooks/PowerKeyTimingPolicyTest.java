package com.vince.geminimi.hooks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PowerKeyTimingPolicyTest {
    @Test
    public void schedulesGeminiAtThreeSeconds() {
        assertEquals(3_000L, PowerKeyTimingPolicy.delayUntil(
                10_000L, 10_000L, PowerKeyTimingPolicy.GEMINI_AT_MS));
        assertEquals(1_000L, PowerKeyTimingPolicy.delayUntil(
                10_000L, 12_000L, PowerKeyTimingPolicy.GEMINI_AT_MS));
    }

    @Test
    public void schedulesPowerMenuAtFiveSecondsAndNeverNegative() {
        assertEquals(2_000L, PowerKeyTimingPolicy.delayUntil(
                10_000L, 13_000L, PowerKeyTimingPolicy.POWER_MENU_AT_MS));
        assertEquals(0L, PowerKeyTimingPolicy.delayUntil(
                10_000L, 16_000L, PowerKeyTimingPolicy.POWER_MENU_AT_MS));
    }
}
