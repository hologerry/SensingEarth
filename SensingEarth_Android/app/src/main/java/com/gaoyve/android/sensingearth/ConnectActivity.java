package com.gaoyve.android.sensingearth;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * SensingEarth
 * Created by Gerry on 11/19/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class ConnectActivity extends AppCompatActivity {
    private EditText mPortEditText;
    private Button mConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mPortEditText = (EditText) findViewById(R.id.port_edit_text);
        mConnectButton = (Button) findViewById(R.id.connect_button);
        mConnectButton.setOnClickListener(connectButtonListener);
    }

    private View.OnClickListener connectButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int port = Integer.parseInt(mPortEditText.getText().toString().trim());
            Intent intent = StartActivity.newIntent(ConnectActivity.this, port);
            startActivity(intent);
            System.out.println("ConnectButton Clicked");
        }
    };
}
