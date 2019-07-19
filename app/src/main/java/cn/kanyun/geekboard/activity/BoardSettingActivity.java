package cn.kanyun.geekboard.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.util.SPUtils;

/**
 * 键盘设置Activity
 */
public class BoardSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BoardSettingActivity";

    /**
     * 按键气泡列表
     */
    private static List<String> boardBubbleItems = new ArrayList() {{
        add("关闭");
        add("开启");
    }};

    /**
     * 按键震动列表
     */
    private static List<String> boardShockItems = new ArrayList() {{
        add("关闭");
        add("开启");
    }};

    /**
     * 按键声音列表
     */
    private static List<String> boardSoundItems = new ArrayList() {{
        add("关闭");
        add("开启");
    }};

    /**
     * 按键布局列表
     */
    private static List<String> boardLayoutItems = new ArrayList() {{
        add("Qwerty");
        add("Azerty");
    }};

    /**
     * 快捷键开关列表
     */
    private static List<String> boardQuickItems = new ArrayList() {{
        add("关闭");
        add("开启");
    }};

    /**
     * 设置项缓存
     */
    private static Map<String, String> config = new HashMap<>(5);

    Context context;

    @BindView(R.id.setting_tool_bar)
    androidx.appcompat.widget.Toolbar setToolBar;

    /**
     * 按键气泡设置
     */
    @BindView(R.id.board_bubble_set)
    LinearLayout boardBubbleSet;

    /**
     * 按键气泡设置
     */
    @BindView(R.id.board_sound_set)
    LinearLayout boardSoundSet;

    /**
     * 键盘布局设置
     */
    @BindView(R.id.board_layout_set)
    LinearLayout boardLayoutSet;

    /**
     * 按钮震动设置
     */
    @BindView(R.id.board_shock_set)
    LinearLayout boardShockSet;

    /**
     * 快捷键开关设置
     */
    @BindView(R.id.board_quick_set)
    ConstraintLayout boardQuickSet;

    @BindView(R.id.board_quick_set_switch)
    SwitchButton quickSwitchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_setting);
        context = this;
        ButterKnife.bind(this);
//        设置标题的字体颜色
        setToolBar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setToolBar.setElevation(30.0f);
//        设置子标题的字体颜色
//        setToolBar.setSubtitleTextColor(Color.WHITE);

//        设置完ToolBar后要调用setSupportActionBar()方法,否则将会出现两个toolbar,一个是自定义的,一个是系统自带的
//        当使用setSupportActionBar可能会报错：FEATURE_SUPPORT_ACTION_BAR
//        解决方法：https://www.cnblogs.com/hh9601/p/6404728.html
        setSupportActionBar(setToolBar);

//        需要注意的是该方法需要放在setSupportActionBar()方法后执行[导航点击监听->返回上个页面]
        setToolBar.setNavigationOnClickListener(v -> finish());

//        按钮配置点击监听需要先注册,否则onClick方法不生效
        boardLayoutSet.setOnClickListener(this);
        boardShockSet.setOnClickListener(this);
        boardBubbleSet.setOnClickListener(this);
        boardSoundSet.setOnClickListener(this);
        boardQuickSet.setOnClickListener(this);

        quickSwitchButton.setEnabled(false);
//        SwitchButton选中监听
//        quickSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });
    }


    @Nullable
    @Override
    public void onStart() {
        super.onStart();

//        获取SharedPreferences中保存的配置
        String board_layout = (String) SPUtils.get(context, Constant.BOARD_LAYOUT, "Qwerty");
        String board_sound = (String) SPUtils.get(context, Constant.BOARD_SOUND, "关闭");
        String board_shock = (String) SPUtils.get(context, Constant.BOARD_SHOCK, "关闭");
        String board_bubble = (String) SPUtils.get(context, Constant.BOARD_BUBBLE, "关闭");
        String board_quick = (String) SPUtils.get(context, Constant.BOARD_QUICK, "关闭");

//        将配置放到缓存中
        config.put(Constant.BOARD_LAYOUT, board_layout);
        config.put(Constant.BOARD_SOUND, board_sound);
        config.put(Constant.BOARD_SHOCK, board_shock);
        config.put(Constant.BOARD_BUBBLE, board_bubble);
        config.put(Constant.BOARD_QUICK, board_quick);

    }

    /**
     * onResume 方法
     * 当前重新回到活动状态(Running),重新回到当前activity界面发生
     * 或者说Activity重获焦点
     */
    @Override
    protected void onResume() {
        super.onResume();
//        设置气泡开关当前状态
        TextView bubbleText = (TextView) boardBubbleSet.getChildAt(1);
        bubbleText.setText(config.get(Constant.BOARD_BUBBLE));

//        设置震动开关当前状态
        TextView shockText = (TextView) boardShockSet.getChildAt(1);
        shockText.setText(config.get(Constant.BOARD_SHOCK));


//        设置声音开关当前状态
        TextView soundText = (TextView) boardSoundSet.getChildAt(1);
        soundText.setText(config.get(Constant.BOARD_SOUND));

//        设置布局当前状态
        TextView layoutText = (TextView) boardLayoutSet.getChildAt(1);
        layoutText.setText(config.get(Constant.BOARD_LAYOUT));

//        设置快捷键当前状态
        if (config.get(Constant.BOARD_QUICK).equals("开启")) {
            quickSwitchButton.setChecked(true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()方法执行,为暂停状态（Paused）,此时该Activity已失去了焦点[不包括弹出dialog]但仍然是可见的状态(包括部分可见)");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.board_bubble_set:
                showBoardBubbleSetDialog(v);
                break;
            case R.id.board_layout_set:
                showBoardLayoutSetDialog(v);
                break;
            case R.id.board_sound_set:
                showBoardSoundSetDialog(v);
                break;
            case R.id.board_quick_set:
                showBoardQuickDialog(v);
                break;
            default:
                showBoardShockSetDialog(v);


        }
    }


    /**
     * 按键气泡设置
     *
     * @param view
     */
    public void showBoardBubbleSetDialog(View view) {
//        获取缓存中的对象的索引值,在dialog中的radio组中进行预选择
        int index = 0;
        if (boardBubbleItems.contains(config.get(Constant.BOARD_BUBBLE))) {
            index = boardBubbleItems.indexOf(config.get(Constant.BOARD_BUBBLE));
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("按键气泡")
//                title颜色
                .titleColor(Color.WHITE)
//                radio组文字颜色
                .itemsColor(Color.WHITE)
//                 radio组小圆点颜色
                .choiceWidgetColor(ColorStateList.valueOf(Color.WHITE))
                .backgroundColor(Color.parseColor("#4B4B4B"))
                .positiveText("确认")
                .negativeText("取消")
                .items(boardBubbleItems)
//                该方法第一个参数,表示预选元素,如果不设置预选,则传入-1,否则传入大于-1的元素,那么在dialog在打开后会自动勾选一个选项
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                    //                    点击dialog确定按钮后回调方法
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Log.d(TAG, "用户点击了Radio中的某一项Item,且点击了确定按钮时,选中的值为：" + text);
                        modifyConfiguration(Constant.BOARD_BUBBLE, text.toString());
                        return true;
                    }
                });
//        默认的MaterialDialog在点击空白位置时候会自动关闭弹出框(默认为true),所以要设置不点击旁白关闭的话,需要设置为false
        builder.canceledOnTouchOutside(true);
        builder.show();

    }

    /**
     * 键盘布局设置
     *
     * @param view
     */
    public void showBoardLayoutSetDialog(View view) {
//        获取缓存中的对象的索引值,在dialog中的radio组中进行预选择
        int index = 0;
        if (boardLayoutItems.contains(config.get(Constant.BOARD_LAYOUT))) {
            index = boardLayoutItems.indexOf(config.get(Constant.BOARD_LAYOUT));
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("按键布局")
//                title颜色
                .titleColor(Color.WHITE)
//                radio组文字颜色
                .itemsColor(Color.WHITE)
//                 radio组小圆点颜色
                .choiceWidgetColor(ColorStateList.valueOf(Color.WHITE))
                .backgroundColor(Color.parseColor("#4B4B4B"))
                .positiveText("确认")
                .negativeText("取消")
                .items(boardLayoutItems)
//                该方法第一个参数,表示预选元素,如果不设置预选,则传入-1,否则传入大于-1的元素,那么在dialog在打开后会自动勾选一个选项
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                    //                    点击dialog确定按钮后回调方法
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Log.d(TAG, "用户点击了Radio中的某一项Item,且点击了确定按钮时,选中的值为：" + text);
                        modifyConfiguration(Constant.BOARD_LAYOUT, text.toString());
                        return true;
                    }
                });
        builder.show();

    }

    /**
     * 按键震动设置
     *
     * @param view
     */
    public void showBoardShockSetDialog(View view) {

//        获取缓存中的对象的索引值,在dialog中的radio组中进行预选择
        int index = 0;
        if (boardShockItems.contains(config.get(Constant.BOARD_SHOCK))) {
            index = boardShockItems.indexOf(config.get(Constant.BOARD_SHOCK));
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("按键震动")
//                title颜色
                .titleColor(Color.WHITE)
//                radio组文字颜色
                .itemsColor(Color.WHITE)
//                 radio组小圆点颜色
                .choiceWidgetColor(ColorStateList.valueOf(Color.WHITE))
                .backgroundColor(Color.parseColor("#4B4B4B"))
                .positiveText("确认")
                .negativeText("取消")
                .items(boardShockItems)
//                该方法第一个参数,表示预选元素,如果不设置预选,则传入-1,否则传入大于-1的元素,那么在dialog在打开后会自动勾选一个选项
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                    //                    点击dialog确定按钮后回调方法
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Log.d(TAG, "用户点击了Radio中的某一项Item,且点击了确定按钮时,选中的值为：" + text);
                        modifyConfiguration(Constant.BOARD_SHOCK, text.toString());
                        return true;
                    }
                });
        builder.show();
    }


    /**
     * 按键声音设置
     *
     * @param view
     */
    public void showBoardSoundSetDialog(View view) {

//       获取缓存中的对象的索引值,在dialog中的radio组中进行预选择
        int index = 0;
        if (boardSoundItems.contains(config.get(Constant.BOARD_SOUND))) {
            index = boardSoundItems.indexOf(config.get(Constant.BOARD_SOUND));
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("按键声音")
//                title颜色
                .titleColor(Color.WHITE)
//                radio组文字颜色
                .itemsColor(Color.WHITE)
//                 radio组小圆点颜色
                .choiceWidgetColor(ColorStateList.valueOf(Color.WHITE))
                .backgroundColor(Color.parseColor("#4B4B4B"))
                .positiveText("确认")
                .negativeText("取消")
                .items(boardSoundItems)
//                该方法第一个参数,表示预选元素,如果不设置预选,则传入-1,否则传入大于-1的元素,那么在dialog在打开后会自动勾选一个选项
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                    //                    点击dialog确定按钮后回调方法
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Log.d(TAG, "用户点击了Radio中的某一项Item,且点击了确定按钮时,选中的值为：" + text);
                        modifyConfiguration(Constant.BOARD_SOUND, text.toString());
                        return true;
                    }
                });
        builder.show();
    }

    /**
     * 快捷键开关设置
     *
     * @param v
     */
    private void showBoardQuickDialog(View view) {
        if (quickSwitchButton.isChecked()) {
            quickSwitchButton.setChecked(false);
            modifyConfiguration(Constant.BOARD_QUICK, "关闭");
        } else {
            quickSwitchButton.setChecked(true);
            modifyConfiguration(Constant.BOARD_QUICK, "开启");
        }

    }

    /**
     * 设置选项更改后,更改配置
     *
     * @param key
     * @param value
     */
    public void modifyConfiguration(String key, String value) {
        config.put(key, value);
        SPUtils.put(context, key, value);
        onResume();
    }
}
