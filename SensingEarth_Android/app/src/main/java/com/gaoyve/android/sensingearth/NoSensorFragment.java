package com.gaoyve.android.sensingearth;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * SensingEarth
 * Created by Gerry on 12/1/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class NoSensorFragment extends Fragment {
    private CardView mNoSensorCard;
    private TextView mNoSensorText;
    private FloatingActionButton mNoSensorBackButon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_no_sensor, container, false);
        mNoSensorCard = (CardView) root.findViewById(R.id.no_sensor_card);
        mNoSensorText = (TextView) root.findViewById(R.id.no_sensor_text_view);
        mNoSensorBackButon = (FloatingActionButton) root.findViewById(R.id.no_sensor_back_button);
        mNoSensorBackButon.setOnClickListener(noSensorBackButonListenser);

        return root;
    }

    private View.OnClickListener noSensorBackButonListenser = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SensingActivity sensingActivity = (SensingActivity) getActivity();
            sensingActivity.finish();
        }
    };
}
