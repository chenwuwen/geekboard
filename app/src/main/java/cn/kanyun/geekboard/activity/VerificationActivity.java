package cn.kanyun.geekboard.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kanyun.geekboard.MainActivity;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.broadcast.InputMethodChangeReceiver;
import cn.kanyun.geekboard.fragment.SettingFragment;
import cn.kanyun.geekboard.observer.SettingValueChangeContentObserver;

/**
 * 验证输入法是否启用/使用
 */
public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = "VerificationActivity";

    /**
     * 当前软件键盘服务名称：这个名称不是随便起的
     * 包名 + / + 类名
     * 注意找其中的规律
     */
    public static String selfKeyBoardServiceId = "cn.kanyun.geekboard/.service.GeekBoardIME";


    /**
     * 启用键盘按钮
     */
    @BindView(R.id.enable_button_intro)
    Button enableButtonIntro;

    /**
     * 切换键盘按钮
     */
    @BindView(R.id.switch_button_intro)
    Button switchButtonIntro;


    /**
     * 切换键盘按钮
     */
    @BindView(R.id.skip_button)
    Button skipButtonIntro;

    Context context;

    /**
     * 输入法改变广播接受者
     */
    private InputMethodChangeReceiver inputMethodChangeReceiver;

    /**
     * 定义意图过滤器
     */
    private IntentFilter intentFilter;

    /**
     * 设置内容改变消息接受者
     */
    private SettingValueChangeContentObserver settingValueChangeContentObserver;



    /**
     * 本地广播
     * 注意：使用本地广播并没有静态注册的方法，
     * 因为静态注册主要是为了让程序在未启动的情况下也能收到广播，
     * 而发动本地广播的时候，我们的程序已经是启动了，自然是没有静态注册这个方法
     * 静态注册指的就是 在AndroidManifest.xml中注册
     */
    private LocalBroadcastManager localBroadcastManager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_verify);
        ButterKnife.bind(this);
        context = this;


        skipButtonIntro.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

//        注册输入法激活观察者 (handler为构建UI线程的handler)
        settingValueChangeContentObserver = new SettingValueChangeContentObserver(handler, context);
//        第一个参数是Uri:表示我去监听哪个Uri的内容改变
//        第二个参数：为 false 表示精确匹配，即只匹配该 Uri。为 true 表示可以同时匹配其派生的 Uri
//        第三个参数：ContentObserver 的实例
//        可以注册多个Uri(每行一个)
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ENABLED_INPUT_METHODS), false, settingValueChangeContentObserver);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.DEFAULT_INPUT_METHOD), false, settingValueChangeContentObserver);

    }

    @Override
    protected void onStart() {
        super.onStart();
        inputMethodChangeReceiver = new InputMethodChangeReceiver();
        intentFilter = new IntentFilter();
//        这个方法的参数是想要接收的广播类型值
//        android.intent.action.INPUT_METHOD_CHANGED:在任何应用中,改变输入法的时候被调用
        intentFilter.addAction("android.intent.action.INPUT_METHOD_CHANGED");
//        intentFilter.addCategory();
//        全局广播：存在有数据安全的问题
//        registerReceiver(inputMethodChangeReceiver, intentFilter);

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
//        当发生键盘改变时将发送广播
//        本地广播:只能在本地应用程序中发送与接收广播，可以起到保护数据安全的作用
        localBroadcastManager.registerReceiver(inputMethodChangeReceiver, intentFilter);

//      指明要发送的广播值
        Intent sendIntent = new Intent("键盘改变了");
//        发送标准广播
        sendBroadcast(sendIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        获得软键盘管理器
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        当输入法第一次被装上以后,就已经出现在 getInputMethodList这个列表中
//        imm.getInputMethodList();
//        获得已启用/激活的键盘列表
        List<InputMethodInfo> inputMethodInfos = imm.getEnabledInputMethodList();
        Stream<InputMethodInfo> stream = inputMethodInfos.stream().filter(
                inputMethodInfo ->
                        selfKeyBoardServiceId.equals(inputMethodInfo.getId()));
//        说明当前输入法已被启用/激活
        if (stream.count() > 0) {
            enableButtonIntro.setEnabled(false);
//            判断当前使用的输入法是否是本输入法
            String curInputMethodId = Settings.Secure.getString(VerificationActivity.this.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            Log.d(TAG, "VerificationActivity的onResume()方法表示->当前使用的输入法是：" + curInputMethodId);
            if (curInputMethodId.equals(selfKeyBoardServiceId)) {
                switchButtonIntro.setEnabled(false);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                switchButtonIntro.setEnabled(true);
            }
        } else {
//            输入法没有被激活,那么需要激活启用输入法按钮,禁用输入法切换按钮. 注意按钮默认是启用状态
            enableButtonIntro.setEnabled(true);
            switchButtonIntro.setEnabled(false);
        }

    }

    /**
     * 启用键盘,此时只是将键盘,加上了键盘列表中
     *
     * @param v
     */
    public void enableButtonIntro(View v) {
        Intent intent = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
        startActivity(intent);
    }

    /**
     * 显示输入法菜单列表
     * 即 让用户选择使用哪个输入法 列表
     *
     * @param v
     */
    public void switchButtonIntro(View v) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();

    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy方法执行");
        super.onDestroy();
//        需要注意的是，动态注册的广播接收器一定要注销，在onDestroy方法中调用unregisterReceiver(recevier);
        localBroadcastManager.unregisterReceiver(inputMethodChangeReceiver);

//        取消注册内容观察者
        getContentResolver().unregisterContentObserver(settingValueChangeContentObserver);
    }


    /**
     * 更新UI线程
     * 非UI线程更新UI时，报错SecurityException
     */
    private Handler handler = new Handler() {
//        能走这个方法就表示,已经勾选了当前输入法,可以直接做一些事情了,因为我在监听者那写的是,如果符合就返回Message,否则什么都不做
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "内容观察者返回结果是：" + msg);
            switchButtonIntro.setEnabled(false);
//                跳转到MainActivity
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);

        }
    };


}

