package cn.kanyun.geekboard.entity;

import android.graphics.Bitmap;

import lombok.Data;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;
/**
 * 皮肤实体类
 * @Entity 表示这个实体类一会会在数据库中生成对应的表，
 * @Id 表示该字段是id，注意该字段的数据类型为包装类型Long
 * @Property 则表示该属性将作为表的一个字段，其中nameInDb看名字就知道这个属性在数据库中对应的数据名称
 * @Transient 该注解表示这个属性将不会作为数据表中的一个字段。
 * @NotNull 表示该字段不可以为空
 * @Unique 表示该字段唯一
 */
@Entity(nameInDb ="SKIN",createInDb = true)
public class Skin {

    @Id(autoincrement = true)
    private Long id;
    /**
     * 皮肤名
     */
    @Property(nameInDb = "NAME")
    private String name;

    /**
     * 预览图资源
     */
    @Property(nameInDb = "previewImg")
    private String previewImg;

    @Property(nameInDb = "SKIN_IMG_XML")
    private String skinImgXml;

    /**
     * 是否启用
     */
    @Property(nameInDb = "ENABLE")
    private boolean enable;

    @Generated(hash = 964362226)
    public Skin(Long id, String name, String previewImg, String skinImgXml,
            boolean enable) {
        this.id = id;
        this.name = name;
        this.previewImg = previewImg;
        this.skinImgXml = skinImgXml;
        this.enable = enable;
    }

    @Generated(hash = 83933338)
    public Skin() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewImg() {
        return this.previewImg;
    }

    public void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }

    public String getSkinImgXml() {
        return this.skinImgXml;
    }

    public void setSkinImgXml(String skinImgXml) {
        this.skinImgXml = skinImgXml;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


}
