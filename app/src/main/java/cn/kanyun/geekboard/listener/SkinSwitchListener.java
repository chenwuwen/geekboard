package cn.kanyun.geekboard.listener;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.broadcast.SkinChangeReceiver;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.util.SPUtils;
import cn.kanyun.geekboard.widget.PreviewPopup;
import cn.kanyun.geekboard.widget.SkinPreviewButton;


/**
 * 监听皮肤被选择
 */
public class SkinSwitchListener implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "SkinSwitchListener";

    /**
     * 上次被点击的 position
     * 这里设置为public是因为,当Fragment初始化时需要从SharedPrefrences中
     * 读取之前设置position,并将其设置到lastIndex中
     */
    public static int lastIndex = 0;


    /**
     * 皮肤名
     */
    String skinName;

    /**
     * 当前被点击的 position
     */
    int position;


    public SkinSwitchListener(String skinName, int position) {
        this.skinName = skinName;
        this.position = position;
    }


    @Override
    public void onClick(View v) {
        Log.i(TAG, "点击了皮肤:" + skinName);
        SkinPreviewButton button = (SkinPreviewButton) v;
        ViewParent parent = button.getParent();
//        ViewParent是view的父类
        View view = (View) parent;
        ConstraintLayout layout = (ConstraintLayout) view;
//        获得隐藏的ImageView
        ImageView enable = layout.findViewById(R.id.skin_enable);
//        将隐藏的ImageView给显示出来
        enable.setVisibility((View.VISIBLE));
        Log.i(TAG, parent.getLayoutDirection() + "");
//        设置当前被点击的position为lastIndex
        lastIndex = position;
//        将键盘皮肤名称保存到SharedPreferences中
        SPUtils.put(v.getContext(), Constant.BOARD_SKIN, skinName);
//           点击皮肤后,直接预览皮肤,之前想的是直接在此处写弹出poup的代码,想了想,这样写不够优雅,因此使用广播来解耦
        Intent intent = new Intent(Constant.BOARD_SKIN_SWITCH);
//          需要注意的是这里发送的广播使用的是Context上下文发送的,所以:在注册广播接受者时,也要使用上下文Context来注册,而不能使用LocalBroadcastManager本地广播
        v.getContext().sendBroadcast(intent);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //按钮按下抬起事件
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.i(TAG, "索引为：" + position + "皮肤预览按钮被按下随后抬起");
            ViewParent parent = v.getParent().getParent();
            RecyclerView recyclerView = (RecyclerView) parent;
//            获得上次被点击的item
            ConstraintLayout layout = (ConstraintLayout) recyclerView.getChildAt(lastIndex);
            if (layout != null) {
//            获得隐藏的ImageView
                ImageView enable = layout.findViewById(R.id.skin_enable);
//            将显示的ImageView给隐藏出来
                enable.setVisibility((View.GONE));
            }


        }
        return false;
    }
}
