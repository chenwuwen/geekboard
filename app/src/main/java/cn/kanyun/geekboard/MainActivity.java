package cn.kanyun.geekboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.FragmentUtils;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.kanyun.geekboard.adapter.MyViewPagerAdapter;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.fragment.SettingFragment;
import cn.kanyun.geekboard.fragment.SkinFragment;
import cn.kanyun.geekboard.listener.TabSwitchListener;
import cn.kanyun.geekboard.sync.CheckInit;


/**
 * 入口Activity
 * 在MainActivity中，实现Tab主要的技术就是利用FragmentTransaction，开启一个事务。
 * 在这个事务中，将我们的fragment加入进来，并嵌套在中间的布局FrameLayout上。
 * 然后通过事务控制隐藏和显示每一个fragment来达到切换的目的
 * 所以我们需要MainActivity去继承FragmentActivity 而不是传统的AppCompatActivity了
 * 注意：继承的类Fragment一定导入androidx.fragment.app.FragmentActivity;这个包，而不能是其他包下的
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    RadioGroup radioGroupColour, radioGroupLayout;
    SeekBar seekBar;
    Context context;


    /**
     * fragment
     */
    Fragment settingFragment;
    Fragment skinFragment;
    /**
     * tab按钮
     */
    ImageButton skinImgButton;
    ImageButton setImgButton;
    /**
     * 按钮文字布局
     */
    LinearLayout skinLayout;
    LinearLayout setLayout;

    /**
     * 事务管理器
     */
    FragmentManager fragmentManager = getSupportFragmentManager();
    /**
     * 事务
     */
    FragmentTransaction transaction;

    /**
     * TabLayout
     */
    TabLayout tabLayout;

    /**
     * 滑动布局
     */
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        skinFragment = new SkinFragment();
        settingFragment = new SettingFragment();

        viewPager = findViewById(R.id.view_pager_content);
//        创建ViewPagerAdapter
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
//        给viewPager赋值adapter
        viewPager.setAdapter(myViewPagerAdapter);
//        设置预加载页面数量的方法
        viewPager.setOffscreenPageLimit(2);
//        设置默认位于第一个
        viewPager.setCurrentItem(0);
//        初始化tabLayout
        tabLayout = findViewById(R.id.foot_tab_layout);
//        将TabLayout与Viewpager联动起来(需要注意的是,该方法需要添加在tabLayout添加tab的方法前面(添加tab的方法前 先remove所有tab),否则将找不到tab按钮和文字,因为该方法会移除以添加的tab)
        tabLayout.setupWithViewPager(viewPager);

//        默认的TabLayout布局
//        tabLayout.addTab(tabLayout.newTab().setText("选项一").setIcon(R.drawable.icon));
//        tabLayout.addTab(tabLayout.newTab().setText("选项二").setIcon(R.drawable.icon));

//        初始化tab(自定义TabLayout布局)
        initTab();



        //用来初始化数据控件
        initView();
        //初始化事件
//        initEvent();

//        由于使用了ViewPager来包装Fragment,故此处先进行注释
//        FragmentUtils.add(fragmentManager, skinFragment, R.id.main_fragment, false);
//        FragmentUtils.add(fragmentManager, settingFragment, R.id.main_fragment, true);

        //进入界面，先让其显示 第一个
//        setSelected(0);

//        默认显示第一个
        switchFragment(0);



        //  声明一个新线程以进行首选项检查[检查是否进入过主界面,如果没有进入过,则进入引导页]
        new Thread(new CheckInit(this)).start();

//        seekBar = findViewById(R.id.size_seekbar);
        // perform seek bar change listener event used for getting the progress value
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int progressChangedValue = seekBar.getProgress();
//
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                progressChangedValue = progress;
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
////                Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
////                        Toast.LENGTH_SHORT).show();
//                SavePreferences("SIZE", progressChangedValue);
//
//
//            }
//        });

//        radioGroupColour = findViewById(R.id.radiogroupcolour);
//        radioGroupColour.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerColour);
//
//        radioGroupLayout = findViewById(R.id.radiogrouplayout);
//        radioGroupLayout.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerLayout);


//        LoadPreferences();

    }

    /**
     * 初始化Tab
     * 自定义TabLayout布局
     */
    private void initTab() {
//        添加tab前移除全部tab
        tabLayout.removeAllTabs();
//        这里不使用HashMap,是因为HashMap是无序的(不是按照插入顺序),这样的话就会导致tab按钮顺序不一致
        Map<String, Integer> tabMap = new LinkedHashMap<>();
//        第一个tab按钮默认是选中的,所以图标是选中的图标
        tabMap.put("皮肤", R.drawable.foot_tab_btn_skin_light);
        tabMap.put("设置", R.drawable.foot_tab_btn_setting_dark);
        Iterator<Map.Entry<String, Integer>> it = tabMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();

            View inflate = View.inflate(this, R.layout.custom_tab_layout, null);
            TabLayout.Tab tab = tabLayout.newTab();
            TextView textView = inflate.findViewById(R.id.tab_name);
            textView.setText(entry.getKey());
            ImageButton imageButton = inflate.findViewById(R.id.tab_img_btn);
            imageButton.setImageResource(entry.getValue());
            tab.setCustomView(inflate);
            tabLayout.addTab(tab);
//            if (tabLayout.getSelectedTabPosition() == 0) {
////              设置第一个选中
//                tabLayout.getTabAt(0).getCustomView().setSelected(true);
//                imageButton.setImageResource(entry.getValue().re);
//            }
        }
//        添加监听器
        tabLayout.addOnTabSelectedListener(new TabSwitchListener());

    }

    /**
     * Fragment切换，使用了开源工具FragmentUtils
     *
     * @param i
     */
    private void switchFragment(int i) {
        switch (i) {
            case 1:
                FragmentUtils.showHide(settingFragment, skinFragment);
//                setImgButton.setImageResource(R.drawable.foot_tab_btn_setting_light);
                break;
            default:
                FragmentUtils.showHide(skinFragment, settingFragment);
//                设置图片按钮的图片(选中状态图片)
//                skinImgButton.setImageResource(R.drawable.foot_tab_btn_skin_light);
        }
    }

    /**
     * 弃用
     * 设定布局中间的FrameLayout的选择状态
     * 启用Fragment切换时可能产生空白页
     *
     * @param i
     */
    @Deprecated
    private void setSelected(int i) {
//        需要将按钮变亮，且需要切换fragment的状体
//        开启一个事务
        transaction = fragmentManager.beginTransaction();
//        自定义一个方法，来隐藏所有的fragment
        hideTransaction(transaction);
        switch (i) {
            case 0:
                if (skinFragment == null) {
//                    实例化每一个fragment
                    skinFragment = new SkinFragment();
//                    千万别忘记将该fragment加入到transaction中
//                    replace 是先remove再add，Fragment的onCreate方法都会被调用(如果在Fragment切换的过程中，不需要保留之前Fragment的状态，直接调用replace)
//                     同时replace每次切换的时候Fragment都会重新实列化，重新加载一次数据，这样做会非常消耗性能用用户的流量
//                    正确的切换方式是add()，切换时hide()，add()另一个Fragment；再次切换时，只需hide()当前，show()另一个。这样就能做到多个Fragment切换不重新实例化
//                      由于使用了ViewPager来包装Fragment,故此处先进行注释
//                    transaction.replace(R.id.main_fragment, skinFragment);

                }
                transaction.show(skinFragment);

//                设置图片按钮的图片(选中状态图片)
                skinImgButton.setImageResource(R.drawable.foot_tab_btn_skin_light);
                break;
            default:
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();

//                    由于使用了ViewPager来包装Fragment,故此处先进行注释
//                    transaction.replace(R.id.main_fragment, settingFragment);
                }
                transaction.show(settingFragment);
                setImgButton.setImageResource(R.drawable.foot_tab_btn_setting_light);
        }
//        最后千万别忘记提交事务
        transaction.commit();
    }

    /**
     * 隐藏fragment
     *
     * @param transaction
     */
    private void hideTransaction(FragmentTransaction transaction) {
        if (skinFragment != null) {
//            隐藏该fragment
            transaction.hide(skinFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
    }

    /**
     * 设定点击事件
     */
    private void initEvent() {
        setImgButton.setOnClickListener(this);
        skinImgButton.setOnClickListener(this);
    }

    /**
     * 用来初始化的方法
     */
    private void initView() {

        //获得按钮
        skinImgButton = findViewById(R.id.btn_skin);
        setImgButton = findViewById(R.id.btn_set);

        //获得底部的线性布局
        skinLayout = findViewById(R.id.lay_skin);
        setLayout = findViewById(R.id.lay_set);
    }


    RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListenerColour =
            new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    RadioButton checkedRadioButtonColour = (RadioButton) radioGroupColour.findViewById(checkedId);
                    int checkedIndexColour = radioGroupColour.indexOfChild(checkedRadioButtonColour);
                    SavePreferences(Constant.BOARD_LAYOUT, checkedIndexColour);

                }
            };

    RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListenerLayout =
            new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {


                    RadioButton checkedRadioButtonLayout = (RadioButton) radioGroupLayout.findViewById(checkedId);
                    int checkedIndexLayout = radioGroupLayout.indexOfChild(checkedRadioButtonLayout);
                    SavePreferences(Constant.BOARD_LAYOUT, checkedIndexLayout);

                }
            };


    /**
     * 保存配置到配置文件
     *
     * @param key
     * @param value
     */
    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public void changeButton(View v) {

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();

//        Button enable = (Button) findViewById(R.id.enable_button);
//        enable.setText("Change Keyboard");
//
//        String id = Settings.Secure.getString(
//                getContentResolver(),
//                Settings.Secure.DEFAULT_INPUT_METHOD
//        );
//
//        if(!(id.equals("cn.kanyun.geekboard/.GeekBoardIME"))){
//            InputMethodManager imm = (InputMethodManager)
//                    getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showInputMethodPicker();
//        }

    }

    public void previewToggle(View v) {
        CheckBox preview = findViewById(R.id.check_preview);
        if (preview.isChecked()) {
            SavePreferences("PREVIEW", 1);
        } else SavePreferences("PREVIEW", 0);
        closeKeyboard(v);

    }

    public void soundToggle(View v) {
        CheckBox preview = findViewById(R.id.check_sound);
        if (preview.isChecked()) {
            SavePreferences("SOUND", 1);
        } else SavePreferences("SOUND", 0);
        closeKeyboard(v);
    }

    /**
     * 振动开关
     *
     * @param v
     */
    public void vibratorToggle(View v) {
        CheckBox preview = findViewById(R.id.check_vibrator);
        if (preview.isChecked()) {
            SavePreferences("VIBRATE", 1);
        } else SavePreferences("VIBRATE", 0);
        closeKeyboard(v);
    }

    public void arrowToggle(View v) {
        CheckBox preview = findViewById(R.id.check_no_arrow);
        if (preview.isChecked()) {
            SavePreferences("ARROW_ROW", 0);
        } else SavePreferences("ARROW_ROW", 1);
        closeKeyboard(v);
    }


    /**
     * 关闭键盘布局
     *
     * @param v
     */
    public void closeKeyboard(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    /**
     * 加载键盘配置
     */
    private void LoadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);

        int savedRadioColour = sharedPreferences.getInt(Constant.BOARD_SKIN, 0);
        RadioButton savedCheckedRadioButtonColour = (RadioButton) radioGroupColour.getChildAt(savedRadioColour);
        savedCheckedRadioButtonColour.setChecked(true);

        int savedRadioLayout = sharedPreferences.getInt(Constant.BOARD_LAYOUT, 0);
        RadioButton savedCheckedRadioButtonLayout = (RadioButton) radioGroupLayout.getChildAt(savedRadioLayout);
        savedCheckedRadioButtonLayout.setChecked(true);

        int setPreview = sharedPreferences.getInt("PREVIEW", 0);
        int setSound = sharedPreferences.getInt("SOUND", 1);
        int setVibrator = sharedPreferences.getInt("VIBRATE", 1);
        int setSize = sharedPreferences.getInt("SIZE", 2);

        int setArrow = sharedPreferences.getInt("ARROW_ROW", 1);
        CheckBox preview = findViewById(R.id.check_preview);

        CheckBox sound = findViewById(R.id.check_sound);
//      按键振动
        CheckBox vibrate = findViewById(R.id.check_vibrator);
        CheckBox noArrow = findViewById(R.id.check_no_arrow);
        SeekBar size = findViewById(R.id.size_seekbar);

        if (setPreview == 1)
            preview.setChecked(true);
        else
            preview.setChecked(false);

        if (setSound == 1)
            sound.setChecked(true);
        else
            sound.setChecked(false);

        if (setVibrator == 1)
            vibrate.setChecked(true);
        else
            vibrate.setChecked(false);

        if (setArrow == 1)
            noArrow.setChecked(false);
        else
            noArrow.setChecked(true);

        size.setProgress(setSize);

    }


    /**
     * 监听点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
//        将按钮复位
        resetImgButton();
        switch (v.getId()) {
            case R.id.btn_skin:
//                setSelected(0);
                switchFragment(0);
                break;
            default:
//                setSelected(1);
                switchFragment(1);
        }
    }

    /**
     * 复位按钮，即设置按钮为未选中图片
     */
    private void resetImgButton() {
        skinImgButton.setImageResource(R.drawable.foot_tab_btn_skin_dark);
        setImgButton.setImageResource(R.drawable.foot_tab_btn_setting_dark);
    }

    /**
     * 页面可见时,就尝试申请权限
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        int hasWriteExternalStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (hasWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//        }
//    }


    /**
     * 尽管ImageLoader很强大，但一直把图片缓存在内存中，会导致内存占用过高。
     * 虽然对图片的引用是软引用，软引用在内存不够的时候会被GC，但我们还是希望减少GC的次数，
     * 所以要经常手动清理ImageLoader中的缓存。
     * 我们在上面MainActivity的 onDestroy()生命周期方法中，执行了ImageLoader的clearMemoryCache方法，
     * 以确保页面销毁时，把为了显示这个页面而增加的内存缓存清除。
     * 这样，即使到了下个页面要复用之前加载过的图片，虽然内存中没有了，根据ImageLoader的缓存策略，
     * 还是可以在本地磁盘上找到
     */
    @Override
    protected void onDestroy() {
        ImageLoader.getInstance().clearMemoryCache();
        super.onDestroy();
    }
}






