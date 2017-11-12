package io.julian.rollingstone;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is a loading view like rolling stones.
 */
public class RollingStoneView extends View {
    private static final int MATRIX_ROW = 3;
    private static final int MATRIX_COLUMN = 4;

    private int mStoneWidth;
    private int mStoneHeight;
    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    private Stone[] mStones;
    private int mFirstHalfDelay;
    private int mFirstHalfDuration;
    private int mSecondHalfDelay;
    private int mSecondHalfDuration;

    public RollingStoneView(Context context) {
        this(context, null);
    }

    public RollingStoneView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollingStoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RollingStoneView, defStyle, R.style.RollingStoneView);

        mStoneWidth = a.getDimensionPixelOffset(
                R.styleable.RollingStoneView_stoneWidth, 0);
        mStoneHeight = a.getDimensionPixelOffset(
                R.styleable.RollingStoneView_stoneHeight, 0);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mHorizontalSpacing = a.getDimensionPixelSize(
                R.styleable.RollingStoneView_stoneHorizontalSpacing,
                0);
        mVerticalSpacing = a.getDimensionPixelOffset(
                R.styleable.RollingStoneView_stoneVerticalSpacing,
                0);

        mFirstHalfDelay = a.getInteger(R.styleable.RollingStoneView_stoneFirstHalfDelay, 0);
        mFirstHalfDuration = a.getInteger(R.styleable.RollingStoneView_stoneFirstHalfDuration, 0);
        mSecondHalfDelay = a.getInteger(R.styleable.RollingStoneView_stoneSecondHalfDelay, 0);
        mSecondHalfDuration = a.getInteger(R.styleable.RollingStoneView_stoneSecondHalfDuration, 0);
        mStones = new Stone[MATRIX_ROW * MATRIX_COLUMN];
        for (int row = 0; row < MATRIX_ROW; row++) {
            for (int column = 0; column < MATRIX_COLUMN; column++) {
                Drawable drawable = a.getDrawable(R.styleable.RollingStoneView_stoneDrawable);
                if (drawable == null) {
                    throw new NullPointerException("Not found attr stoneDrawable");
                }
                mStones[row * MATRIX_COLUMN + column] = new Stone(this, drawable);
            }
        }

        a.recycle();

        initStones();
    }

    private void initStones() {
        mStones[8].setRollStyle(0, mFirstHalfDuration, 12 * mSecondHalfDelay, mSecondHalfDuration);
        mStones[4].setRollStyle(mFirstHalfDelay, mFirstHalfDuration, 10 * mSecondHalfDelay, mSecondHalfDuration);
        mStones[0].setRollStyle(2 * mFirstHalfDelay, mFirstHalfDuration, 11 * mSecondHalfDelay, mSecondHalfDuration);

        mStones[9].setRollStyle(3 * mFirstHalfDelay, mFirstHalfDuration, 9 * mSecondHalfDelay, mSecondHalfDuration);
        mStones[5].setRollStyle(4 * mFirstHalfDelay, mFirstHalfDuration, 7 * mSecondHalfDelay, mSecondHalfDuration);
        mStones[1].setRollStyle(5 * mFirstHalfDelay, mFirstHalfDuration, 8 * mSecondHalfDelay, mSecondHalfDuration);

        mStones[10].setRollStyle(6 * mFirstHalfDelay, mFirstHalfDuration, 6 * mSecondHalfDelay, mSecondHalfDuration);
        mStones[6].setRollStyle(7 * mFirstHalfDelay, mFirstHalfDuration, 4 * mSecondHalfDelay, mSecondHalfDuration);
        mStones[2].setRollStyle(8 * mFirstHalfDelay, mFirstHalfDuration, 5 * mSecondHalfDelay, mSecondHalfDuration);

        mStones[11].setRollStyle(9 * mFirstHalfDelay, mFirstHalfDuration, 3 * mSecondHalfDelay, mSecondHalfDuration);
        mStones[7].setRollStyle(10 * mFirstHalfDelay, mFirstHalfDuration, mSecondHalfDelay, mSecondHalfDuration);
        mStones[3].setRollStyle(11 * mFirstHalfDelay, mFirstHalfDuration, 2 * mSecondHalfDelay, mSecondHalfDuration);

        mStones[3].setOnStoneListener(new Stone.OnStoneListener() {
            @Override
            public void onFirstHalfEnd() {
                for (Stone stone : mStones) {
                    stone.startSecondHalf();
                }
            }

            @Override
            public void onSecondHalfEnd() {

            }
        });
        mStones[8].setOnStoneListener(new Stone.OnStoneListener() {
            @Override
            public void onFirstHalfEnd() {

            }

            @Override
            public void onSecondHalfEnd() {
                for (Stone stone : mStones) {
                    stone.startFirstHalf();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize;
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = (MATRIX_COLUMN + 1) * mStoneWidth + (MATRIX_COLUMN - 1) * mHorizontalSpacing
                    + paddingLeft + paddingRight;
        } else {
            widthSize = MeasureSpec.getSize(widthMeasureSpec);
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize;
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = (MATRIX_ROW + 1) * mStoneHeight + (MATRIX_ROW - 1) * mVerticalSpacing
                    + paddingTop + paddingBottom;
        } else {
            heightSize = MeasureSpec.getSize(heightMeasureSpec);
        }

        measureStones();

        setMeasuredDimension(widthSize, heightSize);
    }

    private void measureStones() {
        for (int row = 0; row < MATRIX_ROW; row++) {
            for (int column = 0; column < MATRIX_COLUMN; column++) {
                int stoneLeft = (column + 1) * mStoneWidth + column * mHorizontalSpacing;
                int stoneTop = (row + 1) * mStoneHeight + row * mVerticalSpacing;
                int stoneRight = stoneLeft + mStoneWidth;
                int stoneBottom = stoneTop + mStoneHeight;

                mStones[row * MATRIX_COLUMN + column].setBounds(stoneLeft, stoneTop, stoneRight,
                        stoneBottom);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Stone stone : mStones) {
            stone.draw(canvas);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    private void startAnimation() {
        for (Stone stone : mStones) {
            stone.startAnimation();
        }
    }

    private void stopAnimation() {
        for (Stone stone : mStones) {
            stone.stopAnimation();
        }
    }
}
