package cn.kanyun.geekboard.sync;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import cn.kanyun.geekboard.MyApplication;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.activity.GuideActivity;
import cn.kanyun.geekboard.entity.Skin;
import cn.kanyun.geekboard.gen.SkinDao;
import cn.kanyun.geekboard.util.SPUtils;

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
//          初始化 SharedPreferences(初始化生成的SharedPreferences文件名是：包名+应用名_preferences.xml 为了缩减SharedPreference文件数量,统一把配置放到一个SharedPreference文件中)
//        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
//      创建新的布尔值(boolean)和首选项(preference)并将其设置为true
//      getBoolean()参数:key检索，defValue：存在值就返回该值否则返回defValue，如果第一次使用getBoolean()通过key检索不到，直接返回 defValue
//        boolean isFirstStart = getPrefs.getBoolean("firstEnter", true);

        boolean isFirstStart = (boolean) SPUtils.get(activity.getBaseContext(), "firstEnter", true);

//       如果activity以前从未开始过(从来没有进入过该activity),那么进入使用向导activity
        if (isFirstStart) {

//            初始化皮肤数据
            initData();

//            Button change = activity.findViewById(R.id.change_button);
//            change.setVisibility(View.GONE);
//           转到使用向导activity
            Intent i = new Intent(activity, GuideActivity.class);
            activity.startActivity(i);

//         创建新的 SharedPreferences editor
//            SharedPreferences.Editor e = getPrefs.edit();

//         编辑SharedPreferences文件并且使变量boolean值为false,这样以后再次进入主界面就不会跳到使用向导了
//            e.putBoolean("firstEnter", false);

            SPUtils.put(activity.getBaseContext(), "firstEnter", false);

//          应用更改
//            e.apply();
        }
    }

    /**
     * 初始化数据库
     */
    public void initData() {
        MyApplication application = (MyApplication) MyApplication.getInstance();
        SkinDao skinDao = application.getDaoSession().getSkinDao();
        List<Skin> list = new ArrayList<>();
        Skin skin1 = new Skin();
        skin1.setName("material黑");
        skin1.setPreviewImg("drawable://" + R.drawable.intro3);
        skin1.setSkinImgXml(String.valueOf(R.layout.keyboard_material_dark));
        skin1.setEnable(true);

        Skin skin2 = new Skin();
        skin2.setName("material白");
        skin2.setPreviewImg("drawable://" + R.drawable.intro3);
        skin2.setSkinImgXml(String.valueOf(R.layout.keyboard_material_light));

        Skin skin3 = new Skin();
        skin3.setName("纯白");
        skin3.setPreviewImg("drawable://" + R.drawable.intro3);
        skin3.setSkinImgXml(String.valueOf(R.layout.keyboard_pure_white));

        Skin skin4 = new Skin();
        skin4.setName("蓝色");
        skin4.setPreviewImg("drawable://" + R.drawable.intro3);
        skin4.setSkinImgXml(String.valueOf(R.layout.keyboard_blue));

        Skin skin5 = new Skin();
        skin5.setName("紫色");
        skin5.setPreviewImg("drawable://" + R.drawable.intro3);
        skin5.setSkinImgXml(String.valueOf(R.layout.keyboard_purple));

        Skin skin6 = new Skin();
        skin6.setName("纯黑");
        skin6.setPreviewImg("drawable://" + R.drawable.intro3);
        skin6.setSkinImgXml(String.valueOf(R.layout.keyboard_pure_black));

        list.add(skin1);
        list.add(skin2);
        list.add(skin3);
        list.add(skin4);
        list.add(skin5);
        list.add(skin6);

        skinDao.insertInTx(list);


    }
}