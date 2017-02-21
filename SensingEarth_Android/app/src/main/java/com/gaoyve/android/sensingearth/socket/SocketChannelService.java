package com.gaoyve.android.sensingearth.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.gaoyve.android.sensingearth.activity.StartActivity;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * SensingEarth
 * Created by Gerry on 11/29/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class SocketChannelService extends Service {
    // Msg what
    public static final int MSG_REGISTER_ACTIVITY = 1;
    public static final int MSG_UNREGISTER_ACTIVITY = 2;
    public static final int MSG_SEND_TO_CLIENT = 3;  // MSG_FROM_ACTIVITY
    public static final int MSG_READ_FROM_CLIENT = 4;  // MSG_SEND_TO_ACTIVITY
    public static final int SOCKET_INFO_IP = 5;
    // Bundle str1 arg
    public static final String MSG_SEND_TO_ACTIVITY = "com.gaoyve.android.sensingearth.msg1";
    public static final String MSG_FROM_ACTIVITY = "com.gaoyve.android.sensingearth.msg2";
    public static final String CLIENT_IP = "com.gaoyve.android.sensingearth.addr";

    static boolean isServiceRuning = false;
    public static boolean isServiceRuning() {
        return isServiceRuning;
    }

    private ServerSocketChannel mServerSocketChannel;
    private SocketChannel mSocketChannel;
    private ByteBuffer mReadBuffer;
    private ByteBuffer mWriteBuffer;
    private static final Charset charset = Charset.forName("UTF-8");
    private InetAddress mClientAddr = null;
    private boolean mSocketChannelChanged = false;

    ArrayList<Messenger> mActivityClients = new ArrayList<Messenger>();

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_ACTIVITY:
                    mActivityClients.add(msg.replyTo);
                    System.out.println("add StartActivity");
                    System.out.println(mActivityClients);
                    break;
                case MSG_UNREGISTER_ACTIVITY:
                    mActivityClients.remove(msg.replyTo);
                    break;
                case MSG_SEND_TO_CLIENT:
                    prepareMessageToClient(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void sendIPMessageToUI() {
        String stringAddr = mClientAddr.toString().substring(1);
        for (int i = mActivityClients.size()-1; i >= 0; i--) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString(CLIENT_IP, stringAddr);
                Message msg = Message.obtain(null, SOCKET_INFO_IP);
                msg.setData(bundle);
                mActivityClients.get(i).send(msg);
            } catch (RemoteException e) {
                System.out.println("Error in send client message to UI");
                mActivityClients.remove(i);
            }
        }
    }
    private void sendMessageToUI(String stringMsg) {
        for (int i = mActivityClients.size()-1; i >= 0; i--) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString(MSG_SEND_TO_ACTIVITY, stringMsg);
                Message msg = Message.obtain(null, MSG_READ_FROM_CLIENT);
                msg.setData(bundle);
                mActivityClients.get(i).send(msg);
            } catch (RemoteException e) {
                System.out.println("Error in send client message to UI");
                mActivityClients.remove(i);
            }
        }
    }

    private void prepareMessageToClient(Message msg) {
        Bundle b = msg.getData();
        String stringMsg = b.getString(MSG_FROM_ACTIVITY);
        if (stringMsg.length() == 3) {
            mWriteBuffer = ByteBuffer.allocate(3);
            mWriteBuffer.clear();
            mWriteBuffer.put(stringMsg.getBytes(charset));
            mWriteBuffer.flip();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyService", "Service Started.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service", "Received start id " + startId + ": " + intent);

        Runnable connect = new connectSocket();
        new Thread(connect).start();
        isServiceRuning = true;

        return START_STICKY; // run until explicitly stopped.
    }

    class connectSocket implements Runnable {

        @Override
        public void run() {
            try {
                mServerSocketChannel = ServerSocketChannel.open();

                mServerSocketChannel.socket().bind(new InetSocketAddress(StartActivity.mPort));
                mServerSocketChannel.configureBlocking(false); // non-blocking mode

                while(mSocketChannel == null) {
                    mSocketChannel = mServerSocketChannel.accept();
                    mSocketChannelChanged = true;
                }

                while(true){
                    if(mSocketChannel != null){
                        if (mClientAddr == null || mSocketChannelChanged) {
                            mClientAddr = mSocketChannel.socket().getInetAddress();
                            sendIPMessageToUI();
                        }

                        // write data
                        while (mWriteBuffer != null && mWriteBuffer.hasRemaining()) {
                            mSocketChannel.write(mWriteBuffer);
                        }

                        // read data
                        mReadBuffer = ByteBuffer.allocate(20);
                        int bytesRead = mSocketChannel.read(mReadBuffer);
                        if (bytesRead != -1) {
                            String stringMsg = new String(mReadBuffer.array(), charset);
                            System.out.println(stringMsg);
                            sendMessageToUI(stringMsg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MyService", "Service Stopped.");
        isServiceRuning = false;
    }

}
