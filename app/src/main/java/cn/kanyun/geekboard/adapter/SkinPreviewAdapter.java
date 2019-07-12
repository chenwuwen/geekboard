package cn.kanyun.geekboard.adapter;

import android.graphics.Color;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.entity.Skin;
import cn.kanyun.geekboard.listener.SkinSwitchListener;
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
 *
 * onBindViewHolder()
 * 这个方法主要用于适配渲染数据到View中。方法提供给你了一viewHolder而不是原来的convertView。
 *
 * getItemCount()
 * 这个方法就类似于BaseAdapter的getCount方法了，即总共有多少个条目。
 */
public class SkinPreviewAdapter extends RecyclerView.Adapter<SkinPreviewAdapter.ViewHolder> {

    private static final String TAG = "SkinPreviewAdapter";
    /**
     * 数据源
     */
    private List<Skin> dataList;


    public SkinPreviewAdapter(List list) {
        this.dataList = list;
    }


    /**
     * 创建ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public SkinPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skin_preview_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * 填充视图
     * 将数据与界面进行绑定的操作
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull SkinPreviewAdapter.ViewHolder holder, int position) {
//        holder后面的属性是当前类中的静态内部类的私有属性
        holder.button.setText(dataList.get(position).getName());
        holder.button.setImageResource(dataList.get(position).getPreviewImg());

//        item 点击事件
//        holder.button.setOnClickListener(v -> {
//            Log.i(TAG, "点击了皮肤:"+dataList.get(position).getName());
//        });
        holder.button.setOnClickListener(new SkinSwitchListener(dataList.get(position).getName(),position));
        holder.button.setOnTouchListener(new SkinSwitchListener(dataList.get(position).getName(),position));

    }

    /**
     * 返回item个数
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
