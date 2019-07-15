package cn.kanyun.geekboard.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.adapter.SkinPreviewAdapter;
import cn.kanyun.geekboard.entity.Skin;

/**
 * 皮肤Fragment
 */
public class SkinFragment extends BaseFragment {

    Context context;

    RecyclerView skinPreView;

    RecyclerView.Adapter adapter;

    /**
     * 数据源
     */
    private List<Skin> list;

    /**
     * ImageLoader展示图片的工具
     */
    private DisplayImageOptions options;

    /**
     * RecyclerView 可以通过设置不同的 LayoutManager 来达到不同的布局效果，如线性布局，网格布局等
     * LayoutManager 负责在 RecyclerView 中度量和定位 Item 视图，
     * 并确定何时回收用户不再可见的 Item 的策略。通过更改 LayoutManager，
     * 可以使用 RecyclerView 实现标准的垂直滚动列表、统一的网格、交错网格、水平滚动集合等
     */
    private GridLayoutManager layoutManager;

    public static Fragment newInstance() {
        return new SkinFragment();
    }

    /**
     * 系统会在Fragment首次绘制其用户界面时调用此方法。 要想为Fragment绘制 UI，
     * 从该方法中返回的 View 必须是Fragment布局的根视图。如果Fragment未提供 UI，您可以返回 null。
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.skin_fragment, container, false);

//        这个ID是在Fragment中设置的RecycleView的ID
        skinPreView = view.findViewById(R.id.skin_preview_recycler_view);
        adapter = new SkinPreviewAdapter(list);
//        GridLayoutManager构造函数接收两个参数， 第一个是Context， 第二个是列数
        layoutManager = new GridLayoutManager(context, 2);

//        设置Adapter
        skinPreView.setAdapter(adapter);
//        设置布局管理器
        skinPreView.setLayoutManager(layoutManager);
//        设置分隔线
//        skinPreView.addItemDecoration(new DividerGridItemDecoration(this));
//        设置增加或删除条目的动画
        skinPreView.setItemAnimator(new DefaultItemAnimator());

        layoutManager.setSmoothScrollbarEnabled(true);
//        解决NestedScrollView嵌套滑动的卡顿
        skinPreView.setNestedScrollingEnabled(false);
        return view;
    }

    /**
     * 系统会在创建Fragment时调用此方法。可以初始化一段资源文件等等
     * 此方法在onCreateView方法前执行
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

//        加载本地图片，不缓存，不存储
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(false).cacheInMemory(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .build();

//        初始化本地数据
        initSkinPreview();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * 初始化皮肤预览数据
     * 写图片的URL的时候需要注意，图片的URL可以分为以下几种：
     * // 网络图片
     * String imageUri = "http://pic2.16pic.com/00/15/80/16pic_1580359_b.jpg";
     * //SD卡图片
     * String imageUri = "file:///mnt/sdcard/zouqi.png";
     * // 媒体文件夹
     * String imageUri = "content://media/external/audio/albumart/6";
     * // assets
     * String imageUri = "assets://zouqi.png";
     * //  drawable文件
     * String imageUri = "drawable://" + R.drawable.zouqi;
     * 记得配置权限：
     * <uses-permission android:name="android.permission.INTERNET"/>
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    private void initSkinPreview() {

        list = new ArrayList();
//        int uri = R.drawable.intro3;
//        FutureTarget<Bitmap> futureTarget  = GlideApp.with(context).asBitmap().load(uri).submit(100,200);
//        Bitmap bm = null;
//        try {
//            bm = futureTarget .get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        此方法需要注意：取到的可能有空,因为涉及IO操作(包括网络IO)需要开线程，4.0以后的这个方法获取为空,可以通过添加option获取
        String uri = "drawable://" + R.drawable.intro3;
        Bitmap bm = ImageLoader.getInstance().loadImageSync(uri, options);

        Skin skin1 = new Skin();
        skin1.setName("material黑");
        skin1.setPreviewImg(bm);

        Skin skin2 = new Skin();
        skin2.setName("纯黑");
        skin2.setPreviewImg(bm);

        Skin skin3 = new Skin();
        skin3.setName("纯白");
        skin3.setPreviewImg(bm);

        Skin skin4 = new Skin();
        skin4.setName("蓝色");
        skin4.setPreviewImg(bm);

        Skin skin5 = new Skin();
        skin5.setName("紫色");
        skin5.setPreviewImg(bm);


        Skin skin7 = new Skin();
        skin7.setName("紫色");
        skin7.setPreviewImg(bm);

        Skin skin8 = new Skin();
        skin8.setName("紫色");
        skin8.setPreviewImg(bm);

        Skin skin9 = new Skin();
        skin9.setName("紫色");
        skin9.setPreviewImg(bm);

        Skin skin10 = new Skin();
        skin10.setName("紫色");
        skin10.setPreviewImg(bm);

        Skin skin11 = new Skin();
        skin11.setName("紫色");
        skin11.setPreviewImg(bm);

        Skin skin12 = new Skin();
        skin12.setName("紫色");
        skin12.setPreviewImg(bm);

        list.add(skin1);
        list.add(skin2);
        list.add(skin3);
        list.add(skin4);
        list.add(skin5);
        list.add(skin7);
        list.add(skin8);
        list.add(skin9);
        list.add(skin10);
        list.add(skin11);
        list.add(skin12);

    }
}
