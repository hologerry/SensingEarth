package com.gaoyve.android.sensingearth;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * SensingEarth
 * Created by Gerry on 11/30/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class SensorData implements Serializable{
    private String mType;
    private int mDataMaxNum;
    private int mCurrentStoredNum = 0;
    private ArrayList<Double> mSensorRawValues = new ArrayList<>();
    private ArrayList<Double> mSensorShowValues = new ArrayList<>();
    private Double mThresholdHigh;
    private Double mThresholdLow;
    private Double mMaxValue;
    private Double mMinValue;

    public SensorData(String name, double thresholdLow, double thresholdHigh, int dataMaxNum) {
        mType = name;
        mThresholdHigh = thresholdHigh;
        mThresholdLow = thresholdLow;
        mDataMaxNum = dataMaxNum;
        setMaxMinValue();
    }

    private void setMaxMinValue() {
        mMaxValue = mThresholdHigh + 2 * ((mThresholdHigh - mThresholdLow)/5);
        mMinValue = mThresholdLow - 2 * ((mThresholdHigh - mThresholdLow)/5);
    }

    public void handleRawValue(double value) {
        Double rawValue = new Double(value);
        if (mCurrentStoredNum >= mDataMaxNum) {
            mSensorRawValues.remove(0);
            mSensorShowValues.remove(0);
        }

        // add new data
        mSensorRawValues.add(rawValue);
        if (rawValue > mMaxValue) {
            mSensorShowValues.add(mMaxValue);
        } else if (rawValue < mMinValue) {
            mSensorShowValues.add(mMinValue);
        } else {
            mSensorShowValues.add(rawValue);
        }

        if (mCurrentStoredNum >= mDataMaxNum) {
            mCurrentStoredNum = mDataMaxNum;
        } else {
            mCurrentStoredNum++;
        }
    }

    public String getType() {
        return mType;
    }

    public int getMaxDataNum() {
        return mDataMaxNum;
    }

    public int getCurrentStoredNum() {
        return mCurrentStoredNum;
    }

    public ArrayList<Double> getSensorShowValues() {
        return mSensorShowValues;
    }

    public ArrayList<Double> getSensorRawValues() {
        return mSensorRawValues;
    }

    public Double getThresholdHigh() {
        return mThresholdHigh;
    }

    public Double getThresholdLow() {
        return mThresholdLow;
    }

    public Double getMaxValue() {
        return mMaxValue;
    }

    public Double getMinValue() {
        return mMinValue;
    }
}
