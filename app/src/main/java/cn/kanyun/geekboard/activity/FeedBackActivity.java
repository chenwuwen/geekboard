package cn.kanyun.geekboard.activity;


import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.mail.EmailUtil;

/**
 * 意见反馈Activity
 */
public class FeedBackActivity extends AppCompatActivity {

    private static final String TAG = "FeedBackActivity";
    /**
     * 使用@BindView注解的字段,不能用private,static修饰
     * 同时需要注意的是：被注解的变量的类型要与layout.xml中配置的一致
     * 否则会报错,判断方法,可以在layout.xml中 找到对应组件,点进去看看使用的是
     * 哪个包下的哪个组件,再在该注解下点击变量类型看看是哪个包下的组件,判断他们是否一致
     */
    @BindView(R.id.feedback)
    EditText feedback;


    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        context = this;
        ButterKnife.bind(this);

    }

    /**
     * 请求权限返回结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 0) {
            Toast.makeText(context, "拒绝权限无法使用捐赠功能", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
//        页面可见时,申请权限
        super.onResume();
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    /**
     * 提交反馈意见
     *
     * @param view
     */
    public void commitFeedBack(View view) {
//        获取设备型号
        String deviceModel = DeviceUtils.getModel();
//        设备制造商
        String firm = DeviceUtils.getManufacturer();
//        安卓系统版本
        String androidVersion = DeviceUtils.getSDKVersionName();
        String deviceInfo = firm + "型号：" + deviceModel + "系统版本：" + androidVersion + "/n";
        Log.i(TAG, "当前设备信息：" + deviceInfo);
        final String feedbacktext = deviceInfo + feedback.getText().toString().trim();
        if (feedbacktext.isEmpty()) {
            Toast.makeText(context, "请填写意见和建议", Toast.LENGTH_SHORT).show();
            return;
        } else {
            view.setEnabled(false);
            ExecutorService executor = Executors.newFixedThreadPool(3);
            Future<Boolean> future = executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    boolean state = EmailUtil.sendNormalEmail(feedbacktext, "2504954849@qq.com");
                    return state;
                }
            });
            boolean state = false;
            try {
                state = future.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                Toast.makeText(context, "发送超时,请检查你的网络", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
                return;
            }
            if (state) {
                Toast.makeText(context, "已发送", Toast.LENGTH_SHORT).show();
                feedback.setText("", TextView.BufferType.NORMAL);
                view.setEnabled(true);
            } else {
                Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        }

    }

    /**
     * 打开神秘页
     *
     * @param view
     */
    public void mysterious(View view) {
        //主布局container即根布局
        final ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final View mysterious_view = View.inflate(context, R.layout.mysterious, null);
                rootView.addView(mysterious_view);
                final EditText redBagCode = rootView.findViewById(R.id.red_bag_code);
                final TextView aliPay = rootView.findViewById(R.id.aliPay);
                final ImageView close = rootView.findViewById(R.id.close);
                final ImageView aliPayCodeImg = rootView.findViewById(R.id.aliPayCodeImg);
                final ImageView weChatCodeImg = rootView.findViewById(R.id.weChatCodeImg);
                redBagCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            // 此处为得到焦点时的处理内容
                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("redBagCode", redBagCode.getText().toString());
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                aliPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAliPay();
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rootView.removeView(mysterious_view);
                    }
                });

                aliPayCodeImg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        saveCodeImg(aliPayCodeImg);
                        return true;
                    }
                });
                weChatCodeImg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        saveCodeImg(weChatCodeImg);
                        return false;
                    }
                });
            }
        });
    }


    /**
     * 打开支付宝
     */
    public void openAliPay() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("提示").setMessage("是否打开应用")
                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PackageManager packageManager = context.getPackageManager();
//                        获取所有安装了的应用
                        List<ApplicationInfo> list = packageManager.getInstalledApplications(0);
                        for (ApplicationInfo info : list) {
                            if (info.packageName.equals("com.eg.android.AlipayGphone")) {
                                Intent intent = packageManager.getLaunchIntentForPackage(info.packageName);
                                startActivity(intent);
                            }
                        }
                        Toast.makeText(context, "未安装支付宝客户端", Toast.LENGTH_SHORT).show();

                    }
                });
        dialog.create().show();
    }


    /**
     * 保存二维码图片到本地
     *
     * @param type
     */
    public void saveCodeImg(ImageView type) {
        //使用兼容库就无需判断系统版本
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
            //拥有权限，执行操作
            boolean sdState = Environment.getExternalStorageState().endsWith(Environment.MEDIA_MOUNTED);
            if (sdState) {
                File file = Environment.getExternalStorageDirectory();
                String storagePath = file.getPath() + "/cn.kanyun.geekboard/" + UUID.randomUUID().toString() + ".png";
                if (!new File(storagePath).getParentFile().exists()) {
                    new File(storagePath).getParentFile().mkdirs();
                }
                if (!new File(storagePath).exists()) {
                    try {
                        new File(storagePath).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Bitmap bitmap = ((BitmapDrawable) type.getDrawable()).getBitmap();
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(storagePath))) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(storagePath));
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                    Toast.makeText(context, "已保存,请在图库中查看", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "SD卡未挂载不能保存图片", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            //没有权限，向用户请求权限,requestCode 为自定义的0，onRequestPermissionsResult()方法如果接受参数为0,说明已用户同意了授权
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

    }

    public void returnHome(View view) {
        finish();
    }
}
