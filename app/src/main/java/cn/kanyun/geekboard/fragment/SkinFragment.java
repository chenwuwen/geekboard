package cn.kanyun.geekboard.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.orhanobut.logger.Logger;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.kanyun.geekboard.MainActivity;
import cn.kanyun.geekboard.MyApplication;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.activity.AdjustCustomSkinActivity;
import cn.kanyun.geekboard.adapter.SkinPreviewAdapter;
import cn.kanyun.geekboard.broadcast.SkinChangeReceiver;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.entity.Skin;
import cn.kanyun.geekboard.gen.SkinDao;
import cn.kanyun.geekboard.util.PhotoUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import top.androidman.SuperButton;

import static android.app.Activity.RESULT_OK;

/**
 * 皮肤Fragment
 */
public class SkinFragment extends BaseFragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final int RC_CAMERA_AND_STORAGE = 9757;
    private static final int RC_STORAGE = 9758;

    /**
     * 所需权限列表
     */
    public static final String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    Context context;

    RecyclerView skinPreView;

    RecyclerView.Adapter adapter;

    /**
     * 数据源
     */
    private List<Skin> list;

    /**
     * 打开相机按钮
     */
    @BindView(R.id.camera)
    SuperButton cameraButton;

    /**
     * 打开本地图片
     */
    @BindView(R.id.photo)
    SuperButton photoButton;


    /**
     * 在非Activity类中使用ButterKnife.bind(this,view)会返回一个Unbinder值（进行解绑）
     * 注意这里的this不能使用getActivity()
     * 在Activity中不需要做解绑操作，在Fragment 中必须在onDestroyView()中做解绑操作
     */
    private Unbinder unbinder;

    /**
     * RecyclerView 可以通过设置不同的 LayoutManager 来达到不同的布局效果，如线性布局，网格布局等
     * LayoutManager 负责在 RecyclerView 中度量和定位 Item 视图，
     * 并确定何时回收用户不再可见的 Item 的策略。通过更改 LayoutManager，
     * 可以使用 RecyclerView 实现标准的垂直滚动列表、统一的网格、交错网格、水平滚动集合等
     */
    private GridLayoutManager layoutManager;

    /**
     * 定义广播接收者
     */
    SkinChangeReceiver skinChangeReceiver;

    /**
     * 定义意图过滤器
     */
    private IntentFilter intentFilter;

    private MyApplication application;

    /**
     * 拍照后保存的照片
     */
    private File imgFile;

    /**
     * 拍照后保存的照片的uri
     */
    private Uri imgUri;

    /**
     * startActivityForResult时的请求码
     * 请求相机返回码
     */
    private static final int REQUEST_CODE_CAMERA = 8848;

    /**
     * 请求相册返回码
     */
    private static final int REQUEST_CODE_PHOTO = 8849;

    /**
     * FileProvider的签名
     */
    private static final String AUTHORITY = "cn.kanyun.geekboard.fileProvider";


    public static Fragment newInstance() {
        return new SkinFragment();
    }

    /**
     * 系统会在创建Fragment时调用此方法。可以初始化一段资源文件等等
     * 此方法在onCreateView方法前执行
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        application = (MyApplication) MyApplication.getInstance();

//        初始化本地数据
        initSkinPreview();
//        注册广播接受者(动态注册)
        skinChangeReceiver = new SkinChangeReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BOARD_SKIN_SWITCH);
        context.registerReceiver(skinChangeReceiver, intentFilter);

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

//        Fragment中使用ButterKnife需要这样配置,使用两个参数的bind方法(注意：在非Activity类,这里的this不能替换成getActivity(),在Activity中不需要做解绑操作，在Fragment 中必须在onDestroyView()中做解绑操作)
        ButterKnife.bind(this, view);

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

        cameraButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 销毁时
     * 取消注册广播接收者
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(skinChangeReceiver);
    }

    /**
     * 解绑ButterKnife
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

        SkinDao skinDao = application.getDaoSession().getSkinDao();
        list = skinDao.loadAll();


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.camera:
                Logger.d("调用相机前先请求权限");
//                原生请求权限方式
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                这里使用EasyPermissions来申请权限,因为申请后还要进行回调
                EasyPermissions.requestPermissions(this, "hellp",
                        RC_CAMERA_AND_STORAGE, perms);
                break;
            default:
                EasyPermissions.requestPermissions(this, "hellp",
                        RC_STORAGE, perms);
        }
    }

    /**
     * 调用相机
     * 调用相机之前，intent需要设置两个参数
     * intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())    设置图片保存的格式
     * intent.putExtra(MediaStore.EXTRA_OUTPUT,uri) 设置拍照后图片保存的位置，如果没有这句，相机则会返回一个拍照的缩略图
     * 注意：在设置图片保存的uri的时候，需要对Android7.0以上系统做处理，否则会报FileUriExposedException异常
     */
    private void useCamera() {
//        这个路径要与file_paths.xml中配置的路径一致,这个路径是相机拍摄完成后图片的存放路径
        imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/skin/" + System.currentTimeMillis() + ".jpg");
        imgFile.getParentFile().mkdirs();
        try {
            imgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //如果是7.0以上，使用FileProvider，否则会报错
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            imgUri = FileProvider.getUriForFile(context, AUTHORITY, imgFile);
            //设置拍照后图片保存的位置
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        } else {
            //设置拍照后图片保存的位置
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
        }
        //设置图片保存的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());


        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //调起系统相机
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 进入相册
     */
    private void usePhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择一张图片用作皮肤"), REQUEST_CODE_PHOTO);
    }


    /**
     * Activity结果返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {

            Uri sourceUri = null;

//            注：android4.4以前直接data.getData就可以获取到真是Uri不用解析,
//            而之后需要使用PhotoUtils.getPath(this, data.getData())。解析获取真实的Uri后
//            判断系统版本开始通过FileProvider获取新的Uri之后就可以同样的图片剪裁了
//            需要注意的是data有可能为null：https://blog.csdn.net/zimo2013/article/details/16916279
            if (data != null) {
                Logger.d("相机拍完照片后Intent不是null,说明Uri是未指定的,也说明当前设备是低于Android7.0的");
                String uriStr = PhotoUtil.getPath(context, data.getData());
                sourceUri = Uri.parse(uriStr);
            } else {
                Logger.d("相机拍完照片后Intent是null,说明Uri是指定的,也说明当前设备是高于Android7.0的");
                sourceUri = FileProvider.getUriForFile(context, AUTHORITY, imgFile);
            }

//            https://www.jianshu.com/p/9e8a91577b65

//            这里不使用这个方法的原因是,在Fragment中使用该方法,onActivityResult只会调用一次,裁剪完成后的回调会不执行,具体看其相应的方法
//            PhotoUtil.toCrop(sourceUri, getActivity());
//            在Fragment中使用uCrop,需要使用 uCrop.start(context,fragment)方法
            PhotoUtil.toCrop(sourceUri, this, context);
        } else if (requestCode == REQUEST_CODE_PHOTO) {
            Logger.d("进到这里说明,请求相册返回了");
            String uriStr = PhotoUtil.getPath(context, data.getData());
            Uri sourceUri = Uri.parse(uriStr);
            PhotoUtil.toCrop(sourceUri, this, context);
        } else if (resultCode == RESULT_OK) {
            Logger.d("进入这里说明Activity调用成功");
            if (requestCode == UCrop.RESULT_ERROR) {
                ToastUtils.showLong("uCrop 错误");
                return;
            }
            if (requestCode == UCrop.REQUEST_CROP) {
                final Uri imgUri = UCrop.getOutput(data);
                Logger.d("图片裁剪后进入这里：" + imgUri.toString());
                Intent intent = new Intent();
                intent.setData(imgUri);
                intent.setClass(context, AdjustCustomSkinActivity.class);
                startActivity(intent);
                return;
            }
        } else {
//            进到这里,也说明了 resultCode != -1 表明Activity跳转出现了问题
            ToastUtils.showLong("Activity跳转出错");
        }
    }

    /**
     * 检查权限
     * 权限可以是单个，也可以是一些列
     * 在EasyPermission库中，使用EasyPermissions#hasPermissions(...)方法
     * 检测一个或者多个权限是否被允许（当有一个权限被拒绝就会返回false）
     *
     * @AfterPermissionGranted()注解是可选的,其中的int型的参数就是requestCode
     */
    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    private void useCameraByRC() {

        if (EasyPermissions.hasPermissions(context, perms)) {
            // 已经赋予了权限
            Logger.d("被@AfterPermissionGranted注解的方法被执行,且已经获取到权限");
            useCamera();
        } else {
            Logger.d("被@AfterPermissionGranted注解的方法被执行,但是没有获取到权限");
            // 没有赋予权限，此时请求权限
//            第一个参数：Context对象  第二个参数：权限弹窗上的文字提示语。告诉用户，这个权限用途。 第三个参数：这次请求权限的唯一标示，code。 第四个参数 : 一些系列的权限
            EasyPermissions.requestPermissions(this, "请确认允许权限,这将打开您的相机为您制作个性皮肤",
                    RC_CAMERA_AND_STORAGE, perms);
        }
    }

    /**
     * 对于使用相册的回调
     */
    @AfterPermissionGranted(RC_STORAGE)
    public void usePhotoAlbumByRc() {
        if (EasyPermissions.hasPermissions(context, perms)) {
            // 已经赋予了权限
            Logger.d("被@AfterPermissionGranted注解的方法被执行,且已经获取到权限");
            usePhotoAlbum();
        } else {
            Logger.d("被@AfterPermissionGranted注解的方法被执行,但是没有获取到权限");
            // 没有赋予权限，此时请求权限
//            第一个参数：Context对象  第二个参数：权限弹窗上的文字提示语。告诉用户，这个权限用途。 第三个参数：这次请求权限的唯一标示，code。 第四个参数 : 一些系列的权限
            EasyPermissions.requestPermissions(this, "请确认允许权限,这将打开您的相机为您制作个性皮肤",
                    RC_STORAGE, perms);
        }
    }


    /**
     * 权限申请结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        将结果转发给EasyPermissions(这里使用了EasyPermissions框架来动态申请权限,同类还有RxPermissions) 回调到 EasyPermissions.PermissionCallbacks接口
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限成功。
     * 可以弹窗显示结果，也可执行具体需要的逻辑操作
     * 当权限被成功申请的时候执行回调，requestCode是代表你权限请求的识别码，list里面装着申请的权限的名字
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Logger.d("请求权限的Code:" + requestCode);
        ToastUtils.showLong("用户授权成功");
    }

    /**
     * 请求权限失败
     * 当权限申请失败的时候执行的回调，参数意义同上。在这个方法里面，
     * 官方还建议用EasyPermissions.somePermissionPermanentlyDenied(this, perms)方法来判断
     * 是否有权限被勾选了不再询问并拒绝，还提供了一个AppSettingsDialog来给我们使用，
     * 在这个对话框里面解释了APP需要这个权限的原因，用户按下是的话会跳到APP的设置界面，
     * 可以去设置权限,这个Dialog可以使用默认的样式new AppSettingsDialog.Builder(this).build().show()，也可以定制
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Logger.d("请求权限的Code:" + requestCode);
        ToastUtils.showLong("用户授权失败");
//      若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。这时候，需要跳转到设置界面去，让用户手动开启。
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
