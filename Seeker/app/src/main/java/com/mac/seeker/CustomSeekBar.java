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


public class CustomSeekBar extends View {

    private int max;
    private int min = 0;
    private int height = 0;
    private boolean showMaxMin;
    private boolean showTop;
    private boolean showThumb;
    private String unit;
    private boolean enable;

    private float current = min;
    private float step;
    private Paint activePaint;
    private Paint inactivePaint;
    private Paint textPaint;
    private float spacing;
    private float thumbRadius = 32;
    private OnLevelChangeListener levelChangeListener;

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomSeekBar,
                0, 0);
        try {
            max = a.getInt(R.styleable.CustomSeekBar_max, 100);
            min = a.getInt(R.styleable.CustomSeekBar_min, 0);
            current = min;
            step = a.getString(R.styleable.CustomSeekBar_step) == null ? 1 : Float.parseFloat(a.getString(R.styleable.CustomSeekBar_step));
            showMaxMin = a.getBoolean(R.styleable.CustomSeekBar_showMaxMin, true);
            showTop = a.getBoolean(R.styleable.CustomSeekBar_showTop, true);
            showThumb = a.getBoolean(R.styleable.CustomSeekBar_showThumb, true);
            unit = a.getString(R.styleable.CustomSeekBar_unit) == null ? "" : a.getString(R.styleable.CustomSeekBar_unit);
            enable = a.getBoolean(R.styleable.CustomSeekBar_enable, true);
            height = a.getInt(R.styleable.CustomSeekBar_heightInt, 80);
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
        inactivePaint.setColor(getResources().getColor(R.color.color_ba)); // 未激活部分为灰色
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
        int fixedHeightPx = dpToPx(height);

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
        int heightview = getHeight();
        textPaint.setTextSize(56f);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        spacing = (width - 2 * thumbRadius) / ((max - min) / step);

        float thumbX = 0;
        float thumbY = 0;
        for (int i = 0; i <= (max - min) / step; i++) {
            float left = thumbRadius + i * spacing;
            float right = left + spacing;
            if (i == (current - min) / step) {
                thumbX = left;
                thumbY = heightview / 2;

                if (showTop) {
                    //Left and right indents
                    float offset = textPaint.measureText(String.valueOf(current) + unit)/2;

                    if(offset>left){
                        canvas.drawText(String.valueOf(current) + unit, offset, 45, textPaint);
                    }else if(left+offset>width){
                        canvas.drawText(String.valueOf(current) + unit, width-offset, 45, textPaint);
                    }else {
                        canvas.drawText(String.valueOf(current) + unit, left, 45, textPaint);
                    }
                }

            }

        }


        activePaint.setStyle(Paint.Style.STROKE);
        activePaint.setStrokeWidth(3);
        RectF oval3 = new RectF(15, heightview / 2 - 15, width - 15, heightview / 2 + 15);
        canvas.drawRoundRect(oval3, 15, 15, activePaint);

        activePaint.setStyle(Paint.Style.FILL);
        activePaint.setStrokeWidth(5);
        if(current>0){
            RectF oval1 = new RectF(15, heightview / 2 - 15, thumbX, heightview / 2 + 15);
            canvas.drawRoundRect(oval1, 15, 15, activePaint);

        }

        if (showMaxMin) {
            float offset = textPaint.measureText(String.valueOf(min) + unit)/2;
            canvas.drawText(String.valueOf(min) + unit,   offset, dpToPx(height), textPaint);
            float offset2 = textPaint.measureText(String.valueOf(max) + unit)/2;
            canvas.drawText(String.valueOf(max) + unit, width-offset2, dpToPx(height), textPaint);
        }

        //thumb
        if (showThumb) {
            activePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(thumbX, thumbY, thumbRadius, activePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (enable) {
                    float x = event.getX();
                    float newLevel = (Math.round((x - thumbRadius) / spacing)) * step + min;
                    newLevel = Math.max(min, Math.min(newLevel, max));
                    if (newLevel != current) {
                        current = newLevel;
                        if (levelChangeListener != null) {
                            levelChangeListener.onLevelChanged(current);
                        }
                    }
                    invalidate();
                    return true;
                }

        }
        return super.onTouchEvent(event);
    }

    public void setOnLevelChangeListener(OnLevelChangeListener listener) {
        this.levelChangeListener = listener;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float level) {
        current = Math.max(1, Math.min(level, max));
        invalidate();
    }

    public interface OnLevelChangeListener {
        void onLevelChanged(float newLevel);
    }

}



