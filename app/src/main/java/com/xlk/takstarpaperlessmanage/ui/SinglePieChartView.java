package com.xlk.takstarpaperlessmanage.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2021/7/15.
 * @desc
 */
public class SinglePieChartView extends View {
    public SinglePieChartView(Context context) {
        this(context,null);
    }

    public SinglePieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SinglePieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int minWidth = Math.min(w, h);
        minWidth *= 0.9;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
