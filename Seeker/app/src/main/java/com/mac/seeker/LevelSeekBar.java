package com.mac.seeker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class LevelSeekBar extends View {

    private int maxLevel;
    private int currentLevel = 3;
    private Paint activePaint;
    private Paint inactivePaint;
    private Paint textPaint;
    private float spacing;
    private float thumbRadius = 36;
    private OnLevelChangeListener levelChangeListener;


    public LevelSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpeedLevelSeekBar,
                0, 0);
        try {
            maxLevel = a.getInt(R.styleable.SpeedLevelSeekBar_speedLevel, 5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        init();
    }


    private void init() {

        activePaint = new Paint();
        activePaint.setColor(Color.BLACK);
        activePaint.setStyle(Paint.Style.FILL);
        inactivePaint = new Paint();
        inactivePaint.setColor(getResources().getColor(R.color.color_ba));
        inactivePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);

    }


    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int fixedHeightPx = dpToPx(80);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = fixedHeightPx;
        int heightMode = MeasureSpec.EXACTLY;

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(height, heightMode));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        spacing = (width - 2 * thumbRadius) / (maxLevel - 1);

        float thumbX = 0;
        float thumbY = 0;
        for (int i = 0; i < maxLevel; i++) {
            float left = thumbRadius + i * spacing;
            float right = left + spacing;
            if (i == (currentLevel - 1)) {
                thumbX = left;
                thumbY = height / 2;
            }
        }


        activePaint.setStyle(Paint.Style.STROKE);
        activePaint.setStrokeWidth(3);
        RectF oval3 = new RectF(15, height / 2 - 15, width - 15, height / 2 + 15);
        canvas.drawRoundRect(oval3, 15, 15, activePaint);

        activePaint.setStyle(Paint.Style.FILL);
        activePaint.setStrokeWidth(5);
        RectF oval1 = new RectF(15, height / 2 - 15, thumbX, height / 2 + 15);
        canvas.drawRoundRect(oval1, 15, 15, activePaint);

        for (int i = 0; i < maxLevel; i++) {
            float x = thumbRadius + i * spacing;
            textPaint.setColor(Color.BLACK);
            if (i == currentLevel - 1) {
                textPaint.setTextSize(56f);
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            } else {
                textPaint.setTextSize(36f);
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            }

            // number
            canvas.drawText(String.valueOf(i + 1), x, 45, textPaint);

            inactivePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, height / 2, 10, inactivePaint);
        }


        //thumb
        activePaint.setStyle(Paint.Style.FILL);//充满
        canvas.drawCircle(thumbX, thumbY, thumbRadius, activePaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                int newLevel = (int) (Math.round((x - thumbRadius) / spacing) + 1);
                newLevel = Math.max(1, Math.min(newLevel, maxLevel));

                if (newLevel != currentLevel) {
                    currentLevel = newLevel;
                    if (levelChangeListener != null) {
                        levelChangeListener.onLevelChanged(currentLevel);
                    }
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnLevelChangeListener(OnLevelChangeListener listener) {
        this.levelChangeListener = listener;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        currentLevel = Math.max(1, Math.min(level, maxLevel));
        invalidate();
    }

    public interface OnLevelChangeListener {
        void onLevelChanged(int newLevel);
    }

}



