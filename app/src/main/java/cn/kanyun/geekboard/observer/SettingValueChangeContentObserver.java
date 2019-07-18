package cn.kanyun.geekboard.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import cn.kanyun.geekboard.activity.VerificationActivity;

/**
 * 监听系统设置项变化
 * 在使用ContentObserver监听时，会接收到多次的onChange事件，事件传回来的Uri的值也有所不同，因此我们还需要对Uri做过滤的工作，以确保Uri是正确的
 */
public class SettingValueChangeContentObserver extends ContentObserver {

    private static final String TAG = "SettingValueChangeContentObserver";

    private Context context;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SettingValueChangeContentObserver(Handler handler) {
        super(handler);
    }


    public SettingValueChangeContentObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    /**
     * 当观察到的Uri发生变化时，回调该方法去处理。所有ContentObserver的派生类都需要重载该方法去处理逻辑
     * @param selfChange  回调后，其值一般为false
     * @param uri
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
        Log.d(TAG, "设置项改变");
//        if (checkAction()) {
//
//        }
    }

    private boolean checkAction() {
        boolean isDefault = false;
        try {
            String curInputMethodId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            if (curInputMethodId.equals(VerificationActivity.selfKeyBoardServiceId)) {
                isDefault = true;
            }
        } catch (Exception e) {
        }
        return isDefault;
    }
}
