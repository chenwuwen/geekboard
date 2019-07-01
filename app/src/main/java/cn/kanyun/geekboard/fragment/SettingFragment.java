package cn.kanyun.geekboard.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.blankj.utilcode.util.AppUtils;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import cn.kanyun.geekboard.activity.FeedBackActivity;
import cn.kanyun.geekboard.activity.GuideActivity;
import cn.kanyun.geekboard.R;

/**
 * 设置Fragment
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    Context context;

    @BindView(R.id.share)
    Button shareButton;

    @BindView(R.id.check_update)
    Button updateButton;

    @BindView(R.id.about_me)
    Button aboutButton;

    @BindView(R.id.board_set)
    Button boardSetButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.setting_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.board_set:
//                进入键盘设置界面
                break;
            case R.id.check_update:
//                检测更新
                break;
            case R.id.share:
//                分享
                break;
            default:
//                关于
                aboutMe();
        }
    }

    /**
     * 关于我
     * Snackbar:https://blog.csdn.net/lhy349/article/details/81096093
     */
    private void aboutMe() {
        Snackbar snackbar = Snackbar.make(null, "这是一个snackbar", Snackbar.LENGTH_SHORT)
                .setAction("关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(null, "点击了关闭按钮", Snackbar.LENGTH_SHORT).show();
                    }
                });
//        动态设置文本显示内容

        snackbar.setText("GeekBoard\n" + AppUtils.getAppVersionCode());
        //动态设置Action文本的颜色
        snackbar.setActionTextColor(Color.RED);
        //动态设置显示时间
        snackbar.setDuration(5000);
        View snackbarView = snackbar.getView();//获取Snackbar显示的View对象
        //获取显示文本View,并设置其显示颜色
//        snackbarView.findViewById(R.id.snackbar_text).setTextColor(Color.BLUE);
        //获取Action文本View，并设置其显示颜色
//        snackbarView.findViewById(R.id.snackbar_action).setTextColor(Color.BLUE);
        //设置SnackBar的背景色
        snackbarView.setBackgroundColor(Color.GREEN);

        //设置SnackBar显示的位置
        ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(params.width, params.height);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;//垂直居中
        snackbarView.setLayoutParams(layoutParams);

//        一定要调用show方法,与Toast一样
        snackbar.show();

    }
}
