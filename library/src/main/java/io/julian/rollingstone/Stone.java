package io.julian.rollingstone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * @author Zhu Liang
 */

class Stone {

    private RollingStoneView mRollingStoneView;
    private Drawable mDrawable;
    private OnStoneListener mOnStoneListener;
    private Rect mBounds;
    private RectF mSaveBounds;
    private ValueAnimator mFirstAnimator;
    private ValueAnimator mSecondAnimator;
    private int mDegrees;
    private boolean mForceStop;

    Stone(RollingStoneView rollingStoneView, Drawable drawable) {
        mRollingStoneView = rollingStoneView;
        mDrawable = drawable;
    }

    void setBounds(int left, int top, int right, int bottom) {
        mBounds = new Rect(left, top, right, bottom);
        mDrawable.setBounds(mBounds);
        final float radius = (float) Math.hypot(mBounds.width(), mBounds.height());
        final float saveLeft = mBounds.left - radius;
        final float saveTop = mBounds.top - (radius - mBounds.height());
        final float saveRight = saveLeft + 2 * radius;
        final float saveBottom = saveTop + radius;
        mSaveBounds = new RectF(saveLeft, saveTop, saveRight, saveBottom);
    }

    void setRollStyle(long firstHalfStartDelay, int firstHalfDuration,
                      long secondHalfStartDelay, int secondHalfDuration) {

        mFirstAnimator = ValueAnimator.ofInt(0, -90);
        mFirstAnimator.setDuration(firstHalfDuration).setStartDelay(firstHalfStartDelay);
        mFirstAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDegrees = (int) animation.getAnimatedValue();
                mRollingStoneView.postInvalidateOnAnimation();
            }
        });
        mFirstAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mForceStop && mOnStoneListener != null) {
                    mOnStoneListener.onFirstHalfEnd();
                }
            }
        });

        mSecondAnimator = ValueAnimator.ofInt(-90, 0);
        mSecondAnimator.setDuration(secondHalfDuration).setStartDelay(secondHalfStartDelay);
        mSecondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDegrees = (int) animation.getAnimatedValue();
                mRollingStoneView.postInvalidateOnAnimation();
            }
        });
        mSecondAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mForceStop && mOnStoneListener != null) {
                    mOnStoneListener.onSecondHalfEnd();
                }
            }
        });
    }

    void draw(Canvas canvas) {
        int saveCount;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveCount = canvas.saveLayer(mSaveBounds, null);

        } else {
            saveCount = canvas.saveLayer(mSaveBounds, null, Canvas.ALL_SAVE_FLAG);
        }
        canvas.rotate(mDegrees, mBounds.left, mBounds.bottom);
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    void startAnimation() {
        stopFirstHalf();
        stopSecondHalf();
        startFirstHalf();
    }

    void stopAnimation() {
        stopFirstHalf();
        stopSecondHalf();
    }

    void startFirstHalf() {
        mForceStop = false;
        mFirstAnimator.start();
    }

    private void stopFirstHalf() {
        mForceStop = true;
        mFirstAnimator.end();
    }

    void startSecondHalf() {
        mForceStop = false;
        mSecondAnimator.start();
    }

    private void stopSecondHalf() {
        mForceStop = true;
        mSecondAnimator.end();
    }

    void setOnStoneListener(OnStoneListener onStoneListener) {
        mOnStoneListener = onStoneListener;
    }

    interface OnStoneListener {
        void onFirstHalfEnd();

        void onSecondHalfEnd();
    }
}
