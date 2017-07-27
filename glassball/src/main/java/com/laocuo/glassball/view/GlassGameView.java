package com.laocuo.glassball.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.laocuo.glassball.R;
import com.laocuo.glassball.utils.L;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Laocuo on 2016/4/27.
 */
public class GlassGameView extends ViewGroup implements ViewDisappearListener {

    private int mBrickCount = 0;
    private final int BALL_MOVE_SETP_MIN = 6, BALL_MOVE_SETP_MAX = 30;
    private final int BALL_RADIUS = 50, BOARD_W = 400, BOARD_H = 70, BRICK_w = 180, BRICK_H = 180;
    private int ball_move_step = BALL_MOVE_SETP_MIN;
    private boolean bUpdateBall = false;
    private boolean bFirstRun = true;
    private int mScreenWidth, mScreenHeight;
    private BallView mBallView;
    private BoardView mBoardView;
    private Center mBallCenter, mBoardCenter;
    private int mBallCenterXDir = ball_move_step, mBallCenterYDir = -1*ball_move_step;
    private int mBallR = BALL_RADIUS;
    private int mBoardW = BOARD_W, mBoardH = BOARD_H;
    private int mBrickW = BRICK_w, mBrickH = BRICK_H;
    private HashMap<Integer, BrickView> mBrickMap = new HashMap<>();
    private Context mContext;
    private AttributeSet mAttributeSet;
    public GlassGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mAttributeSet = attrs;
        init(context, attrs);
        post(new Runnable() {
            @Override
            public void run() {
                addInitBricks();
            }
        });
    }

    private void addInitBricks() {
        mCenterBrickW = mBrickWGap - mBrickW/2;
        mCenterBrickH = mBrickH/2;
        for(int i=0;i<20;i++) {
            addBrick();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mBallView = new BallView(context, attrs);
        mBallView.setRaidus(mBallR);
        addView(mBallView);
        mBallCenter = new Center();
        mBoardView = new BoardView(context, attrs);
        mBoardView.setSize(mBoardW, mBoardH);
        addView(mBoardView);
        mBoardCenter = new Center();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        if (bFirstRun == true) {
            bFirstRun = false;
            mScreenWidth = getWidth();
            mScreenHeight = getHeight();
            mBallCenter.setXY(mScreenWidth / 2, mScreenHeight - mBoardH - mBallR);
            mBoardCenter.setXY(mScreenWidth / 2, mScreenHeight - mBoardH / 2);
            mBrickWGap = (mScreenWidth - (mScreenWidth/mBrickW)*mBrickW)/2;
            mCenterBrickW = mBrickWGap - mBrickW/2;
            mCenterBrickH = mBrickH/2;
        }
        for(int i=0;i<count;i++) {
            View child = getChildAt(i);
            int cl=0,ct=0,cr=0,cb=0;
            if (child instanceof BallView) {
                cl = mBallCenter.x - mBallR;
                ct = mBallCenter.y - mBallR;
                cr = mBallCenter.x + mBallR;
                cb = mBallCenter.y + mBallR;
            } else if (child instanceof BoardView) {
                cl = mBoardCenter.x - mBoardW/2;
                ct = mBoardCenter.y - mBoardH/2;
                cr = mBoardCenter.x + mBoardW/2;
                cb = mBoardCenter.y + mBoardH/2;
            } else if (child instanceof BrickView) {
                Center c = ((BrickView) child).getmCenter();
                cl = c.x - mBrickW / 2;
                ct = c.y - mBrickH / 2;
                cr = c.x + mBrickW / 2;
                cb = c.y + mBrickH / 2;
            } else if (child instanceof BoomView) {
                Point p = ((BoomView) child).getCenter();
                int w = ((BoomView) child).getW();
                int h = ((BoomView) child).getH();
                cl = p.x - w / 2;
                ct = p.y - h / 2;
                cr = p.x + w / 2;
                cb = p.y + h / 2;
            } else {
                continue;
            }
            child.layout(cl,ct,cr,cb);
        }
    }

    public void startUpdateBall() {
        if (false == bUpdateBall) {
            bUpdateBall = true;
            reset();
            post(mUpdateBall);
        }
    }

    public void stopUpdateBall() {
        bUpdateBall = false;
    }

    @Override
    public void onViewDisappear(View v) {
        removeView(v);
    }

    public class Center {
        public int x,y;
        public Center() {
        }
        public Center(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float mXdown = 0f;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bUpdateBall == false) {
            return super.onTouchEvent(event);
        }
        float eventX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXdown = eventX;
                break;
            case MotionEvent.ACTION_MOVE:
                float mXmove = eventX - mXdown;
                updateBoardCenter(mXmove);
                mXdown = eventX;
                break;
            case MotionEvent.ACTION_UP:
                mXdown = 0;
                break;
        }
        return true;
    }

    private void updateBoardCenter(float mXmove) {
        int x = (int) (mBoardCenter.x+mXmove);
        if (x > mScreenWidth - mBoardW/2) {
            x = mScreenWidth - mBoardW/2;
        } else if (x < mBoardW/2) {
            x = mBoardW/2;
        }
        mBoardCenter.x = x;
    }

    private Runnable mUpdateBall = new Runnable() {
        @Override
        public void run() {
            if (bUpdateBall == true) {
                if (true == updateBallCenter()) {
                    requestLayout();
                    post(this);
                } else {
                    bUpdateBall = false;
                }
            }
        }
    };

    private boolean updateBallCenter() {
        int step = ball_move_step;
        mBallCenterXDir = step * mBallCenterXDir / Math.abs(mBallCenterXDir);
        mBallCenterYDir = step * mBallCenterYDir / Math.abs(mBallCenterYDir);
        int x = mBallCenter.x + mBallCenterXDir;
        int y = mBallCenter.y + mBallCenterYDir;
        if (x+mBallR > mScreenWidth) {
            mBallCenterXDir = -1*step;
        } else if (x-mBallR < 0) {
            mBallCenterXDir = step;
        }
        if (y+mBallR > mScreenHeight - mBoardH) {
            if (Math.abs(mBallCenter.x - mBoardCenter.x) < (mBoardW/2 + mBallR)) {
                mBallCenterYDir = -1*step;
                ball_move_step = Math.min(ball_move_step+1,BALL_MOVE_SETP_MAX);
                if (mBrickMap.size() < 1) {
                    addInitBricks();
                }
            } else {
//                reset();
                return false;
            }
        } else if (y-mBallR < 0) {
            mBallCenterYDir = step;
        }
        x = mBallCenter.x + mBallCenterXDir;
        y = mBallCenter.y + mBallCenterYDir;
        int isTouch = checkIfTouchBrick(new Center(x, y), step);
        if (isTouch <= 0) {
            mBallCenter.x += mBallCenterXDir;
            mBallCenter.y += mBallCenterYDir;
        }
        return true;
    }

    private float mMinDistanceBetweenBallAndBrick = -1;
    private int mMinIndex = -1;

    private int checkIfTouchBrick(Center c, int step) {
        int ret = 0;
        if (mBrickMap.size() < 1) {
            return 0;
        }
        Map map = mBrickMap;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            int key = (int) entry.getKey();
            BrickView val = (BrickView) entry.getValue();
            float distance = calcDistance(mBallCenter, val.getmCenter());
            if (mMinDistanceBetweenBallAndBrick < 0) {
                mMinDistanceBetweenBallAndBrick = distance;
                mMinIndex = key;
            } else if (mMinDistanceBetweenBallAndBrick > distance) {
                    mMinDistanceBetweenBallAndBrick = distance;
                    mMinIndex = key;
            }
        }
        BrickView bv = mBrickMap.get(mMinIndex);
        int xDis = Math.abs(c.x - bv.getmCenter().x);
        int yDis = Math.abs(c.y - bv.getmCenter().y);

        //球和砖块接触
        if ((xDis <= mBallR + mBrickW/2) && (yDis <= mBallR + mBrickH/2)) {
            int realxgapDis = Math.abs(mBallCenter.x - bv.getmCenter().x) - mBallR - mBrickW/2;
            int realygapDis = Math.abs(mBallCenter.y - bv.getmCenter().y) - mBallR - mBrickH/2;
            int minGap = 0;

            if (realxgapDis >= 0 && realygapDis >=0) {
                //球碰到砖块角
                minGap = Math.min(realxgapDis, realygapDis);
                if (realxgapDis > realygapDis) {
                    ret = 2;
                } else if (realxgapDis < realygapDis) {
                    ret = 1;
                } else {
                    ret = 3;
                }
            } else if (realxgapDis >= 0) {
                //球碰到砖块左面或者右面
                ret = 2;
                minGap = realxgapDis;
            } else if (realygapDis >= 0) {
                //球碰到砖块上面或者下面
                ret = 1;
                minGap = realygapDis;
            } else {
                L.d("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
            mBallCenter.x += minGap * (mBallCenterXDir/step);
            mBallCenter.y += minGap * (mBallCenterYDir/step);
            Point touch = new Point(mBallCenter.x, mBallCenter.y);

            if (ret == 1) {
                touch.y += mBallR * (mBallCenterYDir/step);
                mBallCenterYDir = -1 * mBallCenterYDir;
            } else if (ret == 2) {
                touch.x += mBallR * (mBallCenterXDir/step);
                mBallCenterXDir = -1 * mBallCenterXDir;
            } else if (ret == 3) {
                touch.x += mBallR * (mBallCenterXDir/step);
                touch.y += mBallR * (mBallCenterYDir/step);
                mBallCenterXDir = -1 * mBallCenterXDir;
                mBallCenterYDir = -1 * mBallCenterYDir;
            }
            if (bv.isBlackSheep()) {
                reduceBoardAndBallSize();
            }
            if (ret > 0) {
                BoomView boomView = new BoomView(mContext);
                boomView.setCenter(touch);
                boomView.setDisappearListener(this);
                addView(boomView);
                boomView.remove();
            }
            mBrickMap.remove(mMinIndex);
            bv.remove();
        }

        mMinDistanceBetweenBallAndBrick = -1;
        mMinIndex = -1;
        return ret;
    }

    private void reduceBoardAndBallSize() {
        if (mBoardW > BOARD_H*3) {
            mBoardW -= 10;
            mBoardView.setSize(mBoardW, mBoardH);
        }
        if (mBallR > BALL_RADIUS/2) {
            mBallR -= 1;
            mBallView.setRaidus(mBallR);
        }
    }

    private float calcDistance(Center p1, Center p2) {
        float x = p1.x - p2.x;
        float y = p1.y - p2.y;
        return (float) Math.hypot(x, y);
    }

    private void reset() {
        mBoardW = BOARD_W;
        mBoardView.setSize(mBoardW, mBoardH);
        mBallR = BALL_RADIUS;
        mBallView.setRaidus(mBallR);
        mBallCenter.setXY(mScreenWidth / 2, mScreenHeight - mBoardH - mBallR);
        mBoardCenter.setXY(mScreenWidth / 2, mScreenHeight - mBoardH / 2);
        ball_move_step = BALL_MOVE_SETP_MIN;
        mBallCenterXDir = ball_move_step;
        mBallCenterYDir = -1*ball_move_step;
//        requestLayout();
    }

    private List<Integer> mColorList = Arrays.asList(
            R.color.fuchsia,
            R.color.salmon,
            R.color.orchid,
            R.color.firebrick,
            R.color.lightblue,
            R.color.lightgreen,
            R.color.purple,
            R.color.mediumslateblue,
            R.color.olivedrab,
            R.color.turquoise,
            R.color.aqua,
            R.color.lawngreen,
            R.color.brown,
            R.color.indigo,
            R.color.darkgreen
    );
    public void addBrick() {
        mBrickCount++;
        int color = mColorList.get((int) (Math.random()*mColorList.size()));
        BrickView mBrick = new BrickView(mContext, mAttributeSet, mBrickW, mBrickH, getResources().getColor(color));
        mBrick.setBrickIndex(mBrickCount);
        mBrick.setCenter(getBrickCenter());
        mBrick.setDisappearListener(this);
        mBrickMap.put(mBrickCount, mBrick);
        addView(mBrick);
        if (false == bUpdateBall) {
            requestLayout();
        }
    }

    private int mBrickWGap = -1, mCenterBrickW = -1, mCenterBrickH = -1;
    private Center getBrickCenter() {
        Center c = new Center();
        mCenterBrickW += mBrickW;
        if (mCenterBrickW < mBrickW / 2) {
            mCenterBrickW = mBrickWGap + mBrickW/2;
        } else if (mCenterBrickW > mScreenWidth-mBrickW/2) {
            mCenterBrickW = mBrickWGap + mBrickW/2;
            mCenterBrickH += mBrickH;
        }
        c.setXY(mCenterBrickW, mCenterBrickH);
        return c;
    }
}
