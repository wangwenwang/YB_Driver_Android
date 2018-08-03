package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 调价屏幕亮度
 * Created by ${tom} on 2017/12/15.
 */
public class ScreenManager {

    /**
     * 设置当前activity的屏幕亮度
     *
     * @param paramFloat 0-1.0f
     * @param activity   需要调整亮度的activity
     */
    public static void setActivityBrightness(float paramFloat, Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = paramFloat;
        localWindow.setAttributes(params);
    }

    /**
     * 获取当前activity的屏幕亮度
     *
     * @param activity 当前的activity对象
     * @return 亮度值范围为0-0.1f，如果为-1.0，则亮度与全局同步。
     */
    public static float getActivityBrightness(Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        return params.screenBrightness;
    }

}
