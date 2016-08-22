package com.bupt.sang.happyweather.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sangyaohui on 16/8/22.
 */
public class MainPagerAdapter extends FragmentPagerAdapter{

    public List<String> cityNames;
    public FragmentManager fragmentManager;

    public MainPagerAdapter(FragmentManager fm, List<String> names) {
        super(fm);
        cityNames = names;
        fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return WeatherFragment.newInstance(cityNames.get(position));
    }

    @Override
    public int getCount() {
        return cityNames.size();
    }

    public List<String> getNameList() {
        return cityNames;
    }

    public void updateCities(List<String> names) {
        this.cityNames = names;
        notifyDataSetChanged();
    }
}
