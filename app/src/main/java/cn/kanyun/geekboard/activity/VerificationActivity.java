package cn.kanyun.geekboard.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kanyun.geekboard.R;

/**
 * 验证输入法是否启用/使用
 */
public class VerificationActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_verify);
        ButterKnife.bind(this);
//        获得软键盘管理器
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        当输入法第一次被装上以后,就已经出现在 getInputMethodList这个列表中
//        imm.getInputMethodList();
//        获得已启用/激活的键盘列表
        List<InputMethodInfo> inputMethodInfos = imm.getEnabledInputMethodList();
        Stream<InputMethodInfo> stream = inputMethodInfos.stream().filter(
                inputMethodInfo ->
                        "cn.kanyun.geekboard/.GeekBoardIME".equals(inputMethodInfo.getId()));
//        说明当前输入法已被启用/激活
        if (stream.count() > 0) {
            enableButtonIntro.setEnabled(false);
//            判断当前使用的输入法是否是本输入法
            InputMethodSubtype type = imm.getCurrentInputMethodSubtype();
            if (true) {
                switchButtonIntro.setEnabled(false);
            } else {
                switchButtonIntro.setEnabled(true);
            }
        } else {
//            输入法没有被激活,那么需要激活启用输入法按钮,禁用输入法切换按钮. 注意按钮默认是启用状态
            enableButtonIntro.setEnabled(true);
            switchButtonIntro.setEnabled(false);
        }
    }
}
