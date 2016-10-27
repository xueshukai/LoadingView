package xsk.cn.progressview;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by xsk on 2016/10/21.
 */

public class LoadingView2 extends View {

    private int radius = 10;//点半径
    private ValueAnimator animator;
    private int t = 1000;//一次振动的周期  影响振动速度
    private Paint paint;
    private int pointCount = 6;//点数
    private int pointDiffT = 150;//每个点开始运动时间的差值 影响最终动画效果
    private ArrayList<Point> points;
    private RectF rectF;
    private int swing = 20;//振幅
    private int space = 20;//点之间的距离


    public LoadingView2(Context context) {
        super(context);
    }

    public LoadingView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView2);
        t = typedArray.getInt(R.styleable.LoadingView2_point_circle_time, 1000);
        pointCount = typedArray.getInt(R.styleable.LoadingView2_point_count , 6);
        radius = typedArray.getDimensionPixelSize(R.styleable.LoadingView2_point_radius , 10);
        space = typedArray.getDimensionPixelSize(R.styleable.LoadingView2_point_space , 20);
        pointDiffT = typedArray.getInt(R.styleable.LoadingView2_point_diff_time , 150);
        swing = typedArray.getDimensionPixelSize(R.styleable.LoadingView2_point_swing , 20);
        typedArray.recycle();
        init();
    }

    public LoadingView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        points = new ArrayList<>();
        for (int i = 0; i < pointCount; i++) {
            points.add(new Point());
        }
        rectF = new RectF();
    }


    public void setPointDiffT(int pointDiffT) {
        this.pointDiffT = pointDiffT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getHeight() / 2;
        canvas.drawColor(Color.LTGRAY);
        paint.setColor(Color.DKGRAY);

        int left = 0;

        int pointTotal = radius * 2 * pointCount + space * (pointCount - 1);
        if (pointTotal > getWidth()){//修正点间距
            space = (getWidth() - radius * 2 * pointCount) / (pointCount-1);
        }else {//
            left = (getWidth() - pointTotal) /2;
        }

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            rectF.set(left + i * space + i * 2*radius, w - radius + point.y, left + i * space + i * 2*radius+2 * radius, w + radius + point.y);
            canvas.drawOval(rectF, paint);
        }

        startTransform();
    }


    private class Point {
        float y;
    }

    private void startTransform() {
        if (animator != null && animator.isRunning()) {
            return;
        }
        if (animator == null)
            animator = ValueAnimator.ofObject(new SineEvaluator(), 0f, 10f);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(t);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] floats = (float[]) animation.getAnimatedValue();
                for (int i = 0; i < points.size(); i++) {
                    Point point = points.get(i);
                    point.y = floats[i];
                }
                invalidate();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    private class SineEvaluator implements TypeEvaluator<Object> {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            float[] floats = new float[pointCount];
            for (int i = 0; i < pointCount; i++) {
                floats[i] = swing * (float) Math.sin(2 * Math.PI / t * (fraction * t - pointDiffT * i));
            }
            return floats;
        }
    }
}
