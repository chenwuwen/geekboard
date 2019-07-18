package cn.kanyun.geekboard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import cn.kanyun.geekboard.activity.VerificationActivity;

/**
 * 键盘切换广播
 * 在创建完BroadcastReceiver之后，还不能进入工作状态，我们需要为它注册一个指定的广播地址
 * 注册分为：
 * 静态注册 ：静态注册是在AndroidManifest.xml文件中配置的
 * 动态注册：需要在代码中动态的指定广播地址并注册，通常是在Activity或Service注册一个广播
 */
public class InputMethodChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "InputMethodChangeReceiver";


    public InputMethodChangeReceiver() {
    }

    /**
     * 接受广播
     * BroadcastReceiver生命周期只有十秒左右，因此在onReceive()不要做一些耗时的操作，
     * 应该发送给service，由service来完成；还有onReceive()不要开启子线程
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //在这里写上相关的处理代码，一般来说，不要此添加过多的逻辑或者是进行任何的耗时操作
        //因为广播接收器中是不允许开启多线程的，过久的操作就会出现报错
        //因此广播接收器更多的是扮演一种打开程序其他组件的角色，比如创建一条状态栏通知，或者启动某个服务
        Log.d(TAG, "广播接受者接收到了广播");
        if (intent.getAction().equals("键盘改变了")) {
            if (checkIsDefault(context)) {

            } else {

            }
        }
    }


    /**
     * 检测输入法是否是自身的输入法
     * @param context
     * @return
     */
    public boolean checkIsDefault(Context context) {
        boolean isDefault = false;
        try {
            String curInputMethodId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            Log.d(TAG, "广播接受者得到了当前使用的输入法：" + curInputMethodId);
            if (curInputMethodId.equals(VerificationActivity.selfKeyBoardServiceId)){
                isDefault = true;
            }
        } catch (Exception e) {
        }
        return isDefault;
    }
}
