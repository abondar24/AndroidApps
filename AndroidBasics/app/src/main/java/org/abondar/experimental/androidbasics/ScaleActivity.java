package org.abondar.experimental.androidbasics;

import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class ScaleActivity extends AppCompatActivity {
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor=1f;
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);

        imageView = (ImageView)this.findViewById(R.id.image_view);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f,Math.min(scaleFactor,5.0f));

            matrix.setScale(scaleFactor,scaleFactor);

            imageView.setImageMatrix(matrix);
            imageView.invalidate();
            return true;
        }
    }
}
