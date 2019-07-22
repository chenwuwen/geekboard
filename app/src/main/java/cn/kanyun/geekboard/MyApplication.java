package cn.kanyun.geekboard;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zhy.autolayout.config.AutoLayoutConifg;

import cn.kanyun.geekboard.entity.Skin;
import cn.kanyun.geekboard.gen.DaoMaster;
import cn.kanyun.geekboard.gen.DaoSession;

/**
 * 自定义Application，需要配置在AndroidManifest.xml中的application name的属性上
 * 该类的作用是为了放一些全局的和一些上下文都要用到变量和方法之类的
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static Application getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
//        AutoLayoutConifg库来实现自适应,当有些尺寸只能使用dp或px时,使用该库可以根据当前设备进行适配,需要在AndroidManifest.xml中配置meta-data,否则会报错
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);

//        ImageLoader的三大组件：
//        ImageLoaderConfiguration——对图片缓存进行总体配置，包挎内存缓存的大小、本地缓存的大小和位置、日志、下载策略（FIFO还是LIFO）等等。
//        ImageLoader——我们一般使用displayImage来把URL对应的图片显示在ImageView上。
//        ImageLoaderOptions——在每个页面需要显示图片的地方，控制如何显示的细节，比如指定下载时的默认图（包括下载中、下载失败、URL为空等），是否将缓存放到内存或者本地磁盘。

//        创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
//        初始化ImageLoad和配置
        ImageLoader.getInstance().init(configuration);
        setDatabase();
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "geek_board_db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
