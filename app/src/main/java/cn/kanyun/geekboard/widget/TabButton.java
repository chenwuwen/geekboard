package cn.kanyun.geekboard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.kanyun.geekboard.R;

public class TabButton extends LinearLayout {

    private ImageButton tabImageButton;
    private TextView tabText;

    public TabButton(Context context) {
        super(context);
    }

    public TabButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //        加载布局
        LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, this, true);
        tabImageButton = findViewById(R.id.tab_img_btn);
        tabText = findViewById(R.id.tab_name);

        //设置本布局的属性

        //可点击
        setClickable(true);
        //可聚焦
        setFocusable(true);
        //布局才用普通按钮的背景(灰色)
        setBackgroundResource(android.R.drawable.btn_default);
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custom_tab_button);
//        这个参数,是declare-styleable中的name+自定义的name
        String name = typedArray.getString(R.styleable.custom_tab_button_tab_name);
        int imageId = typedArray.getResourceId(R.styleable.custom_tab_button_tab_img_btn, 0);
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
        tabImageButton.setImageResource(resId);
    }

    /**
     * 设置图片资源
     */
    public void setImageResource(Bitmap bm) {
        tabImageButton.setImageBitmap(bm);
    }

    /**
     * 设置显示的文字
     */
    public void setText(String text) {
        tabText.setText(text);
    }
}
