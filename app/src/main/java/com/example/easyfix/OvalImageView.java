package com.example.easyfix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class OvalImageView extends AppCompatImageView {
    private Path ovalPath;

    public OvalImageView(Context context) {
        super(context);
        init();
    }

    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OvalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Initialize the path object for the oval shape
        ovalPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Define the oval path based on the view dimensions
        ovalPath.reset();
        ovalPath.addOval(0, 0, w, h, Path.Direction.CW);
        ovalPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Clip the canvas to the defined oval path before drawing the image
        canvas.clipPath(ovalPath);
        super.onDraw(canvas);
    }
}
