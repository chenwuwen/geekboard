package cn.kanyun.geekboard.observer;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import cn.kanyun.geekboard.MainActivity;
import cn.kanyun.geekboard.activity.VerificationActivity;

/**
 * 监听系统设置项变化
 * 在使用ContentObserver监听时，会接收到多次的onChange事件，事件传回来的Uri的值也有所不同，因此我们还需要对Uri做过滤的工作，以确保Uri是正确的
 */
public class SettingValueChangeContentObserver extends ContentObserver {

    private static final String TAG = "SettingValueChangeContentObserver";

    private Context context;

    private Handler handler;

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
        this.handler = handler;
    }

    /**
     * 当观察到的Uri发生变化时，回调该方法去处理。所有ContentObserver的派生类都需要重载该方法去处理逻辑
     *
     * @param selfChange 回调后，其值一般为false
     * @param uri
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
//      启用/激活键盘时Uri：content://settings/secure/enabled_input_methods
//      设置默认键盘/或者说切换键盘时的Uri:content://settings/secure/default_input_method
        Log.d(TAG, "设置项改变,触发onChange()方法");
        if ("content://settings/secure/default_input_method".equals(uri.toString())) {
            if (checkAction()) {
//                如果选择的输入法是自身的输入法,那么直接就跳转到MainActivity,或者回到验证的Activity,但我这里没有做,因为我想返回Message给验证的Activity中的handler使用
//                Intent intent = new Intent(context, MainActivity.class);
//                Intent intent = new Intent(context, VerificationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                context.startActivity(intent);

//                android.os.Message是定义一个Message包含必要的描述和属性数据，并且此对象可以被发送给android.os.Handler处理。
//                属性字段：arg1、arg2、what、obj、replyTo等；其中arg1和arg2是用来存放整型数据的；what是用来保存消息标示的；obj是Object类型的任意对象；replyTo是消息管理器，会关联到一个handler，handler就是处理其中的消息。
//                通常对Message对象不是直接new出来的，只要调用handler中的obtainMessage方法来直接获得Message对象，也就是说这样创建message可以减少内存开销
//                获取消息
                Message message = handler.obtainMessage(1);
//                发送消息(这个时候注册了这个内容监听者的类(本例中指的是VerificationActivity类)中的Handler类型字段中的handleMessage()方法将会执行)
                handler.dispatchMessage(message);
            }
        } else {
            Log.d(TAG, "事件监听者报告：" + "刚刚有人启用了输入法");
        }
    }

    private boolean checkAction() {
        boolean isDefault = false;
        try {
            String curInputMethodId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            Log.d(TAG, "事件监听者得到了当前使用的输入法：" + curInputMethodId);
            if (curInputMethodId.equals(VerificationActivity.selfKeyBoardServiceId)) {
                isDefault = true;
            }
        } catch (Exception e) {
        }
        return isDefault;
    }
}
