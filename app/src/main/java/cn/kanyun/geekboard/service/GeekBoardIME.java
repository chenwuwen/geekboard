package cn.kanyun.geekboard.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioAttributes;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.media.MediaPlayer; // for keypress sound


import com.blankj.utilcode.util.KeyboardUtils;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import cn.kanyun.geekboard.MyApplication;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.entity.Skin;
import cn.kanyun.geekboard.gen.SkinDao;
import cn.kanyun.geekboard.util.SPUtils;

import static android.view.KeyEvent.KEYCODE_CTRL_LEFT;
import static android.view.KeyEvent.KEYCODE_SHIFT_LEFT;
import static android.view.KeyEvent.META_CTRL_ON;
import static android.view.KeyEvent.META_SHIFT_ON;

/**
 * 首先IME是什么：IME 是Input Method Editor 的缩写
 * IME的生命周期：https://blog.csdn.net/le_go/article/details/9265119
 * IME的核心组件是一个继承InputMethodService的类。除了实现基本的服务的生命周期外，这个类还有一些回调函数提供给我们的IME的UI，
 * 用来处理用户输入，传递文本到当前焦点所在的Field。默认情况下，InputMethodService类提供了大部分实现来管理IME的可见性和与当前焦点坐在Field的联系
 */
public class GeekBoardIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    /**
     * 键盘视图类
     * View的一个拓展，渲染了一个键盘，响应用户的输入，键盘布局是可以用定义在XML文件中的Keyboard实例来指定的
     * 检测按键和触摸动作
     * 显示虚拟按键视图,处理呈现的按键并检测按键和触摸动作
     * 其包含一个嵌套类:KeyboardView.OnKeyboardActionListener
     */
    private KeyboardView kv;


    /**
     * 键盘按钮配置
     * 注意是：按钮[按键]
     */
    private Keyboard keyboard;

    /**
     * 描述文本编辑对象的几个属性:包含的文本内容类型和当前光标位置
     * 输入方法正在与（通常是EditText）通信
     */
    EditorInfo sEditorInfo;

    /**
     * 震动开关
     */
    private boolean vibratorOn;

    /**
     * 按键声音开关
     */
    private boolean soundOn;

    private Context context;

    private SkinDao skinDao;

    private MyApplication application;

    /**
     * Shift键是否锁定
     */
    private boolean shiftLock = false;
    private boolean shift = false;
    private boolean ctrl = false;
    private int mKeyboardState = R.integer.keyboard_normal;
    private int mLayout, mToprow, mSize;
    private Timer timerLongPress = null;
    private boolean switchedKeyboard = false;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        application = (MyApplication) MyApplication.getInstance();
        skinDao = application.getDaoSession().getSkinDao();
    }

    /**
     * Ctrl键的相关快捷键
     *
     * @param code
     * @param ic
     */
    public void onKeyCtrl(int code, InputConnection ic) {
        long now2 = System.currentTimeMillis();
        switch (code) {
            case 'a':
            case 'A':
                if (sEditorInfo.imeOptions == 1342177286) {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.selectAll);
                } else {
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A, 0, META_CTRL_ON));
                }
                break;
            case 'c':
            case 'C':
                if (sEditorInfo.imeOptions == 1342177286) {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.copy);
                } else {
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_C, 0, META_CTRL_ON));
                }
                break;
            case 'v':
            case 'V':
                if (sEditorInfo.imeOptions == 1342177286) {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.paste);
                } else {
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V, 0, META_CTRL_ON));
                }
                break;
            case 'x':
            case 'X':
                if (sEditorInfo.imeOptions == 1342177286) {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.cut);
                } else {
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_X, 0, META_CTRL_ON));
                }
                break;
            case 'z':
            case 'Z':
                if (shift) {
                    if (ic != null) {
                        if (sEditorInfo.imeOptions == 1342177286) {
                            getCurrentInputConnection().performContextMenuAction(android.R.id.redo);
                        } else {
                            ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, META_CTRL_ON | META_SHIFT_ON));
                        }

                        long nowS = System.currentTimeMillis();
                        shift = false;
                        ic.sendKeyEvent(new KeyEvent(nowS, nowS, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));

                        shiftLock = false;
                        shiftKeyUpdateView();
                    }
                } else {
                    //Log.e("ctrl", "z");
                    if (sEditorInfo.imeOptions == 1342177286) {
                        getCurrentInputConnection().performContextMenuAction(android.R.id.undo);
                    } else {
                        ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, META_CTRL_ON));
                    }

                }

                break;

            case 'b':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_B, 0, META_CTRL_ON));
                break;

            case 'd':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_D, 0, META_CTRL_ON));
                break;

            case 'e':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_E, 0, META_CTRL_ON));
                break;
            case 'f':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_F, 0, META_CTRL_ON));
                break;
            case 'g':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_G, 0, META_CTRL_ON));
                break;
            case 'h':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_H, 0, META_CTRL_ON));
                break;
            case 'i':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_I, 0, META_CTRL_ON));
                break;
            case 'j':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_J, 0, META_CTRL_ON));
                break;

            case 'k':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_K, 0, META_CTRL_ON));
                break;
            case 'l':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_L, 0, META_CTRL_ON));
                break;
            case 'm':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_M, 0, META_CTRL_ON));
                break;
            case 'n':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_N, 0, META_CTRL_ON));
                break;

            case 'o':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_O, 0, META_CTRL_ON));
                break;
            case 'p':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_P, 0, META_CTRL_ON));
                break;


            case 'q':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_P, 0, META_CTRL_ON));
                break;
            case 'r':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_R, 0, META_CTRL_ON));
                break;

            case 's':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_S, 0, META_CTRL_ON));
                break;

            case 't':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_T, 0, META_CTRL_ON));
                break;

            case 'u':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_U, 0, META_CTRL_ON));
                break;

            case 'w':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_W, 0, META_CTRL_ON));
                break;


            case 'y':
                ic.sendKeyEvent(new KeyEvent(
                        now2 + 1, now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_Y, 0, META_CTRL_ON));
                break;

            default:
                if (Character.isLetter(code) && shift) {
                    code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code), 1);
                    if (!shiftLock) {
                        long nowS = System.currentTimeMillis();
                        shift = false;
                        ic.sendKeyEvent(new KeyEvent(nowS, nowS, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));
                    }
                    shiftKeyUpdateView();
                }
                break;


        }
    }

    /**
     * 点击按键时触发
     * 只处理特殊按键,或自定义按键
     * 普通按键 交给系统函数运行 也就是switch中的default
     *
     * @param primaryCode
     * @param KeyCodes
     */
    @Override
    public void onKey(int primaryCode, int[] KeyCodes) {
//        InputConnection 是输入法和应用内View（通常是EditText）交互的通道，输入法的文本输入和删改事件，包括key event事件都是通过InputConnection发送给EditText
//        InputConnection接口是接收输入的应用程序与InputMethod间的通讯通道。它可以完成以下功能，如读取光标周围的文本，向文本框提交文本，向应用程序提交原始按键事件
        InputConnection ic = getCurrentInputConnection();
        keyboard = kv.getKeyboard();

        switch (primaryCode) {

            case 53737:
                getCurrentInputConnection().performContextMenuAction(android.R.id.selectAll);
                break;
            case 53738:
                getCurrentInputConnection().performContextMenuAction(android.R.id.cut);
                break;
            case 53739:
                getCurrentInputConnection().performContextMenuAction(android.R.id.copy);
                break;
            case 53740:
                getCurrentInputConnection().performContextMenuAction(android.R.id.paste);
                break;
            case 53741:
                getCurrentInputConnection().performContextMenuAction(android.R.id.undo);
                break;
            case 53742:
                getCurrentInputConnection().performContextMenuAction(android.R.id.redo);
                break;
            case Keyboard.KEYCODE_DELETE:
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
                break;

            case Keyboard.KEYCODE_DONE:
                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
                break;

            case 27: //Escape(Esc键)
                long now = System.currentTimeMillis();
                ic.sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON));
                break;

            case -13:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showInputMethodPicker();
                }
                break;

            case -15:  // 切换键
                if (kv != null) {
//                    mKeyboardState只有两种状态：0:普通布局状态 ,1:有快捷键状态
                    if (mKeyboardState == R.integer.keyboard_normal) {
                        //切换键盘布局,从普通布局切换到有快捷键的布局
                        Keyboard symbolKeyboard = chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_sym);
                        kv.setKeyboard(symbolKeyboard);
                        mKeyboardState = R.integer.keyboard_sym;
                    } else  {
                        //切换键盘布局,从有快捷键的布局切换到普通布局
                        Keyboard normalKeyboard = chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_normal);
                        kv.setKeyboard(normalKeyboard);
                        mKeyboardState = R.integer.keyboard_normal;
                    }
                    controlKeyUpdateView();
                    shiftKeyUpdateView();
                }

                break;

            case 17: //ctrl 键
                long nowCtrl = System.currentTimeMillis();
                if (ctrl) {
//                    长按Ctrl走这里
                    ic.sendKeyEvent(new KeyEvent(nowCtrl, nowCtrl, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));
                } else {
//                    短按Ctrl走这里
                    ic.sendKeyEvent(new KeyEvent(nowCtrl, nowCtrl, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));
                }

                ctrl = !ctrl;
                controlKeyUpdateView();
                break;

            case 16:  //Shift键
                long nowShift = System.currentTimeMillis();
                if (shift) {
//                    短按Shift走这里
                    ic.sendKeyEvent(new KeyEvent(nowShift, nowShift, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));
                } else {
//                    长按Shift走这里
                    ic.sendKeyEvent(new KeyEvent(nowShift, nowShift, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));
                }

                if (shiftLock) {
                    shift = true;
                    shiftKeyUpdateView();
                } else {
                    shift = !shift;
                    shiftKeyUpdateView();
                }

                break;

            case 9:  //Tab键盘
                sendDownUpKeyEvents(KeyEvent.KEYCODE_TAB);
                break;

            case 5000: //左方向键(自定义函数处理)
                handleArrow(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case 5001:  //下方向键 (交由系统函数处理)
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case 5002: //上方向键 (交由系统函数处理)
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP);
                break;
            case 5003: //右方向键(自定义函数处理)
                handleArrow(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;

            default:
                char code = (char) primaryCode;
                if (ctrl) {
                    onKeyCtrl(code, ic);
                    if (!shiftLock) {
                        long nowS = System.currentTimeMillis();
                        shift = false;
                        ic.sendKeyEvent(new KeyEvent(nowS, nowS, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));

                        shiftKeyUpdateView();
                    }
                    ctrl = false;
                    controlKeyUpdateView();
                } else if (Character.isLetter(code) && shift) {
                    code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code), 1);
                    if (!shiftLock) {

                        long nowS = System.currentTimeMillis();
                        shift = false;
                        ic.sendKeyEvent(new KeyEvent(nowS, nowS, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));

                    }

                    shiftKeyUpdateView();
                } else {
                    if (!switchedKeyboard) {
                        ic.commitText(String.valueOf(code), 1);
                    }
                    switchedKeyboard = false;
                }
        }

    }

    @Override
    public void onPress(final int primaryCode) {

        if (soundOn) {
//            声音控制
            MediaPlayer keyPressSoundPlayer = MediaPlayer.create(this, R.raw.keypress_sound);
//            设置声音
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setLegacyStreamType(2)
                    .setUsage(AudioAttributes.USAGE_MEDIA);
            AudioAttributes audio = builder.build();
            keyPressSoundPlayer.setAudioAttributes(audio);
            keyPressSoundPlayer.start();
            keyPressSoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
        if (vibratorOn) {
//            震动控制
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null)
                vibrator.vibrate(40);
        }
        if (timerLongPress != null) {
//            终止Timer任务,不影响正在执行的任务
            timerLongPress.cancel();
        }

        timerLongPress = new Timer();

        timerLongPress.schedule(new TimerTask() {

            @Override
            public void run() {

                try {

                    Handler uiHandler = new Handler(Looper.getMainLooper());

                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {

                            try {

                                GeekBoardIME.this.onKeyLongPress(primaryCode);

                            } catch (Exception e) {
                                Logger.e("uiHandler.run: " + e.getMessage(), e);
                            }

                        }
                    };

                    uiHandler.post(runnable);

                } catch (Exception e) {
                    Logger.e("Timer.run: " + e.getMessage(), e);
                }
            }

        }, ViewConfiguration.getLongPressTimeout());

    }

    /**
     * 释放按键事件
     * 该事件是在onKey事件结束后调用
     * 对于重复的键只调用一次
     *
     * @param primaryCode
     */
    @Override
    public void onRelease(int primaryCode) {
        if (timerLongPress != null)
            timerLongPress.cancel();

    }

    /**
     * 长按事件
     * 比如长按 shift 将全部输入大写字符
     *
     * @param keyCode
     */
    public void onKeyLongPress(int keyCode) {
//         长按Shift键,将启用大写锁定(或者解锁大写锁定)
        if (keyCode == 16) {
            shiftLock = !shiftLock;
        }

//        长按空格键切换成其他输入法
        if (keyCode == 32) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showInputMethodPicker();
            switchedKeyboard = true;
        }

//        长按产生振动
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(50);
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (text.toString().contains("for")) {
            ic.commitText(text, 1);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);

        } else {
            ic.commitText(text, 1);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
        }
    }

    /**
     * 手指从上往下滑动，关闭键盘
     */
    @Override
    public void swipeDown() {
        Logger.d("手指在键盘上,使用下滑手势操作");
//        closing方法并不是关闭键盘的方法,他清除键盘缓存,即在键盘中使用的位图图像
//        kv.closing();
        View view = kv.getRootView();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        IBinder binder = view.getApplicationWindowToken();
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//        imm.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 左滑
     */
    @Override
    public void swipeLeft() {
        Logger.d("手指在键盘上,使用左滑手势操作");
    }

    /**
     * 右滑动
     */
    @Override
    public void swipeRight() {
        Logger.d("手指在键盘上,使用右滑手势操作");
    }

    /**
     * 上滑动
     */
    @Override
    public void swipeUp() {
        Logger.d("手指在键盘上,使用上滑手势操作");
    }

    /**
     * 切换键盘(将普通键盘布局切换到带有快捷键的键盘)
     *
     * @param layout 键盘布局(QWERTY布局/AZERTY布局)
     * @param toprow 快捷键状态
     * @param size   键盘尺寸
     * @param mode   模式(当前键盘状态,是否是在快捷键页面(mode为1),还是普通字符页面(mode为0),这个mode的改变时点击切换键时改变的)
     * @return
     */
    public Keyboard chooseKB(int layout, int toprow, int size, int mode) {
        Keyboard keyboard;
        if (layout == 0) {

            if (toprow == 1) {

                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.qwerty0r, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.qwerty1r, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.qwerty2r, mode);
                } else {
                    keyboard = new Keyboard(this, R.xml.qwerty3r, mode);
                }
            } else {

                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.qwerty0e, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.qwerty1e, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.qwerty2e, mode);
                } else keyboard = new Keyboard(this, R.xml.qwerty3e, mode);
            }
        } else {
            if (toprow == 1) {
                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.azerty0r, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.azerty1r, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.azerty2r, mode);
                } else {
                    keyboard = new Keyboard(this, R.xml.azerty3r, mode);
                }
            } else {
                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.azerty0e, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.azerty1e, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.azerty2e, mode);
                } else {
                    keyboard = new Keyboard(this, R.xml.azerty3e, mode);
                }
            }
        }
        return keyboard;
    }


    /**
     * 该函数中创建键盘视图
     *
     * @return
     */
    @Override
    public View onCreateInputView() {

        Long id = (Long) SPUtils.get(context, Constant.BOARD_SKIN, 0L);

        Skin skin;

        if (id != 0) {
            skin = skinDao.load(id.longValue());
        } else {
            skin = skinDao.loadByRowId(1);
        }

        String xml = skin.getSkinImgXml();
        int resourceId = 0;

//        这里先进行转换,如果能转换成int不报错,则说明数据库中存放的是本地的资源Id,否则需要手动获取resource对象
        try {
            resourceId = Integer.parseInt(xml);
        } catch (Exception e) {

        }
//        选择键盘皮肤
//        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_material_dark, null);
        kv = (KeyboardView) getLayoutInflater().inflate(resourceId, null);

//        按键气泡(按键气泡其实就是按键回显)
        if (SPUtils.get(context, Constant.BOARD_BUBBLE, "关闭").equals("开启")) {
            kv.setPreviewEnabled(true);
        } else {
            kv.setPreviewEnabled(false);
        }
//         声音
        if (SPUtils.get(context, Constant.BOARD_SOUND, "关闭").equals("开启")) {
            soundOn = true;
        } else {
            soundOn = false;
        }

//        震动
        if (SPUtils.get(context, Constant.BOARD_SHOCK, "关闭").equals("开启")) {
            vibratorOn = true;
        } else {
            vibratorOn = false;
        }

        shift = false;
        ctrl = false;

//        键盘布局
        if (SPUtils.get(context, Constant.BOARD_LAYOUT, "Qwerty").equals("Qwerty")) {
            mLayout = 0;
        } else {
            mLayout = 1;
        }
//        键盘尺寸
        mSize = (int) SPUtils.get(context, Constant.BOARD_SIZE, 2);
//        快捷键
        if (SPUtils.get(context, Constant.BOARD_QUICK, "关闭").equals("开启")) {
            mToprow = 0;
        } else {
            mToprow = 1;
        }
        mKeyboardState = R.integer.keyboard_normal;

        Keyboard keyboard = chooseKB(mLayout, mToprow, mSize, mKeyboardState);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);


        return kv;
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        setInputView(onCreateInputView());
        sEditorInfo = attribute;

    }

    /**
     * 改变ctrl键的显示,由小写变大写,或者由大写变小写
     */
    public void controlKeyUpdateView() {
        keyboard = kv.getKeyboard();
        int i;
        List<Keyboard.Key> keys = keyboard.getKeys();
        for (i = 0; i < keys.size(); i++) {
            if (ctrl) {
                if (keys.get(i).label != null && keys.get(i).label.equals("Ctrl")) {
                    keys.get(i).label = "CTRL";
                    break;
                }
            } else {
                if (keys.get(i).label != null && keys.get(i).label.equals("CTRL")) {
                    keys.get(i).label = "Ctrl";
                    break;
                }
            }
        }
        kv.invalidateKey(i);
    }

    /**
     * 改变Shift键的表现形式,由大写变成小写或者由小写变成大写
     */
    public void shiftKeyUpdateView() {

        keyboard = kv.getKeyboard();
        List<Keyboard.Key> keys = keyboard.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (shift) {
                if (keys.get(i).label != null && keys.get(i).label.equals("Shift")) {
                    keys.get(i).label = "SHIFT";
                    break;
                }
            } else {
                if (keys.get(i).label != null && keys.get(i).label.equals("SHIFT")) {
                    keys.get(i).label = "Shift";
                    break;
                }
            }
        }
        keyboard.setShifted(shift);
        kv.invalidateAllKeys();
    }

    /**
     * 处理方向键按钮
     * 仅处理 左右方向键
     *
     * @param keyCode
     */
    public void handleArrow(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        Long now2 = System.currentTimeMillis();
        if (ctrl && shift) {
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, KEYCODE_CTRL_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));
//            当shift和ctrl都为true时,点击左右方向键,会选中从当前光标处到最左或最右的所有字符
            moveSelection(keyCode);
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_UP, KEYCODE_CTRL_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));
        } else if (shift) {
//            当shift为true时,点击左右方向键,会选中当前光标处左或者右处的一个字符
            moveSelection(keyCode);
        } else if (ctrl) {
//            当ctrl为true时,点击左右方向键,光标会移动到最左/右处
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_CTRL_ON));
        } else {
            sendDownUpKeyEvents(keyCode);
        }
    }

    /**
     * 移动选中
     * 在Shift键为true时 触发，且当前触发的按键为左右方向键
     *
     * @param keyCode
     */
    private void moveSelection(int keyCode) {
//        inputMethodService.sendDownKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
//        inputMethodService.sendDownAndUpKeyEvent(dpad_keyCode, 0);
//        inputMethodService.sendUpKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
        InputConnection ic = getCurrentInputConnection();
        Long now2 = System.currentTimeMillis();
        ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));
        if (ctrl) {
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON | META_CTRL_ON));
        } else {
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON));
        }
        ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_UP, KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));


    }

}


