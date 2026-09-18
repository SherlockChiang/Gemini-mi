package com.vince.geminimi;

public final class Constants {
    private Constants() {}

    public static final String TAG = "[GeminiMi]";

    public static final String XIAOAI_PKG  = "com.miui.voiceassist";
    public static final String GEMINI_PKG  = "com.google.android.apps.bard";
    public static final String GOOGLE_APP_PKG = "com.google.android.googlequicksearchbox";
    public static final String SYSTEM      = "android";

    public static final String SECURE_ASSISTANT       = "assistant";
    public static final String SECURE_VOICE_INTERACT  = "voice_interaction_service";
    public static final String SECURE_VOICE_RECOG     = "voice_recognition_service";
    public static final String SECURE_ASSIST_STRUCTURE = "assist_structure_enabled";
    public static final String SECURE_ASSIST_SCREENSHOT = "assist_screenshot_enabled";

    // 教程里"创建快捷方式 → 电源菜单 → 数字助理"实际写入的就是这个 Global int。
    // AOSP PhoneWindowManager: LONG_PRESS_POWER_ASSISTANT = 5
    public static final String GLOBAL_POWER_LONG_PRESS  = "power_button_long_press";
    public static final int    LONG_PRESS_POWER_ASSIST  = 5;

    public static final String GSB_ASSIST_SERVICE  =
            "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService";
    public static final String GSB_RECOG_SERVICE   =
            "com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService";
}
