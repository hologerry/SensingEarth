package com.gaoyve.android.sensingearth.sensor;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * SensingEarth
 * Created by Gerry on 11/20/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class SensorConfigurationLab {
    private static SensorConfigurationLab mConfigurationLab;
    private ArrayList<SensorConfiguration> mSensorCfgs;

    public static SensorConfigurationLab get(Context context) {
        if (mConfigurationLab == null) {
            mConfigurationLab = new SensorConfigurationLab(context);
        }
        return mConfigurationLab;
    }

    private SensorConfigurationLab(Context context) {
        mSensorCfgs = new ArrayList<>();
        SensorConfiguration sensorConfiguration = new SensorConfiguration("Default",10,30,0,600,0,30);
        mSensorCfgs.add(sensorConfiguration);
    }

    public List<SensorConfiguration> getSensorCfgs() {
        return mSensorCfgs;
    }

    public List<String> getSensorCfgNameList() {
        List<String> list = new ArrayList<>();
        for (SensorConfiguration cfg: mSensorCfgs) {
            list.add(cfg.getConfigName());
        }
        return list;
    }

    public SensorConfiguration getCfg(String name) {
        for (SensorConfiguration sensorCfg:mSensorCfgs) {
            if (sensorCfg.getConfigName().equals(name)) {
                return sensorCfg;
            }
        }
        return null;
    }

    public boolean createConfiguration(String name,
                                    double tmpLow, double tmpHigh,
                                    double lightLow, double lightHigh,
                                    double noiseLow, double noiseHigh ) {
        SensorConfiguration cfg = getCfg(name);
        if (cfg == null) {
            SensorConfiguration sensorConfiguration = new SensorConfiguration(name,
                    tmpLow, tmpHigh,
                    lightLow, lightHigh,
                    noiseLow,noiseHigh);
            mSensorCfgs.add(sensorConfiguration);
            return true;
        } else {
            return false;
        }
    }
}
