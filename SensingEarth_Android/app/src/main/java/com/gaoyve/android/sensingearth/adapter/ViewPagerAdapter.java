package com.gaoyve.android.sensingearth.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gaoyve.android.sensingearth.fragment.NoSensorFragment;
import com.gaoyve.android.sensingearth.fragment.SensorFragment;
import com.gaoyve.android.sensingearth.sensor.SensorData;

import java.util.ArrayList;

/**
 * SensingEarth
 * Created by Gerry on 11/29/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static int PageCount;
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<SensorFragment> fragments = new ArrayList<>();
    private ArrayList<SensorData> mSensorDatas = new ArrayList<>();
    private boolean mIsAllSensorOff = false;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public ArrayList<SensorData> getSensorDatas() {
        return mSensorDatas;
    }

    public void setSensorDatas(ArrayList<SensorData> sensorDatas) {
        mSensorDatas = sensorDatas;
        loadPages();
    }

    public void loadPages() {
        System.out.println("loadPages...");
        PageCount = mSensorDatas.size();
        for (int i = 0; i < mSensorDatas.size(); i++) {
            mTitles.add(mSensorDatas.get(i).getType());
            SensorFragment frg = SensorFragment.newInstance(i,mSensorDatas.get(i));
            fragments.add(frg);
        }
        if (mSensorDatas.size() > 0) {
            System.out.println(mSensorDatas.get(0).getCurrentStoredNum());
        } else {
            mIsAllSensorOff = true;
            mTitles.add("No Sensor");
            PageCount = 1;
        }
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return PageCount;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if (mIsAllSensorOff) {
            return new NoSensorFragment();
        } else {
            return fragments.get(position);
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        if (mIsAllSensorOff) {
            return POSITION_UNCHANGED;
        } else {
            SensorFragment f = (SensorFragment) object;
            if (f != null) {
                f.update();
            }
            return super.getItemPosition(object);
        }
    }
}
