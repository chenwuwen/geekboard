package cn.kanyun.geekboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.entity.Skin;
import cn.kanyun.geekboard.listener.SkinSwitchListener;
import cn.kanyun.geekboard.util.SPUtils;
import cn.kanyun.geekboard.widget.SkinPreviewButton;

/**
 * RecyclerView 是 ListView 的升级版，更加灵活，同时由于封装了 ListView 的部分实现，
 * 导致其使用更简单，结构更清晰
 * 创建适配器:
 * 标准实现步骤如下：
 * ① 创建Adapter：创建一个继承RecyclerView.Adapter<VH>的Adapter类（VH是ViewHolder的类名）
 * ② 创建ViewHolder：在Adapter中创建一个继承RecyclerView.ViewHolder的静态内部类，记为VH。
 * ViewHolder的实现和ListView的ViewHolder实现几乎一样。
 * ③ 在Adapter中实现3个方法：
 * onCreateViewHolder()
 * 这个方法主要生成为每个Item inflater出一个View，但是该方法返回的是一个ViewHolder。
 * 该方法把View直接封装在ViewHolder中，然后我们面向的是ViewHolder这个实例，
 * 当然这个ViewHolder需要我们自己去编写
 * onBindViewHolder()
 * 这个方法主要用于适配渲染数据到View中。方法提供给你了一viewHolder而不是原来的convertView。
 * getItemCount()
 * 这个方法就类似于BaseAdapter的getCount方法了，即总共有多少个条目。
 */
public class SkinPreviewAdapter extends RecyclerView.Adapter<SkinPreviewAdapter.ViewHolder> {

    private static final String TAG = "SkinPreviewAdapter";


    /**
     * 数据源
     */
    private List<Skin> dataList;

    Context context;

    /**
     * SharedPreferences中保存的皮肤Id,如果没有找不到
     * 则返回dataList中的第一个
     */
    private long lastSkinId;


    public SkinPreviewAdapter(List list) {
        this.dataList = list;
    }


    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public SkinPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skin_preview_item, parent, false);
//        获得SharedPreferences中记录上次选择的皮肤ID(找不到返回默认值)
        lastSkinId = (long) SPUtils.get(context, Constant.BOARD_SKIN, dataList.get(0).getId());
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    /**
     * 填充视图
     * 将数据与界面进行绑定的操作
     * 这个方法将会遍历,遍历此时为dataList.size()
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull SkinPreviewAdapter.ViewHolder holder, int position) {

//        获取当前遍历得到的皮肤名称
        String currentSkinName = dataList.get(position).getName();
//        获取当前遍历得到的皮肤Id
        long currentSkinId = dataList.get(position).getId();

//      holder后面的属性是当前类中的静态内部类的私有属性
        holder.button.setText(dataList.get(position).getName());
        holder.button.setImageResource(dataList.get(position).getPreviewImg());

//      如果SharedPreferences中记录上次选择的皮肤名称与当前皮肤名称一致,则将其标注为已启用状态
        if (lastSkinId == currentSkinId) {
//            将皮肤预览按钮点击监听器的最后一个索引设置为当前索引
            SkinSwitchListener.lastIndex = position;
//            将被选中的的按钮添加角标
            ConstraintLayout layout = (ConstraintLayout) holder.itemView;
            ImageView enable = layout.findViewById(R.id.skin_enable);
            enable.setVisibility((View.VISIBLE));
        }


//        item 点击事件
//        holder.button.setOnClickListener(v -> {
//            Log.i(TAG, "点击了皮肤:"+dataList.get(position).getName());
//        });
        holder.button.setOnClickListener(new SkinSwitchListener(dataList.get(position).getName(), dataList.get(position).getId(), position));
        holder.button.setOnTouchListener(new SkinSwitchListener(dataList.get(position).getName(), dataList.get(position).getId(), position));

    }

    /**
     * 返回item个数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }


    /**
     * 自定义的ViewHolder，持有每个Item的的所有界面元素
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SkinPreviewButton button;

        public ViewHolder(View itemView) {
            super(itemView);
            initView();
        }

        private void initView() {
            button = itemView.findViewById(R.id.skin_preview_item_button);
        }
    }
}
