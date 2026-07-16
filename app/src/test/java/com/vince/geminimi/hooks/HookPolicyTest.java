package com.vince.geminimi.hooks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

public class HookPolicyTest {
    @Test
    public void hooksKnownInstanceMethodsWithSupportedReturnTypes() throws Exception {
        assertTrue(HookPolicy.shouldHookPowerMethod(method("launchVoiceAssistant")));
        assertTrue(HookPolicy.shouldHookPowerMethod(method("launchAssistAction")));
        assertTrue(HookPolicy.shouldHookPowerMethod(method("launchXiaoAiOnPowerLong")));
    }

    @Test
    public void rejectsUnknownStaticAndUnsupportedMethods() throws Exception {
        assertFalse(HookPolicy.shouldHookPowerMethod(method("unrelated")));
        assertFalse(HookPolicy.shouldHookPowerMethod(
                StaticFixtures.class.getDeclaredMethod("launchVoiceAssistant")));
        assertFalse(HookPolicy.shouldHookPowerMethod(method("launchVoiceAssist")));
    }

    @Test
    public void matchesOnlyConfirmedPowerWakeupKey() {
        assertTrue(HookPolicy.isPowerWakeupKey("power_wakeup"));
        assertFalse(HookPolicy.isPowerWakeupKey("power_wake_lock_enabled"));
        assertFalse(HookPolicy.isPowerWakeupKey(null));
    }

    @Test
    public void matchesOnlyLongPressPowerShortcut() {
        assertTrue(HookPolicy.isPowerLongPressShortcut("long_press_power_key"));
        assertFalse(HookPolicy.isPowerLongPressShortcut("headset"));
        assertFalse(HookPolicy.isPowerLongPressShortcut(null));
    }

    private static Method method(String name) throws Exception {
        return Fixtures.class.getDeclaredMethod(name);
    }

    private static final class Fixtures {
        void launchVoiceAssistant() {}
        boolean launchAssistAction() { return false; }
        int launchXiaoAiOnPowerLong() { return 0; }
        void unrelated() {}
        Object launchVoiceAssist() { return null; }
    }

    private static final class StaticFixtures {
        static void launchVoiceAssistant() {}
    }
}
