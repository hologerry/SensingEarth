package com.gaoyve.android.sensingearth.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.gaoyve.android.sensingearth.R;
import com.gaoyve.android.sensingearth.sensor.SensorConfiguration;
import com.gaoyve.android.sensingearth.sensor.SensorConfigurationLab;
import com.gaoyve.android.sensingearth.socket.SocketChannelService;

import java.util.ArrayList;
import java.util.List;

/**
 * SensingEarth
 * Created by Gerry on 11/19/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class StartActivity extends AppCompatActivity {

    public static final boolean DEBUG = false;
    private static final String EXTRA_PORT = "com.gaoyve.android.sensingearth.port";
    private static final int NEW_CFG_REQUEST = 0;

    public static int mPort;
    private TextView mStateTextView;
    private TextView mIPTextView;
    private TextView mPortTextView;
    private CardView mConfigurationCard;

    private Spinner mConfigurationSpinner;
    public SensorConfiguration mSelectedSensorCfg;
    private FloatingActionButton mNewConfigurationButton;

    private Button mStartSensingButton;
    public String mState;


    private boolean mIsBound;
    private Messenger mService = null;

    private Messenger mMessenger = new Messenger(new IncomingHandler());;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketChannelService.MSG_READ_FROM_CLIENT:
                    break;
                case SocketChannelService.SOCKET_INFO_IP:
                    updateUI(msg);
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
            startService(new Intent(StartActivity.this, SocketChannelService.class));
            System.out.println("StartAndBindService");
        }
        doBindService();
    }

    void doBindService() {
        bindService(new Intent(this, SocketChannelService.class), mConnection, Context.BIND_AUTO_CREATE);
        System.out.println("Start Activity is bound to service");
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
            System.out.println("Start Activity is unbound to service");
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
                    System.out.println("sendMessagetoService");
                }
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Intent newIntent(Context packageContext, int port) {
        Intent i = new Intent(packageContext, StartActivity.class);
        i.putExtra(EXTRA_PORT, port);
        return i;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mStateTextView = (TextView)findViewById(R.id.state_text_view);
        mIPTextView = (TextView)findViewById(R.id.ip_text_view);
        mPortTextView = (TextView)findViewById(R.id.port_text_view);

        mPort = getPort(getIntent()); // get input port from ConnectActivity

        mConfigurationCard = (CardView)findViewById(R.id.sensor_cfg_card);

        mConfigurationSpinner = (Spinner)findViewById(R.id.configurations_spinner);
        mConfigurationSpinner.setOnItemSelectedListener(spinnerItemSelected);
        addItemsToSpinner();

        mNewConfigurationButton = (FloatingActionButton) findViewById(R.id.new_configuration_button);
        mNewConfigurationButton.setOnClickListener(newButtonListener);

        mStartSensingButton = (Button)findViewById(R.id.start_sensing_button);
        mStartSensingButton.setOnClickListener(startButtonListenser);

        mStateTextView.setText("Listening...");
        mPortTextView.setText(String.valueOf(mPort));
        if (DEBUG == false) {
            mConfigurationCard.setVisibility(View.INVISIBLE);
            mStartSensingButton.setVisibility(View.INVISIBLE);
        }
        StartAndBindService();
    }

    private View.OnClickListener newButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(StartActivity.this, NewConfigurationActivity.class);
            startActivityForResult(intent,NEW_CFG_REQUEST);
        }
    };

    private View.OnClickListener startButtonListenser = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println("StartButton Clicked");
            System.out.println(mSelectedSensorCfg.getConfigName());
            System.out.println(mSelectedSensorCfg.getSensorState());
            sendMessageToService(mSelectedSensorCfg.getSensorState());
            Intent intent = SensingActivity.newIntent(StartActivity.this, mSelectedSensorCfg);
            startActivity(intent);

        }
    };

    private AdapterView.OnItemSelectedListener spinnerItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            String selectedCfgName = adapterView.getItemAtPosition(pos).toString();
            SensorConfigurationLab sensorConfigurationLab = SensorConfigurationLab.get(StartActivity.this);
            mSelectedSensorCfg =  sensorConfigurationLab.getCfg(selectedCfgName);
            String str = mSelectedSensorCfg.getSensorState();
            if (str.length() == 3) {
                for (char c: str.toCharArray()) {
                    if (c != '0' && c != '1') {
                        // Configuration ERROR
                        return;
                    }
                }
            }
            mState =  str;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_CFG_REQUEST) {
            if (resultCode == RESULT_OK) {
                addItemsToSpinner();
            }
        }
    }

    public void addItemsToSpinner() {
        List<String> list = new ArrayList<String>();
        SensorConfigurationLab sensorCfgLab = SensorConfigurationLab.get(StartActivity.this);
        list = sensorCfgLab.getSensorCfgNameList();
        ArrayAdapter<String> cfgsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        cfgsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mConfigurationSpinner.setAdapter(cfgsAdapter);
    }

    public static int getPort(Intent result) {
        return result.getIntExtra(EXTRA_PORT, 5001);
    }

    private void updateUI(Message msg) {
        Bundle bundle = msg.getData();
        String ipAddr = bundle.getString(SocketChannelService.CLIENT_IP);
        mStateTextView.setText("Connected");
        mIPTextView.setText(ipAddr);
        mConfigurationCard.setVisibility(View.VISIBLE);
        mStartSensingButton.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        }
        catch (Throwable t) {
            Log.e("StartActivity", "Failed to unbind from the service", t);
        }
    }
}
