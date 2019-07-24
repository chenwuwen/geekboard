package cn.kanyun.geekboard.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.AppUtils;
import com.google.android.material.snackbar.Snackbar;

import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.activity.BoardSettingActivity;
import cn.kanyun.geekboard.activity.FeedBackActivity;
import cn.kanyun.geekboard.activity.GuideActivity;
import cn.kanyun.geekboard.sync.CheckVersion;
import cn.kanyun.geekboard.widget.SharePopup;
import top.androidman.SuperButton;

/**
 * 设置Fragment
 */
public class SettingFragment extends BaseFragment {

    private static final String TAG = "SettingFragment";
    Context context;

    /**
     * 事务
     */
    FragmentTransaction transaction;

    public static Fragment newInstance() {
        return new SettingFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.setting_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Fragment无法使用onClick来处理点击事件,只能将点击事件放在onActivityCreated中
//        原因在于：Fragment不是布局器，不具备渲染视图的能力，虽然可以管理布局器，但它管理的布局器最终要加载到一个ViewGroup对象内，由ViewGroup对象来渲染，而ViewGroup并不知道每一个子控件来源于哪里。

        SuperButton shareButton = getActivity().findViewById(R.id.share);

        SuperButton updateButton = getActivity().findViewById(R.id.check_update);

        SuperButton aboutButton = getActivity().findViewById(R.id.about_me);

        SuperButton boardSetButton = getActivity().findViewById(R.id.board_set);

        SuperButton introButton = getActivity().findViewById(R.id.intro);

        SuperButton feedbackButton = getActivity().findViewById(R.id.feedback);

//        分享
        shareButton.setOnClickListener(v -> share(v));
//        更新版本
        updateButton.setOnClickListener(v -> updateVersion(v));
//        关于我
        aboutButton.setOnClickListener(v -> aboutMe(v));
//        进入键盘设置界面
        boardSetButton.setOnClickListener(v -> boardSetting(v));
//        App启动页
        introButton.setOnClickListener(v -> openTutorial(v));
//        意见反馈
        feedbackButton.setOnClickListener(v -> feedback(v));
    }

    /**
     * 跳到键盘设置页面
     *
     * @param v
     */
    private void boardSetting(View v) {
        Intent intent = new Intent(context, BoardSettingActivity.class);
        startActivity(intent);
    }

    /**
     * 版本更新
     *
     * @param v
     */
    private void updateVersion(View v) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title("检查更新")
                .content("正在检查新版本....")
                .progress(true, 0);

//        配置监听(注意顺序,需要放在builder.show()之前)
        builder.showListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "dialog弹起监听");
            }
        });
            builder.dismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG, "dialog关闭");
            }
        });
        builder.show();

//        调用ASync任务,检查新版本
        CheckVersion checkVersion = new CheckVersion(context);
        checkVersion.execute(getActivity());

    }

    /**
     * 打开应用市场
     *
     * @param v
     */
    public void openPlay(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("market://details?id=cn.kanyun.geekboard"));
        startActivity(i);
    }

    /**
     * 打开个人主页
     *
     * @param v
     */
    public void openUserHome(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/chenwuwen"));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "未安装相关应用程序", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 使用教程
     *
     * @param v
     */
    public void openTutorial(View v) {
        Intent i = new Intent(context, GuideActivity.class);
        startActivity(i);
    }

    /**
     * 意见反馈
     *
     * @param v
     */
    public void feedback(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        通过是否为NULL判断网络
//        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
//        NetworkInfo gprsInfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

        NetworkInfo activeNetWork = connectivityManager.getActiveNetworkInfo();
        if (activeNetWork != null) {
            if (activeNetWork.getType() == connectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "当前连接的是WIFI", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "当前连接的是GPRS", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(context, FeedBackActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(context, "当前没有网络连接", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 打开分享Popup
     * 展示PopupWindow的方法有三种:
     * 分别是showPopupWindow()、showPopupWindow(View anchor)和showPopupWindow(int x, int y)
     * 这三个方法有不同的含义：
     * showPopupWindow()：无参传入，此时PopupWindow参考对象为屏幕（或者说整个DecorView），
     * Gravity的表现就像在FrameLayout里面的Gravity表现一样，表示其处于屏幕的哪个方位
     * showPopupWindow(View anchor)：传入AnchorView，此时PopupWindow参考对象为传入的anchorView，
     * Gravity的表现则意味着这个PopupWindow应该处于目标AnchorView的哪个方位
     * showPopupWindow(int x, int y)：传入位置信息，此时PopupWindow将会在指定位置弹出。
     * https://github.com/razerdp/BasePopup
     */
    private void share(View view) {
        new SharePopup(context).showPopupWindow();
    }

    /**
     * 关于我
     * Snackbar:https://blog.csdn.net/lhy349/article/details/81096093
     */
    private void aboutMe(View view) {
        Snackbar snackbar = Snackbar.make(view, "这是一个snackbar", Snackbar.LENGTH_SHORT)
                .setAction("关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(view, "已关闭", Snackbar.LENGTH_SHORT).show();
                    }
                });

//        动态设置文本显示内容
        snackbar.setText("GeekBoard\n当前版本：" + AppUtils.getAppVersionName());

        //动态设置Action文本的颜色
        snackbar.setActionTextColor(Color.BLACK);
        //动态设置显示时间
        snackbar.setDuration(5000);
        //获取Snackbar显示的View对象
        View snackBarView = snackbar.getView();

        TextView snackbarMsgTextView = snackBarView.findViewById(R.id.snackbar_text);

//        设置Snackbar提示信息文字的颜色
        snackbarMsgTextView.setTextColor(Color.BLACK);

        //获取显示文本View(不包括Action),并设置其背景颜色
//        snackBarView.findViewById(R.id.snackbar_text).setBackgroundColor(Color.BLACK);
        //获取Action文本View，并设置其背景颜色
//        snackBarView.findViewById(R.id.snackbar_action).setBackgroundColor(Color.BLACK);
        //设置SnackBar的背景色（改配置是上面两行配置的集合）
        snackBarView.setBackgroundColor(Color.parseColor("#dbdbdb"));

        //设置SnackBar显示的位置
        ViewGroup.LayoutParams params = snackBarView.getLayoutParams();
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(params.width, params.height);
        //垂直居中
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
//        水平居中
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        snackBarView.setLayoutParams(layoutParams);
        snackBarView.setAnimation(new AnimationSet(true));

//        一定要调用show方法,与Toast一样
        snackbar.show();

    }
}
