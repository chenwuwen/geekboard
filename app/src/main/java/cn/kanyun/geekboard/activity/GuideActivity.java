package cn.kanyun.geekboard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import cn.kanyun.geekboard.fragment.GeekBoardIntro;
import cn.kanyun.geekboard.R;

/**
 * 使用向导
 * 启动页
 */
public class GuideActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(GeekBoardIntro.newInstance(R.layout.geekboard_intro1));
        addSlide(GeekBoardIntro.newInstance(R.layout.geekboard_intro2));
        addSlide(AppIntroFragment.newInstance("所有快捷方式!", "点击 'ctrl' 当 选择 全部, 剪切, 复制, 粘贴, 或者 撤销." +
                "\nCtrl+Shift+Z 回退" + "\n 长按空格键更改键盘 \n 更多的符号",
                R.drawable.intro3, Color.parseColor("#3F51B5")));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        setDoneText("完成");
        setSkipText("跳过");
        // Hide Skip/Done button.
       showSkipButton(true);
       // setProgressButtonEnabled(false);

//         Turn vibration on and set intensity.
//         NOTE: you will probably need to ask VIBRATE permission in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);
//        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
    public void enableButtonIntro(View v){

        Intent intent=new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
        startActivity(intent);
            }

    public void changeButtonIntro(View v){

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();}
}

