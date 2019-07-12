package cn.kanyun.geekboard.listener;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.widget.TabButton;


/**
 * TabLayout tab切换监听
 */
public class TabSwitchListener implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "TabSwitchListener";

    /**
     * 选中了tab的逻辑
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
//        得到布局
        LinearLayout linearLayout = (LinearLayout) tab.getCustomView();
        Log.i(TAG, "当前布局下子布局个数为：" + linearLayout.getChildCount());
        ImageButton imageButton = (ImageButton) linearLayout.getChildAt(0);
        if (tab.getPosition() == 0) {
            imageButton.setImageResource(R.drawable.foot_tab_btn_skin_light);
        }else {
            imageButton.setImageResource(R.drawable.foot_tab_btn_setting_light);
        }
        Log.i(TAG, "onTabSelected 执行");

    }

    /**
     * 未选中tab的逻辑
     *
     * @param tab
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        LinearLayout linearLayout = (LinearLayout) tab.getCustomView();
        Log.i(TAG, "当前布局下子布局个数为：" + linearLayout.getChildCount());
        ImageButton imageButton = (ImageButton) linearLayout.getChildAt(0);
        if (tab.getPosition() == 0) {
            imageButton.setImageResource(R.drawable.foot_tab_btn_skin_dark);
        }else {
            imageButton.setImageResource(R.drawable.foot_tab_btn_setting_dark);
        }
        Log.i(TAG, "onTabUnselected 执行");
    }

    /**
     * 再次选中tab的逻辑
     *
     * @param tab
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.i(TAG, "onTabReselected 执行");

    }
}
