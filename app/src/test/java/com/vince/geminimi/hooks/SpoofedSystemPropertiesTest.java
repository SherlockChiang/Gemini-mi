package com.vince.geminimi.hooks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SpoofedSystemPropertiesTest {
    @Test
    public void matchesTelephonyPropertiesAndSlots() {
        assertEquals("310030", value("gsm.operator.numeric"));
        assertEquals("310030", value("gsm.sim.operator.numeric.0"));
        assertEquals("us", value("gsm.operator.iso-country,1"));
    }

    @Test
    public void rejectsMalformedOrUnrelatedProperties() {
        assertNull(value(null));
        assertNull(value("gsm.operator.numeric."));
        assertNull(value("gsm.operator.numeric.0x"));
        assertNull(value("gsm.operator.alpha"));
    }

    private static String value(String key) {
        return SpoofedSystemProperties.valueFor(key, "310030", "us");
    }
}
