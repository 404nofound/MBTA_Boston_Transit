package com.eddy.mbta;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.eddy.mbta.ui.alerts.AlertsFragment;
import com.eddy.mbta.ui.map.MapFragment;
import com.eddy.mbta.ui.stations.StationFragment;


public class MainPageAdapter extends FragmentPagerAdapter {
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public MainPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 1: return StationFragment.newInstance ();
            case 2: return AlertsFragment.newInstance ();
            default: return MapFragment.newInstance ();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
}

