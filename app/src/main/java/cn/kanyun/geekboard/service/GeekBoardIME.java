package cn.kanyun.geekboard.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.util.SPUtils;

import static android.view.KeyEvent.KEYCODE_CTRL_LEFT;
import static android.view.KeyEvent.KEYCODE_SHIFT_LEFT;
import static android.view.KeyEvent.META_CTRL_ON;
import static android.view.KeyEvent.META_SHIFT_ON;

public class GeekBoardIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    /**
     * 键盘视图类
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

    EditorInfo sEditorInfo;

    /**
     * 震动开关
     */
    private boolean vibratorOn;

    /**
     * 按键声音开关
     */
    private boolean soundOn;
    private boolean shiftLock = false;
    private boolean shift = false;
    private boolean ctrl = false;
    private int mKeyboardState = R.integer.keyboard_normal;
    private int mLayout, mToprow, mSize;
    private Timer timerLongPress = null;
    private boolean switchedKeyboard = false;


    public void onKeyCtrl(int code, InputConnection ic) {
        long now2 = System.currentTimeMillis();
        switch (code) {
            case 'a':
            case 'A':
                if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.selectAll);
                } else
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A, 0, META_CTRL_ON));
                break;
            case 'c':
            case 'C':
                if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.copy);
                } else
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_C, 0, META_CTRL_ON));
                break;
            case 'v':
            case 'V':
                if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.paste);
                } else
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V, 0, META_CTRL_ON));
                break;
            case 'x':
            case 'X':
                if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                {
                    getCurrentInputConnection().performContextMenuAction(android.R.id.cut);
                } else
                    ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_X, 0, META_CTRL_ON));
                break;
            case 'z':
            case 'Z':
                if (shift) {
                    if (ic != null) {
                        if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                        {
                            getCurrentInputConnection().performContextMenuAction(android.R.id.redo);
                        } else
                            ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, META_CTRL_ON | META_SHIFT_ON));

                        long nowS = System.currentTimeMillis();
                        shift = false;
                        ic.sendKeyEvent(new KeyEvent(nowS, nowS, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));

                        shiftLock = false;
                        shiftKeyUpdateView();
                    }
                } else {
                    //Log.e("ctrl", "z");
                    if (sEditorInfo.imeOptions == 1342177286)//fix for DroidEdit
                    {
                        getCurrentInputConnection().performContextMenuAction(android.R.id.undo);
                    } else
                        ic.sendKeyEvent(new KeyEvent(now2 + 1, now2 + 1, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, META_CTRL_ON));

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

                        //Log.e("GeekBoardIME", "Unshifted b/c no lock");
                    }
                    shiftKeyUpdateView();
                }
                break;


        }
    }

    @Override
    public void onKey(int primaryCode, int[] KeyCodes) {


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

            case 27:
                //Escape
                long now = System.currentTimeMillis();
                ic.sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE, 0, KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON));

                break;

            case -13:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showInputMethodPicker();
                break;
            case -15:
                if (kv != null) {
                    if (mKeyboardState == R.integer.keyboard_normal) {
                        //change to symbol keyboard_material_dark
                        Keyboard symbolKeyboard = chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_sym);

                        kv.setKeyboard(symbolKeyboard);

                        mKeyboardState = R.integer.keyboard_sym;
                    } else if (mKeyboardState == R.integer.keyboard_sym) {
                        //change to normal keyboard_material_dark
                        Keyboard normalKeyboard = chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_normal);

                        kv.setKeyboard(normalKeyboard);
                        mKeyboardState = R.integer.keyboard_normal;
                    }
                    controlKeyUpdateView();
                    shiftKeyUpdateView();

                }

                break;

            case 17:
//              ctrl key
                long nowCtrl = System.currentTimeMillis();
                if (ctrl)
                    ic.sendKeyEvent(new KeyEvent(nowCtrl, nowCtrl, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));
                else
                    ic.sendKeyEvent(new KeyEvent(nowCtrl, nowCtrl, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT, 0, META_CTRL_ON));

                ctrl = !ctrl;
                controlKeyUpdateView();
                break;

            case 16:
                // Log.e("GeekBoardIME", "onKey" + Boolean.toString(shiftLock));
                //Shift - runs after long press, so shiftlock may have just been activated
                long nowShift = System.currentTimeMillis();
                if (shift)
                    ic.sendKeyEvent(new KeyEvent(nowShift, nowShift, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));
                else
                    ic.sendKeyEvent(new KeyEvent(nowShift, nowShift, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON));

                if (shiftLock) {
                    shift = true;
                    shiftKeyUpdateView();
                } else {
                    shift = !shift;
                    shiftKeyUpdateView();
                }

                break;

            case 9:
                //tab
                // ic.commitText("\u0009", 1);
                sendDownUpKeyEvents(KeyEvent.KEYCODE_TAB);
                break;

            case 5000:
                handleArrow(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case 5001:
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case 5002:
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP);
                break;
            case 5003:
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

                        //Log.e("GeekBoardIME", "Unshifted b/c no lock");
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
        if (timerLongPress != null)
            timerLongPress.cancel();

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
                                Log.e(GeekBoardIME.class.getSimpleName(), "uiHandler.run: " + e.getMessage(), e);
                            }

                        }
                    };

                    uiHandler.post(runnable);

                } catch (Exception e) {
                    Log.e(GeekBoardIME.class.getSimpleName(), "Timer.run: " + e.getMessage(), e);
                }
            }

        }, ViewConfiguration.getLongPressTimeout());

    }

    @Override
    public void onRelease(int primaryCode) {
        if (timerLongPress != null)
            timerLongPress.cancel();

    }

    public void onKeyLongPress(int keyCode) {
        // Process long-click here
        if (keyCode == 16) {
            shiftLock = !shiftLock;
            //Log.e("GeekBoardIME", "long press" + Boolean.toString(shiftLock));
            //and onKey will now happen
        }

        if (keyCode == 32) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showInputMethodPicker();
            switchedKeyboard = true;
        }

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

    @Override
    public void swipeDown() {

        kv.closing();

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {

    }

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
                } else keyboard = new Keyboard(this, R.xml.qwerty3r, mode);
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
                } else keyboard = new Keyboard(this, R.xml.azerty3r, mode);
            } else {
                if (size == 0) {
                    keyboard = new Keyboard(this, R.xml.azerty0e, mode);
                } else if (size == 1) {
                    keyboard = new Keyboard(this, R.xml.azerty1e, mode);
                } else if (size == 2) {
                    keyboard = new Keyboard(this, R.xml.azerty2e, mode);
                } else keyboard = new Keyboard(this, R.xml.azerty3e, mode);
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

        SharedPreferences pre = getSharedPreferences(SPUtils.FILE_NAME, MODE_PRIVATE);
//        键盘皮肤
        switch (pre.getString(Constant.BOARD_SKIN, "material黑")) {
            case "material黑":
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_material_dark, null);
                break;
            case "material白":
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_material_light, null);
                break;
            case "纯黑":
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_pure_black, null);
                break;
            case "纯白":
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_pure_white, null);
                break;
            case "蓝":
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_blue, null);
                break;
            case "紫":
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_purple, null);
                break;

            default:
                kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_material_dark, null);
                break;

        }

//        按键气泡
        if (pre.getString(Constant.BOARD_BUBBLE, "关闭").equals("开启")) {
            kv.setPreviewEnabled(true);
        } else {
            kv.setPreviewEnabled(false);
        }
//         声音
        if (pre.getString(Constant.BOARD_SOUND, "关闭").equals("开启")) {
            soundOn = true;
        } else {
            soundOn = false;
        }

//        震动
        if (pre.getString(Constant.BOARD_SHOCK, "关闭").equals("开启")) {
            vibratorOn = true;
        } else {
            vibratorOn = false;
        }

        shift = false;
        ctrl = false;

//        键盘布局
        mLayout = pre.getInt("RADIO_INDEX_LAYOUT", 0);
//        键盘尺寸
        mSize = pre.getInt("SIZE", 2);
//        快捷键
        if (pre.getString(Constant.BOARD_QUICK, "关闭").equals("开启")) {
            mToprow = 0;
        } else {
            mToprow = 1;
        }
        mKeyboardState = R.integer.keyboard_normal;
        //reset to normal

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

    public void shiftKeyUpdateView() {

        keyboard = kv.getKeyboard();
        List<Keyboard.Key> keys = keyboard.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (shift) {
                if (keys.get(i).label != null && keys.get(i).label.equals("Shft")) {
                    keys.get(i).label = "SHFT";
                    break;
                }
            } else {
                if (keys.get(i).label != null && keys.get(i).label.equals("SHFT")) {
                    keys.get(i).label = "Shft";
                    break;
                }
            }
        }
        keyboard.setShifted(shift);
        kv.invalidateAllKeys();
    }

    public void handleArrow(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        Long now2 = System.currentTimeMillis();
        if (ctrl && shift) {
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, KEYCODE_CTRL_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));
            moveSelection(keyCode);
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_UP, KEYCODE_CTRL_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));

        } else if (shift)
            moveSelection(keyCode);
        else if (ctrl)
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_CTRL_ON));
        else {
            sendDownUpKeyEvents(keyCode);
        }
    }

    private void moveSelection(int keyCode) {
//        inputMethodService.sendDownKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
//        inputMethodService.sendDownAndUpKeyEvent(dpad_keyCode, 0);
//        inputMethodService.sendUpKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
        InputConnection ic = getCurrentInputConnection();
        Long now2 = System.currentTimeMillis();
        ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));
        if (ctrl)
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON | META_CTRL_ON));

        else
            ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON));
        ic.sendKeyEvent(new KeyEvent(now2, now2, KeyEvent.ACTION_UP, KEYCODE_SHIFT_LEFT, 0, META_SHIFT_ON | META_CTRL_ON));


    }
}


