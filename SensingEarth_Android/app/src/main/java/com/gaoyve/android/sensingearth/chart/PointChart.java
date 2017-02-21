package com.gaoyve.android.sensingearth.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.gaoyve.android.sensingearth.R;

import java.util.ArrayList;

/**
 * SensingEarth
 * Created by Gerry on 11/29/16.
 * Copyright © 2016 Gerry. All rights reserved.
 */

public class PointChart extends View {
    private int mWidth;
    private int mHeight;
    private float mAxisFontSize = 25;
    private int mAxisColor = R.color.colorAxis;
    private int mNormalColor = R.color.colorNormal;
    private int mAbnormalColor = R.color.colorAbnormal;
    private String[] mXAxis;
    private String[] mYAxis;
    private ArrayList<Double> mPointValues;
    private Double mThresholdHigh;
    private Double mThresholdLow;
    private Double mMaxValue;
    private Double mMinValue;
    private float mPointRadius = 5;
    private String mNoDataMsg = "no data";

    public PointChart(Context context) {
        this(context, null);
    }

    public PointChart(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PointChart(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            throw new IllegalArgumentException("width must be EXACTLY, you should set like android:width=\"200dp\"");
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            throw new IllegalArgumentException("light must be EXACTLY, you should set like android:width=\"200dp\"");
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // No Axis
        if (mXAxis.length == 0 || mYAxis.length == 0) {
            throw new IllegalArgumentException("X or Y items is null");
        }

        // Draw Axis
        Paint axisPaint = new Paint();
        axisPaint.setTextSize(mAxisFontSize);
        axisPaint.setColor(getResources().getColor(mAxisColor));
        Paint noDataPaint = new Paint();
        noDataPaint.setTextSize(50);
        noDataPaint.setColor(getResources().getColor(mNormalColor));
        if (mPointValues == null || mPointValues.size() == 0) {
            int textLength = (int) axisPaint.measureText(mNoDataMsg);
            canvas.drawText(mNoDataMsg, mWidth/2 - textLength/2, mHeight/2, noDataPaint);
        } else {

            // Draw Y Axis
            float[] yAxis = new float[mYAxis.length];
            int yInterval = (int) (2 * mAxisFontSize);

//            float zero = 0f;
            for (int i = 0; i < mYAxis.length; i++) {
                System.out.println("Drawing Y Axis....");
                canvas.drawText(mYAxis[i], 0, (mYAxis.length - i) * yInterval - mAxisFontSize, axisPaint);
                if (Double.parseDouble(mYAxis[i]) == 0) {
//                    zero = (mYAxis.length - i) * yInterval - mAxisFontSize; // use to debug
                }
            }

            System.out.println("long..." + (mYAxis.length * yInterval+yInterval) );

            // Draw X Axis
            int[] xPoints = new int[mXAxis.length];
            int xItemX = (int) axisPaint.measureText(mYAxis[1]);
            int xOffset = 25;
            int xInterval = (int) (mWidth - 1.5 * xItemX) / (mXAxis.length);
            int xItemY = (int) (mAxisFontSize + mYAxis.length * yInterval);

            for (int i = 0; i < mXAxis.length; i++) {
                System.out.println("Drawing X Axis....");
                canvas.drawText(mXAxis[i], (float)(i * xInterval + xItemX + xOffset), xItemY, axisPaint);
                xPoints[i] = (int) ((i+0.5) * xInterval + xItemX);
            }

            // Draw Points
            float[] yPoints = new float[mPointValues.size()]; // store points y value
            Paint normalPointPaint = new Paint();
            normalPointPaint.setColor(getResources().getColor(mNormalColor));
            normalPointPaint.setStyle(Paint.Style.FILL);
            Paint abnormalPointPaint = new Paint();
            abnormalPointPaint.setColor(getResources().getColor(mAbnormalColor));
            abnormalPointPaint.setStyle(Paint.Style.FILL);

            for (int i = 0; i < mPointValues.size(); i++) {
                System.out.println("Drawing points....");
                // Draw points
                Double value = mPointValues.get(i);

                float ratio = (float) ((value - mMinValue) / (mMaxValue-mMinValue));
                yPoints[i] = (float) ((1 - ratio) * (mYAxis.length * yInterval));
                if (value <= 0) {
                    yPoints[i] -= yInterval;
                }
                if (value != 0 && value <= (mMaxValue-mMinValue) /10) {
                    yPoints[i] -= mAxisFontSize;
                }
                if (value > mThresholdHigh || value < mThresholdLow) {
                    canvas.drawCircle(xPoints[i], yPoints[i]+(mPointRadius)/2, mPointRadius, abnormalPointPaint);
                } else {
                    canvas.drawCircle(xPoints[i], yPoints[i]+(mPointRadius)/2, mPointRadius, normalPointPaint);
                }

//                System.out.println(zero);
                System.out.println("points " + xPoints[i] + " " + yPoints[i] + " " + ratio);
            }
        }
    }

    public void setPointValues(ArrayList<Double> pointValues) {
        mPointValues = pointValues;
        invalidate();
    }

    public void setXAxis(String[] XAxis) {
        mXAxis = XAxis;
    }

    public void setYAxis(String[] YAxis) {
        mYAxis = YAxis;
        mMaxValue = Double.parseDouble(mYAxis[mYAxis.length-1]);
        mMinValue = Double.parseDouble(mYAxis[0]);
    }

    public void setThresholdLow(Double thresholdLow) {
        mThresholdLow = thresholdLow;
    }

    public void setThresholdHigh(Double thresholdHigh) {
        mThresholdHigh = thresholdHigh;
    }

}
