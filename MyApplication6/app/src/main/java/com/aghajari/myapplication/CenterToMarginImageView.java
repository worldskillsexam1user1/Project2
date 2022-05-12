package com.aghajari.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CenterToMarginImageView extends AppCompatImageView {
    public CenterToMarginImageView(@NonNull Context context) {
        super(context);
    }

    public CenterToMarginImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterToMarginImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        if (lp.gravity == Gravity.CENTER) {
            lp.leftMargin += getContext().getResources().getDisplayMetrics().widthPixels / 2 - getMeasuredWidth() / 2;
            lp.topMargin += getContext().getResources().getDisplayMetrics().heightPixels / 2 - getMeasuredHeight() / 2;
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.width = getMeasuredWidth();
            lp.height = getMeasuredHeight();
            setLayoutParams(lp);
        }
    }
}
