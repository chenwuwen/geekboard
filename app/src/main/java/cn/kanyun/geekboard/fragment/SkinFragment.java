package cn.kanyun.geekboard.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.kanyun.geekboard.R;

/**
 * 皮肤Fragment
 */
public class SkinFragment extends BaseFragment {

    Context context;


    /**
     * 系统会在Fragment首次绘制其用户界面时调用此方法。 要想为Fragment绘制 UI，
     * 从该方法中返回的 View 必须是Fragment布局的根视图。如果Fragment未提供 UI，您可以返回 null。
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.skin_fragment, container, false);
    }

    /**
     * 系统会在创建Fragment时调用此方法。可以初始化一段资源文件等等
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
