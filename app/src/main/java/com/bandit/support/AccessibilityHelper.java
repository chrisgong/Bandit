package com.bandit.support;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.bandit.service.BanditService;

public class AccessibilityHelper {
    private static AccessibilityHelper accessibilityHelper = null;

    private AccessibilityHelper() {
    }

    public static AccessibilityHelper getInstance() {
        if (accessibilityHelper == null) {
            synchronized (AccessibilityHelper.class) {
                if (accessibilityHelper == null)
                    accessibilityHelper = new AccessibilityHelper();
            }
        }
        return accessibilityHelper;
    }

    /**
     * Check当前辅助服务是否启用，启动
     *
     * @param context
     * @return
     */
    public static boolean checkAccessibilityEnabled(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + BanditService.class.getName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {

        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return accessibilityFound;
    }

    /**
     * 打开目标 APP
     *
     * @param context
     */
    public static void goApp(Context context) {
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage("com.nike.snkrs"));
    }
}
