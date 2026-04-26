package com.newtermux.features;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/** Draws a mini terminal mockup for the theme picker grid. */
public class ThemePreviewView extends View {

    private int mBg, mFg, mToolbar, mGreen, mCursor, mAccent;
    private boolean mActive;

    private final Paint mFill   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mText   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBorder = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ThemePreviewView(Context context) { super(context); init(); }
    public ThemePreviewView(Context context, AttributeSet a) { super(context, a); init(); }

    private void init() {
        mBorder.setStyle(Paint.Style.STROKE);
        mText.setTypeface(Typeface.MONOSPACE);
    }

    /** colors: {bg, fg, toolbar, green, cursor} */
    public void setTheme(int[] colors, boolean active, int accentColor) {
        mBg = colors[0]; mFg = colors[1]; mToolbar = colors[2];
        mGreen = colors[3]; mCursor = colors[4];
        mActive = active;
        mAccent = accentColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float d = getResources().getDisplayMetrics().density;
        int w = getWidth(), h = getHeight();

        // Terminal background
        mFill.setColor(mBg);
        canvas.drawRect(0, 0, w, h, mFill);

        // Toolbar strip
        float toolH = 11 * d;
        mFill.setColor(mToolbar);
        canvas.drawRect(0, 0, w, toolH, mFill);

        // Window-control dots (fixed colors, always visible)
        float dotY = toolH / 2f;
        float dotR = 1.8f * d;
        mFill.setColor(0xFFFF5F57); canvas.drawCircle(5.5f * d, dotY, dotR, mFill);
        mFill.setColor(0xFFFFBD2E); canvas.drawCircle(10.5f * d, dotY, dotR, mFill);
        mFill.setColor(0xFF28CA41); canvas.drawCircle(15.5f * d, dotY, dotR, mFill);

        // Terminal text lines
        float ts = 6f * d;
        mText.setTextSize(ts);
        float x = 4 * d;
        float y = toolH + 8 * d;
        float lh = 8 * d;

        // Line 1: prompt + command
        mText.setColor(mGreen);
        String prompt = "$ ";
        canvas.drawText(prompt, x, y, mText);
        float pw = mText.measureText(prompt);
        mText.setColor(mFg);
        canvas.drawText("ls -la", x + pw, y, mText);

        // Line 2: dim output
        y += lh;
        mText.setColor(dim(mFg));
        canvas.drawText("total 8", x, y, mText);

        // Line 3: colored directory entry
        y += lh;
        mText.setColor(mGreen);
        String perm = "drwx ";
        canvas.drawText(perm, x, y, mText);
        float permW = mText.measureText(perm);
        mText.setColor(mFg);
        canvas.drawText("home", x + permW, y, mText);

        // Line 4: next prompt + block cursor
        if (y + lh + 2 * d < h) {
            y += lh;
            mText.setColor(mGreen);
            canvas.drawText(prompt, x, y, mText);
            float cx2 = x + mText.measureText(prompt);
            mFill.setColor(mCursor);
            canvas.drawRect(cx2, y - ts, cx2 + 5 * d, y + 1.5f * d, mFill);
        }

        // Active border
        if (mActive) {
            float sw = 2.5f * d;
            mBorder.setColor(mAccent);
            mBorder.setStrokeWidth(sw);
            float half = sw / 2f;
            canvas.drawRect(half, half, w - half, h - half, mBorder);
        }
    }

    private static int dim(int color) {
        int r = (int)(((color >> 16) & 0xFF) * 0.55f);
        int g = (int)(((color >> 8)  & 0xFF) * 0.55f);
        int b = (int)((color & 0xFF) * 0.55f);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
