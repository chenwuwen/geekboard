package cn.kanyun.geekboard.sync;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.activity.GuideActivity;

/**
 * 检测是否是首次进入主页面
 */
public class CheckInit implements Runnable {

    private Activity activity;

    public CheckInit(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
//          初始化 SharedPreferences
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
//      创建新的布尔值(boolean)和首选项(preference)并将其设置为true
//      getBoolean()参数:key检索，defValue：存在值就返回该值否则返回defValue，如果第一次使用getBoolean()通过key检索不到，直接返回 defValue
        boolean isFirstStart = getPrefs.getBoolean("firstEnter", true);

//       如果activity以前从未开始过(从来没有进入过该activity),那么进入使用向导activity
        if (isFirstStart) {
            Button change = activity.findViewById(R.id.change_button);
            change.setVisibility(View.GONE);
//           转到使用向导activity
            Intent i = new Intent(activity, GuideActivity.class);
            activity.startActivity(i);

//         创建新的 SharedPreferences editor
            SharedPreferences.Editor e = getPrefs.edit();

//         编辑SharedPreferences文件并且使变量boolean值为false,这样以后再次进入主界面就不会跳到使用向导了
            e.putBoolean("firstEnter", false);

//          应用更改
            e.apply();
        }
    }
}