package com.laocuo.glassball.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

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

    private ViewDisappearListener l;
    private float mAlpha = 1.0f;
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

    public void remove() {
        ValueAnimator ani = ValueAnimator.ofFloat(1.0f, 0);
        ani.setDuration(500);
        ani.setInterpolator(new LinearInterpolator());
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAlpha = (float) valueAnimator.getAnimatedValue();
                if (mAlpha > 0) {
                    BrickView.this.setAlpha(mAlpha);
//                    BrickView.this.setScaleX(mAlpha);
//                    BrickView.this.setScaleY(mAlpha);
                } else {
                    if (l != null) {
                        l.onViewDisappear(BrickView.this);
                        l = null;
                    }
                }
            }
        });
        ani.start();
    }

    public void setDisappearListener(ViewDisappearListener l) {
        this.l = l;
    }
}
