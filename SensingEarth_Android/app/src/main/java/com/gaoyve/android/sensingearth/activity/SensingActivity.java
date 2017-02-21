package com.gaoyve.android.sensingearth.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gaoyve.android.sensingearth.R;
import com.gaoyve.android.sensingearth.sensor.SensorConfiguration;
import com.gaoyve.android.sensingearth.sensor.SensorData;
import com.gaoyve.android.sensingearth.socket.SocketChannelService;
import com.gaoyve.android.sensingearth.adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * SensingEarth
 * Created by Gerry on 11/27/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */


public class SensingActivity extends AppCompatActivity {
    private static final String EXTRA_CFG = "com.gaoyve.android.sensingearth.sensorstate";
    public static final String TMP = "Tmp";
    public static final String LIGHT = "Light";
    public static final String NOISE = "Noise";
    public static final int MAX_DATA_NUM = 13;

    private SensorConfiguration mSelectedSelectedCfg;
    private String mCurrentSensorState;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private boolean mIsBound;
    private Messenger mService = null;
    private Messenger mMessenger = new Messenger(new IncomingHandler());

    private SensorData mTmpSensorData;
    private SensorData mLightSensorData;
    private SensorData mNoiseSensorData;
    private ArrayList<SensorData> mShowingSensorDatas = new ArrayList<>();

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketChannelService.MSG_READ_FROM_CLIENT:
                    handleSensorData(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            System.out.println(mService);
            try {
                Message msg = Message.obtain(null, SocketChannelService.MSG_REGISTER_ACTIVITY);
                msg.replyTo = mMessenger;
                System.out.println(mMessenger);
                mService.send(msg);
                System.out.println("mConnection....");
            }
            catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
        }
    };

    private void StartAndBindService() {
        //If the service is running when the activity starts, we want to automatically bind to it.
        if (!SocketChannelService.isServiceRuning()) {
            startService(new Intent(SensingActivity.this, SocketChannelService.class));
            System.out.println("StartAndBindService");
        }
        doBindService();
    }

    void doBindService() {
        bindService(new Intent(this, SocketChannelService.class), mConnection, Context.BIND_AUTO_CREATE);
        System.out.println("Sensing Activity is bound to service");
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, SocketChannelService.MSG_UNREGISTER_ACTIVITY);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            System.out.println("Sensing Activity is unbound to service");
        }
    }

    private void sendMessageToService(String stringMsg) {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString(SocketChannelService.MSG_FROM_ACTIVITY, stringMsg);
                    Message msg = Message.obtain(null, SocketChannelService.MSG_SEND_TO_CLIENT);
                    msg.setData(bundle);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                    System.out.println(stringMsg);
                    System.out.println("sendMessagetoService");
                }
                catch (RemoteException e) {
                }
            }
        }
    }

    private void initSensorData() {
        System.out.println("initSensorData");
        if (mCurrentSensorState.charAt(0) == '1') {
            mTmpSensorData = new SensorData(TMP, mSelectedSelectedCfg.getTmpLow(),mSelectedSelectedCfg.getTmpHigh(),MAX_DATA_NUM);
            mShowingSensorDatas.add(mTmpSensorData);
            System.out.println("mTmpSensorData" + mTmpSensorData);
        }
        if (mCurrentSensorState.charAt(1) == '1') {
            mLightSensorData = new SensorData(LIGHT, mSelectedSelectedCfg.getLightLow(),mSelectedSelectedCfg.getLightHigh(),MAX_DATA_NUM);
            mShowingSensorDatas.add(mLightSensorData);
            System.out.println("mLightSensorData" + mLightSensorData);
        }
        if (mCurrentSensorState.charAt(2) == '1') {
            mNoiseSensorData = new SensorData(NOISE, mSelectedSelectedCfg.getNoiseLow(),mSelectedSelectedCfg.getNoiseHigh(),MAX_DATA_NUM);
            mShowingSensorDatas.add(mNoiseSensorData);
            System.out.println("mNoiseSensorData" + mNoiseSensorData);
        }
    }

    private void handleSensorData(Message message) {
        System.out.println("handleSensorData");
        Bundle bundle = message.getData();
        String stringMsg = bundle.getString(SocketChannelService.MSG_SEND_TO_ACTIVITY);
        String[] sensorDatas = stringMsg.split("#");
        double tmp = Double.parseDouble(sensorDatas[1]);
        double light = Double.parseDouble(sensorDatas[2]);
        double noise = Double.parseDouble(sensorDatas[3]);
        System.out.println(mCurrentSensorState);

        boolean isAbnormalData = false;
        String string = null;
        if (mCurrentSensorState.charAt(0) == '0') {
            string = "0";
        } else {
            if (tmp > mSelectedSelectedCfg.getTmpHigh() ||
                    tmp < mSelectedSelectedCfg.getTmpLow()) {
                string = "2";
                isAbnormalData = true;
            } else {
                string = "1";
            }
            System.out.println("handling new tmp data");
            mTmpSensorData.handleRawValue(tmp);
        }

        if (mCurrentSensorState.charAt(1) == '0') {
            string += "0";
        } else {
            if (light > mSelectedSelectedCfg.getLightHigh() || light < mSelectedSelectedCfg.getLightLow()) {
                string += "2";
                isAbnormalData = true;
            } else {
                string += "1";
            }
            mLightSensorData.handleRawValue(light);
        }

        if (mCurrentSensorState.charAt(2) == '0') {
            string += "0";
        } else {
            if (noise > mSelectedSelectedCfg.getNoiseHigh() || noise < mSelectedSelectedCfg.getNoiseLow()) {
                string += "2";
                isAbnormalData = true;
            } else {
                string += "1";
            }
            mNoiseSensorData.handleRawValue(noise);
        }

        mCurrentSensorState = string;
        updateSensorDatas();

        if (isAbnormalData) {
            sendMessageToService(mCurrentSensorState);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
        System.out.println("handle message done from client");
    }

    private void updateSensorDatas() {
        for (int i = 0; i < mShowingSensorDatas.size(); i++) {
            if (mShowingSensorDatas.get(i).getType().equals(TMP)) {
                mShowingSensorDatas.set(i, mTmpSensorData);
            } else if (mShowingSensorDatas.get(i).getType().equals(LIGHT)) {
                mShowingSensorDatas.set(i, mLightSensorData);
            } else if (mShowingSensorDatas.get(i).getType().equals(NOISE)) {
                mShowingSensorDatas.set(i, mNoiseSensorData);
            }
        }
        mViewPagerAdapter.setSensorDatas(mShowingSensorDatas);
        updateWave();
    }

    private void updateWave() {
        mViewPagerAdapter.notifyDataSetChanged();

    }

    public static Intent newIntent(Context packageContext, SensorConfiguration mSltdCfg) {
        Intent i = new Intent(packageContext, SensingActivity.class);
        i.putExtra(EXTRA_CFG,mSltdCfg);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensing);
        StartAndBindService();

        mSelectedSelectedCfg = (SensorConfiguration) getIntent().getSerializableExtra(EXTRA_CFG);
        mCurrentSensorState = mSelectedSelectedCfg.getSensorState();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        initSensorData();

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.setSensorDatas(mShowingSensorDatas);
        mViewPager.setAdapter(mViewPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        }
        catch (Throwable t) {
            Log.e("SensingActivity", "Failed to unbind from the service", t);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            doUnbindService();
        }
        catch (Throwable t) {
            Log.e("SensingActivity", "Failed to unbind from the service", t);
        }
        finish();
    }
}
