package cn.kanyun.geekboard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.List;
import java.util.stream.Stream;

import cn.kanyun.geekboard.fragment.GeekBoardIntro;
import cn.kanyun.geekboard.R;

/**
 * 使用向导
 * 启动页
 */
public class GuideActivity extends AppIntro {

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        // 注意：不要使用setContentView();

        // AppIntro将自动生成点指示器和按钮。

//        添加slide页:这里直接使用AppIntro默认的slide,只需要添加一些参数即可
        addSlide(AppIntroFragment.newInstance("所有快捷方式!", null, "点击 'ctrl' 当 选择 全部, 剪切, 复制, 粘贴, 或者 撤销." +
                        "\nCtrl+Shift+Z 回退" + "\n 长按空格键更改键盘 \n 更多的符号",
                null, R.drawable.intro3, Color.parseColor("#3F51B5"), Color.WHITE, Color.WHITE));
        // 添加自定义slide页
        Fragment fragment = GeekBoardIntro.newInstance(R.layout.geekboard_intro_1);
        addSlide(fragment);


//        ButterKnife.bind(this);

//         一些选项方法
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        设置下划线的颜色
//        setSeparatorColor(Color.parseColor("#2196F3"));

//        设置作者默认封装好的动画效果
        setFadeAnimation();
//        setZoomAnimation();
//        setFlowAnimation();
//        setSlideOverAnimation();
//        setDepthAnimation();

//        更改下一步按钮
//        setImageNextButton(R.drawable.next_step);

//        设置文字及颜色
        setDoneText("完成");
        setSkipText("跳过");
        setColorDoneText(Color.parseColor("#23A0FF"));
        setColorSkipButton(Color.parseColor("#23A0FF"));
//        显示/隐藏跳过按钮
        showSkipButton(true);
        // setProgressButtonEnabled(false);

//         打开振动并设置强度：需要在清单中请求振动权限
//        setVibrate(true);
//        setVibrateIntensity(30);
//        setFadeAnimation();
    }

    /**
     * 用户点击跳过按钮
     *
     * @param currentFragment
     */
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // 当用户点击“跳过”按钮时做一些事情。
        finish();
    }

    /**
     * 用户点击完成按钮
     *
     * @param currentFragment
     */
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
//         当用户点击 "完成" 按钮时执行某些操作
//        finish();
//        这边判断是否启用和选择了输入法,如果都成功了,那么进入主界面,否则进入VerificationActivity
        Intent intent = new Intent(context,VerificationActivity.class);
        startActivity(intent);

    }

    /**
     * 当slide改变时做一些事情
     *
     * @param oldFragment
     * @param newFragment
     */
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // 做一些事情，当slide改变时,在这里我判断第二页的按钮的禁用和启用【由于此处无法使用findViewById因此无法使用】
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


    /**
     * 验证键盘状态
     *
     * @return
     */
    private boolean verifyBoardStatus() {

//      获得软键盘管理器
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        当输入法第一次被装上以后,就已经出现在 getInputMethodList这个列表中
//        imm.getInputMethodList();
//        获得已启用/激活的键盘列表
        List<InputMethodInfo> inputMethodInfos = imm.getEnabledInputMethodList();
        Stream<InputMethodInfo> stream = inputMethodInfos.stream().filter(
                inputMethodInfo ->
                        "cn.kanyun.geekboard/.GeekBoardIME".equals(inputMethodInfo.getId()));
        if (stream.count() > 0) {
            return true;
        }
        return false;
    }
}

