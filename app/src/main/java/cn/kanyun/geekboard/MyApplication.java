package cn.kanyun.geekboard;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zhy.autolayout.config.AutoLayoutConifg;

/**
 * 自定义Application，需要配置在AndroidManifest.xml中的application name的属性上
 * 该类的作用是为了放一些全局的和一些上下文都要用到变量和方法之类的
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static Application getInstance(){
        if(mInstance == null){
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
    }
}
