package cn.kanyun.geekboard.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.kanyun.geekboard.activity.FeedBackActivity;
import cn.kanyun.geekboard.activity.GuideActivity;
import cn.kanyun.geekboard.R;

/**
 * 设置Fragment
 */
public class SettingFragment extends BaseFragment {

    Context context;

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
//                Toast.makeText(context, "当前连接的是WIFI",Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(context, "当前连接的是GPRS",Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(context, FeedBackActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(context, "当前没有网络连接", Toast.LENGTH_SHORT).show();
        }
    }
}
