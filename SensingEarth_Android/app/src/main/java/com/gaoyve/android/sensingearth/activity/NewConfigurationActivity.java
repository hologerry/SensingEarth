package com.gaoyve.android.sensingearth.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gaoyve.android.sensingearth.R;
import com.gaoyve.android.sensingearth.sensor.SensorConfigurationLab;

/**
 * SensingEarth
 * Created by Gerry on 11/20/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class NewConfigurationActivity extends AppCompatActivity {

    private boolean mInputError;
    private EditText mCfgNameText;
    private EditText mTmpLowText;
    private double tmpLow;
    private EditText mTmpHighText;
    private double tmpHigh;
    private EditText mLightLowText;
    private double lightLow;
    private EditText mLightHighText;
    private double lightHigh;
    private EditText mNoiseLowText;
    private double noiseLow;
    private EditText mNoiseHighText;
    private double noiseHigh;
    private FloatingActionButton mCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_configuration);

        mCfgNameText = (EditText)findViewById(R.id.config_name_text_view);
        mTmpLowText = (EditText)findViewById(R.id.tmp_low_text_view);
        mTmpHighText = (EditText)findViewById(R.id.tmp_high_text_view);
        mLightLowText = (EditText)findViewById(R.id.light_low_text_view);
        mLightHighText = (EditText)findViewById(R.id.light_high_text_view);
        mNoiseLowText = (EditText)findViewById(R.id.noise_low_text_view);
        mNoiseHighText = (EditText)findViewById(R.id.noise_high_text_view);

        mCreateButton = (FloatingActionButton) findViewById(R.id.create_cfg_button);
        mCreateButton.setOnClickListener(createButtonListener);
        mInputError = false;

        Toast toast = Toast.makeText(NewConfigurationActivity.this, R.string.configuration_intro,Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    private View.OnClickListener createButtonListener = new  View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // add cfg
            createCfg();
            // back to start
            if (mInputError) {
                Toast.makeText(NewConfigurationActivity.this, R.string.create_cfg_input_error,Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    };

    private double convertTextTODouble(EditText editText) {
        double doubleValue;
        String str = editText.getText().toString();
        if (str.isEmpty()) {
            doubleValue = 0.0;
        } else {
            try {
                doubleValue = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                mInputError = true;
                doubleValue = 0.0;
            }
        }
        return doubleValue;
    }
    private void createCfg() {
        String cfgName = mCfgNameText.getText().toString();
        if (cfgName.isEmpty()) {
            mInputError = true;
            return;
        }
        tmpLow = convertTextTODouble(mTmpLowText);
        if (mInputError) return;
        tmpHigh = convertTextTODouble(mTmpHighText);
        if (mInputError) return;
        lightLow = convertTextTODouble(mLightLowText);
        if (mInputError) return;
        lightHigh = convertTextTODouble(mLightHighText);
        if (mInputError) return;
        noiseLow = convertTextTODouble(mNoiseLowText);
        if (mInputError) return;
        noiseHigh = convertTextTODouble(mNoiseHighText);
        if (mInputError) return;

        if (!mInputError) {
            SensorConfigurationLab sensorConfigurationLab =
                    SensorConfigurationLab.get(NewConfigurationActivity.this);
            boolean res = sensorConfigurationLab.createConfiguration(cfgName,
                    tmpLow, tmpHigh, lightLow, lightHigh, noiseLow, noiseHigh);

            if (res) {
                setResult(RESULT_OK);
            } else {
                // make toast input error
                Toast.makeText(NewConfigurationActivity.this, R.string.create_cfg_name_error, Toast.LENGTH_LONG).show();
            }
        }
    }

}
