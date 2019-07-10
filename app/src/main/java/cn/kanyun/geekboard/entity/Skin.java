package cn.kanyun.geekboard.entity;

import android.graphics.Bitmap;

import lombok.Data;

/**
 * 皮肤实体类
 */
@Data
public class Skin {

    /**
     * 皮肤名
     */
    private String name;

    /**
     * 预览图资源
     */
    private Bitmap previewImg;



}
