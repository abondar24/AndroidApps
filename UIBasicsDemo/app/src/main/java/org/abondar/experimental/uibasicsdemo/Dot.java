package org.abondar.experimental.uibasicsdemo;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

/**
 * Created by abondar on 12/21/16.
 */
public class Dot extends View implements View.OnDragListener {

    private static final int DEFAULT_RADIUS = 20;
    private static final int DEFAULT_COLOR = Color.WHITE;
    private static final int SELECTED_COLOR = Color.MAGENTA;
    private Paint normalPaint;
    private Paint draggingPaint;
    private int color = DEFAULT_COLOR;
    private int radius = DEFAULT_RADIUS;
    private boolean inDrag;

    public Dot(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.Dot);

        final int numAttrs = attrsArray.getIndexCount();
        for (int i = 0; i < numAttrs; i++) {
            int attr = attrsArray.getIndex(i);
            switch (attr) {
                case R.styleable.Dot_radius:
                    radius = attrsArray.getDimensionPixelSize(attr, DEFAULT_COLOR);
                    break;
                case R.styleable.Dot_color:
                    color = attrsArray.getColor(attr, DEFAULT_COLOR);
                    break;
            }
        }

        attrsArray.recycle();

        normalPaint = new Paint();
        normalPaint.setColor(color);
        normalPaint.setAntiAlias(true);

        draggingPaint = new Paint();
        draggingPaint.setColor(SELECTED_COLOR);
        draggingPaint.setAntiAlias(true);

        setOnLongClickListener(lclistner);
        setOnDragListener(this);
    }

    private static View.OnLongClickListener lclistner = new View.OnLongClickListener() {

        private boolean dragInProgress;

        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("DragData", (String) view.getTag());

            dragInProgress = view.startDrag(data, new View.DragShadowBuilder(view), view, 0);
            Log.v((String) view.getTag(), "starting drag? " + dragInProgress);
            return true;
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = 2 * radius + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(size, size);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        if (dragEvent.getLocalState() != this) {
            return false;
        }
        boolean res = true;

        int action = dragEvent.getAction();
        float x = dragEvent.getX();
        float y = dragEvent.getY();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                inDrag = true;
                break;
            case DragEvent.ACTION_DROP:
                res = false;
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                inDrag = false;
                break;
            default:
                res = false;
                break;
        }
        return res;
    }

    public void draw(Canvas canvas){
        float cx = this.getWidth()/2 + getLeftPaddingOffset();
        float cy = this.getHeight()/2 + getTopPaddingOffset();
        Paint paint = normalPaint;
        if(inDrag){
            paint = draggingPaint;
        }
        canvas.drawCircle(cx,cy,radius,paint);
        invalidate();
    }
}
