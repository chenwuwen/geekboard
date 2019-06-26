package cn.kanyun.geekboard.fragment;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import cn.kanyun.geekboard.MyApplication;

/**
 * 在使用Fragment的时候经常需要传递Context类型的参数，而Fragment自己又不是一个Context类型的对象，
 * 于是我们有下面两种方法获取Context对象
 * 1.this.getActivity()（可以不写this.）
 * 在Fragment中直接调用getActivity()方法，可以直接得到Fragment依附的Activity，而Activity是一个Context类型的对象。
 * 2.获取Application对象
 * 如下自己写一个MyApplication类继承Application，通过getInstance获取一个Application类型的对象，也是Context对象。
 *
 * https://blog.csdn.net/suyan_why/article/details/52574197
 */
public class BaseFragment extends Fragment {
    private Activity activity;

    public Context getContext(){
        if(activity == null){
            return MyApplication.getInstance();
        }
        return activity;
    }

    /**
     * 现在 Android 开发多使用一个 Activity 管理多个 Fragment 进行开发，不免需要两者相互传递数据，
     * 一般是给 Fragment 添加回调接口，让 Activity 继承并实现。回调接口一般都写在 Fragment 的onAttach()方法中
     *
     * onAttach 有两个重载的方法：
     * onAttach(Context context)
     * onAttach(Activity activity) 【过时】
     * SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
     * 建议使用FragmentV4版的，减少不必要的系统自身Bug的处理 ,当然本项目已经升级到androidX了,所以就使用新API了
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }
}

