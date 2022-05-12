package com.aghajari.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

public class CenterToMarginTextView extends AppCompatTextView {
    public CenterToMarginTextView(@NonNull Context context) {
        super(context);
    }

    public CenterToMarginTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterToMarginTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        if (lp.gravity == Gravity.CENTER) {
            lp.leftMargin += getContext().getResources().getDisplayMetrics().widthPixels / 2 - getMeasuredWidth() / 2;
            lp.topMargin += getContext().getResources().getDisplayMetrics().heightPixels / 2 - getMeasuredHeight() / 2;
            lp.width = getMeasuredWidth();
            lp.height = getMeasuredHeight();
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            setLayoutParams(lp);
        }
    }
}
