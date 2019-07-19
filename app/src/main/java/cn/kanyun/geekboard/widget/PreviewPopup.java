package cn.kanyun.geekboard.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import cn.kanyun.geekboard.R;
import razerdp.basepopup.BasePopup;
import razerdp.basepopup.BasePopupWindow;

/**
 * 皮肤预览 选择皮肤后 弹出一个窗口(带输入框) 可以预览皮肤启用后的效果 BasePopup
 * https://github.com/razerdp/BasePopup
 */
public class PreviewPopup extends BasePopupWindow {

    public PreviewPopup(Context context) {
        super(context);
    }


    /**
     * 必须实现，这里返回您的contentView
     * 为了让库更加准确的做出适配，强烈建议使用createPopupById()进行inflate
     * @return
     */
    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_preview);
    }

    /**
     * 以下为可选代码（非必须实现）返回作用于PopupWindow的show和dismiss动画，
     * 本库提供了默认的几款动画，这里可以自由实现
     * @return
     */
    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }


}
