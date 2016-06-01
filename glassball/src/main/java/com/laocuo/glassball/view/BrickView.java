package com.laocuo.glassball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.laocuo.glassball.utils.L;


/**
 * Created by Laocuo on 2016/5/11.
 */
public class BrickView extends View {
    private int width, height;
    private Paint mPaint;
    private RectF mRect;
    private int mBrickIndex;
    private int mColor = Color.GRAY;
    private boolean mBlackSheep;

    public GlassGameView.Center getmCenter() {
        return mCenter;
    }

    private GlassGameView.Center mCenter;
    public int getBrickIndex() {
        return mBrickIndex;
    }

    public void setBrickIndex(int mBrickIndex) {
        this.mBrickIndex = mBrickIndex;
    }

    public BrickView(Context context, AttributeSet attrs, int w, int h, int color) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mColor = color;
        mPaint.setColor(mColor);
        width = w;
        height = h;
        mRect = new RectF(0,0,width,height);
        int r = (int) (Math.random() * 5);
        if (r < 2) {
            mBlackSheep = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawRect(mRect, mPaint);
        canvas.drawRoundRect(mRect, height/4, height/4, mPaint);
        if (mBlackSheep) {
            mPaint.setColor(Color.BLACK);
            canvas.drawCircle(width/2, height/2, height/4, mPaint);
            mPaint.setColor(mColor);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    public void setCenter(GlassGameView.Center mC) {
        mCenter = mC;
    }

    public boolean isBlackSheep() {
        return mBlackSheep;
    }
}
