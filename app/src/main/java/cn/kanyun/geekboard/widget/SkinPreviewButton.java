package cn.kanyun.geekboard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import cn.kanyun.geekboard.R;

/**
 * https://www.jianshu.com/p/5776362ee74e
 * 自定义皮肤预览ImageButton
 */
public class SkinPreviewButton extends LinearLayout {

    private ImageView buttonImage;
    private TextView buttonText;

    /**
     * ImageLoader展示图片的工具
     */
    public static DisplayImageOptions options;

    static {

//        加载本地图片，不缓存，不存储
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(false).cacheInMemory(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .build();
    }

    /**
     * 只能在代码中动态的添加
     *
     * @param context
     */
    public SkinPreviewButton(Context context) {
        super(context);
    }

    /**
     * 可以在代码和xml中都可以用
     * @param context
     */
    public SkinPreviewButton(Context context, AttributeSet attrs) {
        super(context,attrs);
//        加载布局
        LayoutInflater.from(context).inflate(R.layout.skin_preview_image_button, this, true);
        buttonImage = findViewById(R.id.skin_preview);
        buttonText = findViewById(R.id.skin_name);

        buttonImage.setPadding(0, 0, 0, 0);

        setTextColor(0xFF000000);
        buttonText.setPadding(0, 0, 0, 0);

        //设置本布局的属性

        //可点击
        setClickable(true);
        //可聚焦
        setFocusable(true);

        //布局才用普通按钮的背景(默认是灰色背景)
//        setBackgroundResource(android.R.drawable.btn_default);

        //垂直布局
        setOrientation(LinearLayout.VERTICAL);
        //首先添加Image，然后才添加Text
        //添加顺序将会影响布局效果
//        addView(buttonImage);
//        addView(buttonText);

        initData(context,attrs);
    }

    /**
     * 初始化数据
     * AttributeSet是xml文件中元素属性的一个集合。其中提供了各种Api，供我们从已编译好的xml文件获取属性值
     * https://blog.csdn.net/boyeleven/article/details/82790738
     * @param attrs
     */
    public void initData(Context context, AttributeSet attrs) {
//        skin_preview_button 的值是attrs.xml中declare-styleable定义的名字
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.skin_preview_button);
//        这个参数,是declare-styleable中的name+自定义的name
        String name = typedArray.getString(R.styleable.skin_preview_button_skin_name);
        int imageId = typedArray.getResourceId(R.styleable.skin_preview_button_img_preview, 0);
        setImageResource(imageId);
        setText(name);
//        在 Android 自定义 View 的时候，需要使用 TypedArray 来获取 XML layout 中的属性值，使用完之后，需要调用 recyle() 方法将 TypedArray 回收
//        https://blog.csdn.net/Monicabg/article/details/45014327
        typedArray.recycle();
    }

    /**
     * 设置图片资源
     */
    public void setImageResource(int resId) {
        buttonImage.setImageResource(resId);
    }

    /**
     * 设置图片资源
     */
    public void setImageResource(Bitmap bm) {
        buttonImage.setImageBitmap(bm);
    }

    /**
     * 设置图片资源
     */
    public void setImageResource(String uri) {
//            如果以Uri开头的路径则是自带的图片
        if (uri.startsWith("drawable")) {
//          此方法需要注意：取到的可能有空,因为涉及IO操作(包括网络IO)需要开线程，4.0以后的这个方法获取为空,可以通过添加option获取
            Bitmap bm = ImageLoader.getInstance().loadImageSync(uri, options);
            buttonImage.setImageBitmap(bm);
        }
    }

    /**
     * 设置显示的文字
     */
    public void setText(String text) {
        buttonText.setText(text);
    }


    /**
     * setTextColor方法
     */
    public void setTextColor(int color) {
        buttonText.setTextColor(color);
    }

}
