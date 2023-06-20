package com.example.gine_tube.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.gine_tube.Dashboard.AboutDashFragment;
import com.example.gine_tube.Dashboard.HomeDashFragment;
import com.example.gine_tube.Dashboard.PlaylistDashFragment;
import com.example.gine_tube.Dashboard.SubscriptionDashFragment;
import com.example.gine_tube.Dashboard.VideoDashFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment>fragments=new ArrayList<>();
    ArrayList<String>strings=new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new HomeDashFragment();
            case 1:
                return new VideoDashFragment();
            case 2:
                return new PlaylistDashFragment();
            case 3:
                return new SubscriptionDashFragment();
            case 4:
                return new AboutDashFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    public void add(Fragment fr, String s)
    {
        fragments.add(fr);
        strings.add(s);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return strings.get(position);
    }
}
