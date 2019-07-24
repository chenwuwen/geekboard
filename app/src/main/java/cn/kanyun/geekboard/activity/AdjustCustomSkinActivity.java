package cn.kanyun.geekboard.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kanyun.geekboard.GlideApp;
import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.util.PhotoUtil;

/**
 * 对自定义的皮肤进行一些处理
 */
public class AdjustCustomSkinActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    /**
     * 调整透明度的SeekBar
     */
    @BindView(R.id.skin_transparent_seekbar)
    SeekBar seekBar;

    /**
     * 剪切后的图片预览
     */
    @BindView(R.id.yu_lan_tu_img)
    ImageView view;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_custom_skin);
        context = this;
        ButterKnife.bind(this);
        seekBar.setOnSeekBarChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        Logger.d("图片处理Activity得到裁剪后的图片：" + uri);
        String filePath = PhotoUtil.getImagePath(context, uri);
//        使用Glide加载图片并防止在ImageView上
        GlideApp.with(this).load(filePath).into(view);

    }

    /**
     * SeekBar改变时触发
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
