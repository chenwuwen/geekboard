package cn.kanyun.geekboard.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kanyun.geekboard.R;
import razerdp.basepopup.BasePopup;
import razerdp.basepopup.BasePopupWindow;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 皮肤预览 选择皮肤后 弹出一个窗口(带输入框) 可以预览皮肤启用后的效果 BasePopup
 * https://github.com/razerdp/BasePopup
 */
public class PreviewPopup extends BasePopupWindow {

    Context context;

    public PreviewPopup(Context context) {
        super(context);
        this.context = context;
        ButterKnife.bind(this, getContentView());
        Button button = findViewById(R.id.screenshot);
//        将截图按钮隐藏掉
        button.setVisibility(View.GONE);
//        如果是Debug包设置截图按键可见,如果是Release包则隐藏该按钮
        ApplicationInfo info = context.getApplicationInfo();
        if ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            Logger.d("当前应用时DEBUG模式的应用,将显示截图按钮");
            button.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 必须实现，这里返回您的contentView
     * 为了让库更加准确的做出适配，强烈建议使用createPopupById()进行inflate
     *
     * @return
     */
    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_preview);
    }

    /**
     * 以下为可选代码（非必须实现）返回作用于PopupWindow的show和dismiss动画，
     * 本库提供了默认的几款动画，这里可以自由实现
     *
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


    @OnClick(R.id.screenshot)
    public void screenshot(View v) {
        Logger.d("截取键盘");
        View view = getContentView();
        View pview =v.getRootView();
        int id3 = pview.getId();
        int id1 = view.getId();
        int id2 = v.getId();
        Logger.d("id1：" + id1 + "  id2:" + id2);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);


    }

    private void viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cacheBmp = loadBitmapFromView(view);

        FileOutputStream fos;
        String imagePath = "";
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录
                File sdRoot = Environment.getExternalStorageDirectory();
                File file = new File(sdRoot, LocalDateTime.now() + ".png");
                fos = new FileOutputStream(file);
                imagePath = file.getAbsolutePath();
            } else {
                throw new Exception("创建文件失败!");
            }
            cacheBmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.e("imagePath=" + imagePath);

        view.destroyDrawingCache();
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

}
