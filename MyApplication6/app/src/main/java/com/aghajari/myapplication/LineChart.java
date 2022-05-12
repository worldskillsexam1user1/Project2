package com.aghajari.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LineChart extends View {

    final List<ChartData> list = new ArrayList<>();
    float max = -1;
    float firstPointPadding = 16;
    float lastPointPadding = 16;

    final Paint linePaint = new Paint();

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(2 * context.getResources().getDisplayMetrics().density);

        lastPointPadding *= context.getResources().getDisplayMetrics().density;
        firstPointPadding *= context.getResources().getDisplayMetrics().density;

        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            list.add(new ChartData(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)), (float) rnd.nextInt(10), (float)  rnd.nextInt(20),  (float) rnd.nextInt(40),  (float) rnd.nextInt(3)));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (list.size() == 0) return;

        float maxData = max;
        if (maxData == -1) {
            maxData = 0;
            for (ChartData d : list) {
                for (float a : d.data)
                    maxData = Math.max(maxData, a);
            }
        }
        maxData = Math.max(1, maxData);


        float pr = 0;
        float left = getPaddingLeft() + pr;
        float top = getPaddingTop() + pr;
        float right = getMeasuredWidth() - getPaddingRight() - pr;
        float bottom = getMeasuredHeight() - getPaddingBottom();
        float height = bottom - top;

        float height2 = (int) (height - pr);
        float linePadding = height2 / (maxData + 1);

        left += firstPointPadding;
        right -= lastPointPadding;
        float width = right - left;


        for (ChartData name : list) {
            final int count = name.data.length;
            float xPadding = (width - count * pr) / Math.max(count - 1, 1);

            for (int i = 0; i < count; i++) {
                float y = (maxData - name.data[i]) * linePadding + top;
                float x = left + (i * xPadding) + (i * pr);

                if (i + 1 < count) {
                    float y2 = (maxData - name.data[i + 1]) * linePadding + top;
                    float x2 = left + ((i + 1) * xPadding) + ((i + 1) * pr);
                    canvas.drawLine(x, y, x2, y2, linePaint);
                }
            }
        }
    }

    public static class ChartData {
        float[] data;
        int color;

        public ChartData(int color, float... data) {
            this.data = data;
            this.color = color;
        }
    }
}
