package cn.kanyun.geekboard.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kanyun.geekboard.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * 分享弹窗使用 BasePopup
 * https://github.com/razerdp/BasePopup
 */
public class SharePopup extends BasePopupWindow implements View.OnClickListener {

    @BindView(R.id.qq)
    ImageView shareQQ;

    @BindView(R.id.qzone)
    ImageView shareQzone;

    @BindView(R.id.sina)
    ImageView shareSina;

    @BindView(R.id.wechat)
    ImageView shareWechat;

    /**
     * Popup的构造方法后于onCreateContentView方法执行
     * @param context
     */
    public SharePopup(Context context) {
        super(context);
//        在BasePopup里使用ButterKnife,可以在构造器中进行绑定
        ButterKnife.bind(this,getContentView());
//        绑定事件也在构造方法中执行
        shareQQ.setOnClickListener(this);
        shareWechat.setOnClickListener(this);
        shareQzone.setOnClickListener(this);
        shareSina.setOnClickListener(this);
    }

    /**
     * 必须实现，这里返回您的contentView
     * 为了让库更加准确的做出适配，强烈建议使用createPopupById()进行inflate
     * @return
     */
    @Override
    public View onCreateContentView() {
        View view = createPopupById(R.layout.popup_share);
        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qq:
                Toast.makeText(getContext(), "暂不支持分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.qzone:
                Toast.makeText(getContext(), "暂不支持分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sina:
                Toast.makeText(getContext(), "暂不支持分享", Toast.LENGTH_SHORT).show();
                break;
            default:
//                微信
                Toast.makeText(getContext(), "暂不支持分享", Toast.LENGTH_SHORT).show();
        }
    }
}
