package com.gaoyve.android.sensingearth.sensor;

import java.io.Serializable;
import java.util.UUID;

/**
 * SensingEarth
 * Created by Gerry on 11/20/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class SensorConfiguration implements Serializable{
    private UUID mID;
    private String mConfigName;
    private String mSensorState;
    private double mTmpHigh;
    private double mTmpLow;
    private double mLightHigh;
    private double mLightLow;
    private double mNoiseHigh;
    private double mNoiseLow;

    SensorConfiguration(String name, double tmpLow, double tmpHigh,
                        double lightLow, double lightHigh,
                        double noiseLow, double noiseHigh) {
        mConfigName = name;

        mID = UUID.randomUUID();
        if (tmpHigh > tmpLow) {
            mTmpHigh = tmpHigh;
            mTmpLow = tmpLow;
            mSensorState = "1";
        } else {
            mTmpHigh = mTmpLow = 0;
            mSensorState = "0";
        }

        if (lightHigh > lightLow){
            mLightHigh = lightHigh;
            mLightLow = lightLow;
            mSensorState += "1";
        } else {
            mLightHigh = mLightLow = 0;
            mSensorState += "0";
        }

        if (noiseHigh > noiseLow) {
            mNoiseHigh = noiseHigh;
            mNoiseLow = noiseLow;
            mSensorState += "1";
        } else {
            mNoiseHigh = mNoiseLow = 0;
            mSensorState += "0";
        }
    }

    public UUID getID() {
        return mID;
    }

    public String getConfigName() {
        return mConfigName;
    }

    public String getSensorState() {
        return mSensorState;
    }

    public double getTmpHigh() {
        return mTmpHigh;
    }

    public double getTmpLow() {
        return mTmpLow;
    }

    public double getLightHigh() {
        return mLightHigh;
    }

    public double getLightLow() {
        return mLightLow;
    }

    public double getNoiseLow() {
        return mNoiseLow;
    }

    public double getNoiseHigh() {
        return mNoiseHigh;
    }


}
