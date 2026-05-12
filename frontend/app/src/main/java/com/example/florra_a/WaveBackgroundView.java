package com.example.florra_a;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class WaveBackgroundView extends View {
    private Paint paint;
    private Path path;
    private float phase = 0;
    private ValueAnimator animator;

    public WaveBackgroundView(Context context) {
        super(context);
        init();
    }

    public WaveBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();

        animator = ValueAnimator.ofFloat(0, (float) (2 * Math.PI));
        animator.setDuration(15000); // Much slower for ultra-smoothness
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            phase = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // Wave 1: Slow, broad background layer
        drawWave(canvas, width, height, phase * 0.4f, 0.6f, 0.25f, 0.10f, "#F1F5F9");
        
        // Wave 2: Very slow, deep layer
        drawWave(canvas, width, height, -phase * 0.6f, 0.4f, 0.35f, 0.08f, "#E2E8F0");
        
        // Wave 3: Main flowing layer
        drawWave(canvas, width, height, phase * 0.8f, 0.8f, 0.20f, 0.12f, "#CBD5E1");
        
        // Wave 4: Subtle top ripple
        drawWave(canvas, width, height, -phase * 1.0f, 0.5f, 0.30f, 0.06f, "#94A3B8");
    }

    private void drawWave(Canvas canvas, int width, int height, float phase, float frequency, float amplitudeMultiplier, float alpha, String colorStr) {
        int color = Color.parseColor(colorStr);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        
        path.reset();
        float midY = height * 0.6f; 
        float baseAmplitude = height * amplitudeMultiplier;

        // Use a gradient for a more premium look
        paint.setShader(new LinearGradient(0, midY - baseAmplitude, 0, height, 
                Color.argb((int)(alpha * 255), r, g, b),
                Color.argb((int)(alpha * 30), r, g, b), 
                Shader.TileMode.CLAMP));
        
        path.moveTo(0, height);
        path.lineTo(0, midY);

        // Step of 2 for maximum point density
        for (int x = 0; x <= width; x += 2) { 
            // Broad sine wave with very subtle secondary interference for natural flow
            float angle = (float) (frequency * 2 * Math.PI * x / width + phase);
            float subAngle = (float) (frequency * 0.8 * Math.PI * x / width - phase * 0.5f);
            
            float y = midY + (float) (baseAmplitude * Math.sin(angle)) 
                           + (float) (baseAmplitude * 0.15f * Math.sin(subAngle)); // Reduced secondary effect
            
            path.lineTo(x, y);
        }

        path.lineTo(width, height);
        path.close();
        canvas.drawPath(path, paint);
        paint.setShader(null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
