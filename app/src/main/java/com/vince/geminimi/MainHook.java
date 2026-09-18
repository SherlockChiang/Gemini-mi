package com.vince.geminimi;

import com.vince.geminimi.hooks.AssistantPersistHook;
import com.vince.geminimi.hooks.PowerKeyOverlayHook;
import com.vince.geminimi.hooks.SimSpoofHook;
import com.vince.geminimi.hooks.XiaoAiPowerKeyDisableHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) {
        switch (lpp.packageName) {
            case Constants.SYSTEM:
                PowerKeyOverlayHook.apply(lpp);
                AssistantPersistHook.applySystemServer(lpp);
                break;
            case Constants.XIAOAI_PKG:
                XiaoAiPowerKeyDisableHook.apply(lpp);
                break;
            case Constants.GEMINI_PKG:
            case Constants.GOOGLE_APP_PKG:
                SimSpoofHook.apply(lpp);
                break;
            default:
                return;
        }
        XposedBridge.log(Constants.TAG + " loaded into " + lpp.packageName);
    }
}
