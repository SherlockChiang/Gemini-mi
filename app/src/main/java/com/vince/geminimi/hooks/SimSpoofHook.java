package com.vince.geminimi.hooks;

import java.lang.reflect.Method;

import com.vince.geminimi.Constants;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/** Makes Google's region checks see a US SIM/network inside the hooked process. */
public final class SimSpoofHook {
    private static final String FAKE_ISO = "us";
    private static final String FAKE_ISO_SUBSCRIPTION = "US";
    private static final String FAKE_MCC_MNC = "310030";
    private static final int FAKE_MCC = 310;
    private static final int FAKE_MNC = 30;

    private SimSpoofHook() {}

    public static void apply(XC_LoadPackage.LoadPackageParam lpp) {
        XposedBridge.log(Constants.TAG + " applying SIM spoof in " + lpp.packageName
                + " (process=" + lpp.processName + ")");
        hookTelephonyManager(lpp.classLoader);
        hookSubscriptionInfo(lpp.classLoader);
        hookSystemProperties(lpp.classLoader);
    }

    private static void hookTelephonyManager(ClassLoader cl) {
        Class<?> clazz;
        try {
            clazz = XposedHelpers.findClass("android.telephony.TelephonyManager", cl);
        } catch (Throwable t) {
            log("TelephonyManager not found: " + t);
            return;
        }
        hookAllReturning(clazz, "getSimCountryIso", FAKE_ISO);
        hookAllReturning(clazz, "getNetworkCountryIso", FAKE_ISO);
        hookAllReturning(clazz, "getSimCountryIsoForPhone", FAKE_ISO);
        hookAllReturning(clazz, "getNetworkCountryIsoForPhone", FAKE_ISO);
        hookAllReturning(clazz, "getSimOperator", FAKE_MCC_MNC);
        hookAllReturning(clazz, "getNetworkOperator", FAKE_MCC_MNC);
        hookAllReturning(clazz, "getSimOperatorNumeric", FAKE_MCC_MNC);
        hookAllReturning(clazz, "getNetworkOperatorNumeric", FAKE_MCC_MNC);
        hookAllReturning(clazz, "getSimOperatorNumericForPhone", FAKE_MCC_MNC);
        hookAllReturning(clazz, "getNetworkOperatorForPhone", FAKE_MCC_MNC);
    }

    private static void hookSubscriptionInfo(ClassLoader cl) {
        Class<?> clazz;
        try {
            clazz = XposedHelpers.findClass("android.telephony.SubscriptionInfo", cl);
        } catch (Throwable t) {
            log("SubscriptionInfo not found: " + t);
            return;
        }
        hookAllReturning(clazz, "getCountryIso", FAKE_ISO_SUBSCRIPTION);
        hookAllReturning(clazz, "getMccString", "310");
        hookAllReturning(clazz, "getMncString", "030");
        hookAllReturning(clazz, "getMcc", FAKE_MCC);
        hookAllReturning(clazz, "getMnc", FAKE_MNC);
    }

    private static void hookAllReturning(Class<?> clazz, String name, Object value) {
        try {
            int count = 0;
            for (Method method : clazz.getDeclaredMethods()) {
                if (!name.equals(method.getName()) || !canReturn(method.getReturnType(), value)) {
                    continue;
                }
                XposedBridge.hookMethod(method, constant(value));
                count++;
            }
            if (count > 0) {
                log("hooked " + count + " overload(s) of " + clazz.getSimpleName()
                        + "." + name + " -> " + value);
            }
        } catch (Throwable t) {
            log("hooking " + clazz.getSimpleName() + "." + name + " failed: " + t);
        }
    }

    private static boolean canReturn(Class<?> returnType, Object value) {
        return returnType.isInstance(value)
                || (returnType == int.class && value instanceof Integer);
    }

    private static XC_MethodReplacement constant(final Object value) {
        return new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                return value;
            }
        };
    }

    private static void hookSystemProperties(ClassLoader cl) {
        Class<?> clazz;
        try {
            clazz = XposedHelpers.findClass("android.os.SystemProperties", cl);
        } catch (Throwable t) {
            log("SystemProperties not found: " + t);
            return;
        }
        XC_MethodHook hook = new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                String key = (String) param.args[0];
                String fake = SpoofedSystemProperties.valueFor(key, FAKE_MCC_MNC, FAKE_ISO);
                if (fake != null) return fake;
                String original = (String) XposedBridge.invokeOriginalMethod(
                        param.method, param.thisObject, param.args);
                return original == null ? (param.args.length > 1 ? param.args[1] : "") : original;
            }
        };
        try {
            XposedHelpers.findAndHookMethod(clazz, "get", String.class, hook);
            XposedHelpers.findAndHookMethod(clazz, "get", String.class, String.class, hook);
            log("SystemProperties telephony hooks installed");
        } catch (Throwable t) {
            log("SystemProperties hooks failed: " + t);
        }
    }

    private static void log(String message) {
        XposedBridge.log(Constants.TAG + " " + message);
    }
}
