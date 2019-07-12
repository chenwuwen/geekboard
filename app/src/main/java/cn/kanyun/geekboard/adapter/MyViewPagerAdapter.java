package cn.kanyun.geekboard.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cn.kanyun.geekboard.fragment.SettingFragment;
import cn.kanyun.geekboard.fragment.SkinFragment;

/**
 * 设置ViewPager与Fragment的关系
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {


    public MyViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public MyViewPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return SkinFragment.newInstance();
            case 1:
                return SettingFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    /**
     * 当ViewPager与TabLayout结合使用时,不必给TabLayout添加tab,
     * 也就是不用调用TabLayout.addTab().只需要重写PagerAdapter.getPageTitle()方法即可.
     */
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            case 0:
//                return "皮肤";
//            default:
//                return "设置";
//        }
//
//    }


}
