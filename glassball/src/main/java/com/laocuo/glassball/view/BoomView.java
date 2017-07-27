package com.laocuo.glassball.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.laocuo.glassball.R;
import com.laocuo.glassball.utils.L;

/**
 * Created by Laocuo on 7/27/17.
 */

public class BoomView extends View {
    private Paint mPaint;
    private Bitmap mBitmap;
    private int w,h;
    private Point mCenter;
    private float mAlpha = 1.0f;

    private ViewDisappearListener l;

    public BoomView(Context context) {
        this(context, null);
    }

    public BoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
        mBitmap = getBitmapFromVectorDrawable(context, R.drawable.peng);
        if (mBitmap != null) {
            w = mBitmap.getWidth();
            h = mBitmap.getHeight();
        }
    }

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable d = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(),
                d.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    public void setCenter(Point center) {
        this.mCenter = center;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public Point getCenter() {
        return mCenter;
    }

    public void remove() {
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1.0f);
        ani.setDuration(200);
        ani.setInterpolator(new LinearInterpolator());
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAlpha = (float) valueAnimator.getAnimatedValue();
                if (mAlpha < 1.0f) {
//                    BoomView.this.setScaleX(mAlpha);
//                    BoomView.this.setScaleY(mAlpha);
                } else {
                    if (l != null) {
                        l.onViewDisappear(BoomView.this);
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
