package com.newtermux.features;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/** Draws a colored circle swatch for the accent color picker grid. */
public class AccentSwatchView extends View {

    private int mColor;
    private boolean mActive;
    private boolean mIsCustomSlot;

    private final Paint mFill   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mCheck  = new Paint(Paint.ANTI_ALIAS_FLAG);

    public AccentSwatchView(Context context) { super(context); init(); }
    public AccentSwatchView(Context context, AttributeSet a) { super(context, a); init(); }

    private void init() {
        mBorder.setStyle(Paint.Style.STROKE);
        mCheck.setColor(0xFFFFFFFF);
        mCheck.setTextAlign(Paint.Align.CENTER);
        mCheck.setFakeBoldText(true);
    }

    public void setColor(int color, boolean active, boolean isCustomSlot) {
        mColor = color;
        mActive = active;
        mIsCustomSlot = isCustomSlot;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float d = getResources().getDisplayMetrics().density;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(cx, cy) - 3 * d;

        if (mIsCustomSlot) {
            // Rainbow sweep gradient for the custom slot
            SweepGradient sg = new SweepGradient(cx, cy,
                new int[]{0xFFFF0000, 0xFFFF8C00, 0xFFFFFF00,
                           0xFF00CC00, 0xFF0088FF, 0xFF8800FF, 0xFFFF0000},
                null);
            mFill.setShader(sg);
            canvas.drawCircle(cx, cy, radius, mFill);
            mFill.setShader(null);
        } else {
            mFill.setColor(mColor);
            canvas.drawCircle(cx, cy, radius, mFill);
        }

        if (mActive) {
            float sw = 2.5f * d;
            mBorder.setColor(0xFFFFFFFF);
            mBorder.setStrokeWidth(sw);
            canvas.drawCircle(cx, cy, radius - sw / 2f, mBorder);

            mCheck.setTextSize(14 * d);
            canvas.drawText("✓", cx, cy + 5 * d, mCheck);
        }
    }
}
