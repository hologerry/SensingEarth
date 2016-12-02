package com.gaoyve.android.sensingearth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * SensingEarth
 * Created by Gerry on 11/29/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class SensorFragment extends Fragment {
    public static final String FRG_PAGE = "com.gaoyve.android.sensingearth.sensorpage";
    public static final String FRG_DATA = "com.gaoyve.android.sensingearth.sensordata";
    private String mTitle;
    private int mPage;
    private PointChart mPointChart;
    private TextView mCurrentLabel;
    private TextView mCurrentData;
    private TextView mHigh;
    private TextView mLow;
    private SensorData mSensorData;

    public static SensorFragment newInstance(int page, SensorData sensorData) {
        System.out.println("newInstanceFragment" + sensorData);
        SensorFragment sensorFragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putInt(FRG_PAGE, page);
        args.putSerializable(FRG_DATA, sensorData);
        sensorFragment.setArguments(args);
        return sensorFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(FRG_PAGE, 0);
        mSensorData = (SensorData) getArguments().getSerializable(FRG_DATA);
        mTitle = mSensorData.getType();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sensor, container, false);
        mPointChart = (PointChart)root.findViewById(R.id.point_chart);
        mCurrentLabel = (TextView)root.findViewById(R.id.current_data_text_label);
        mCurrentData = (TextView)root.findViewById(R.id.current_data_text_view);
        mHigh = (TextView)root.findViewById(R.id.fragment_high_text_view);
        mLow = (TextView)root.findViewById(R.id.fragment_low_text_view);
        mHigh.setText(mSensorData.getThresholdHigh().toString());
        mLow.setText(mSensorData.getThresholdLow().toString());

        prepareData();
        return root;
    }

    private void prepareData() {

        // set threshold
        mPointChart.setThresholdHigh(mSensorData.getThresholdHigh());
        mPointChart.setThresholdLow(mSensorData.getThresholdLow());

        // prepare Y
        String[] YAxis = new String[10];
        Double interval = (mSensorData.getMaxValue() - mSensorData.getMinValue() ) / 9;
        for (int i = 0; i < 10; i++) {
            Double tmp = mSensorData.getMinValue() + i * interval;
            if (mSensorData.getType() == SensingActivity.LIGHT) {
                YAxis[i] = String.format("%d",tmp.intValue());
            } else {
                YAxis[i] = String.format("%.2f", tmp);
            }
        }

        // prepare X
        String[] XAxis = new String[SensingActivity.MAX_DATA_NUM];
        for (int i = 0; i < SensingActivity.MAX_DATA_NUM; i++) {
            XAxis[i] = Integer.toString(i+1);
        }

        // prepare data
        ArrayList<Double> points = new ArrayList<>();
        for (int i = 0; i < mSensorData.getCurrentStoredNum(); i++) {
            points.add(mSensorData.getSensorShowValues().get(i));
            System.out.println("CurrentStoreValue" + mSensorData.getSensorRawValues().get(i));
        }

        // update data
        mPointChart.setXAxis(XAxis);
        mPointChart.setYAxis(YAxis);
        mPointChart.setPointValues(points);
    }

    public void update() {
        // reset data
        ArrayList<Double> points = new ArrayList<>();
        for (int i = 0; i < mSensorData.getCurrentStoredNum(); i++) {
            points.add(mSensorData.getSensorShowValues().get(i));
            System.out.println("CurrentStoreValue" + mSensorData.getSensorRawValues().get(i));
        }
        mPointChart.setPointValues(points);

        // set current data
        if (mSensorData.getCurrentStoredNum() > 0) {
            Double currentData = mSensorData.getSensorRawValues().get(mSensorData.getCurrentStoredNum() - 1);
            String strCurrentData = currentData.toString();
            mCurrentData.setText(strCurrentData);
            if (currentData > mSensorData.getThresholdHigh() || currentData < mSensorData.getThresholdLow()) {
                mCurrentData.setTextColor(getResources().getColor(R.color.colorAbnormal));
            } else {
                mCurrentData.setTextColor(getResources().getColor(R.color.colorNormal));
            }
            System.out.println("update currentData");
        }
    }
}
