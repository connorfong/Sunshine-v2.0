package com.connorfong.sunshine3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tacosrkewl on 4/28/2016.
 */
public class Compass extends View {
    private final Rect r = new Rect();
    private float direction;

    public Compass(Context context) {
        super(context);
    }

    public Compass(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public Compass(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int r = (w > h) ? h/2 : w/2;
        r -= 8;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setColor(Color.GRAY);

        canvas.drawCircle(w/2, h/2, r, paint);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_size));
        paint.setColor(Color.RED);
        drawCenter(canvas, paint, "N");

        paint.setColor(getResources().getColor(R.color.sunshine_blue));
        canvas.drawLine(
                w/2,
                h/2,
                (float)(w/2 + r * Math.sin(-direction)),
                (float)(h/2 - r * Math.cos(-direction)),
                paint
        );
    }

    public void update(float dir) {
        direction = dir;

        // Call invalidate to force redraw of page
        invalidate();
    }

    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cWidth = r.width();
        int cHeight = r.height();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = r.height() + 15 - r.bottom;
        canvas.drawText(text, x, y, paint);
    }
}
