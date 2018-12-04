package xsk.cn.progressview;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by xsk on 2016/10/18.
 */

public class LoadingView1 extends View {

    private Paint paint;

    private int radius = 20;
    private Transform transform;
    private ValueAnimator animator;

    private float thinWidthMaxPercent = 0.4f;
    private float thinWidthMinPercent = 0.3f;
    private boolean isRotate = true;
    private int[] colors;

    public LoadingView1(Context context) {
        this(context, null);
    }

    public LoadingView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        thinWidthMaxPercent = 0.4f;
        thinWidthMinPercent = 0.3f;
        colors = new int[4];
        colors[0] = Color.parseColor("#F8BBD0");
        colors[1] = Color.parseColor("#F48FB1");
        colors[2] = Color.parseColor("#F06292");
        colors[3] = Color.parseColor("#EC407A");
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    public void setColors(@ColorInt int... cs) {
        for (int i = 0; i < cs.length; i++) {
            if (i >= colors.length)
                break;
            colors[i] = cs[i];
        }
    }
    public void setColor(@ColorInt int color){
        for (int i = 0; i < colors.length; i++) {
            colors[i] = color;
        }
    }

    /**
     * 设置分开程度 越小分的越开 相对于最短边的百分比
     *
     * @param min 最小的分开
     * @param max 最大的分开
     */
    public void setThinWidthPercent(float min, float max) {
        this.thinWidthMinPercent = min;
        this.thinWidthMaxPercent = max;
        transform = null;
        initTransform();
    }

    public void setRotate(boolean rotate) {
        isRotate = rotate;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initTransform();
        canvas.save();

        if (getWidth() > getHeight()) {
            canvas.translate((getWidth() - getHeight()) / 2, 0);
        } else {
            canvas.translate(0, (getHeight() - getWidth()) / 2);
        }
        if (isRotate)
            canvas.rotate(transform.degrees, transform.dx, transform.dy);
        paint.setColor(colors[0]);
        canvas.drawOval(transform.leftTop, paint);
        paint.setColor(colors[1]);
        canvas.drawOval(transform.leftBottom, paint);
        paint.setColor(colors[2]);
        canvas.drawOval(transform.rightTop, paint);
        paint.setColor(colors[3]);
        canvas.drawOval(transform.rightBottom, paint);

        canvas.restore();
        startTransform();
    }


    private class Transform {
        RectF leftTop, leftBottom, rightTop, rightBottom;
        float degrees;
        float dx, dy;
        float fromDegrees;
        float thinWidth;
        float thinWidthMin, thinWidthMax;
        float width;

        public Transform() {
            leftTop = new RectF();
            leftBottom = new RectF();
            rightTop = new RectF();
            rightBottom = new RectF();
        }

        @Override
        public String toString() {
            return "Transform{" +
                    "degrees=" + degrees +
                    '}';
        }

        void transformRect() {
            leftTop.set(thinWidth - radius, thinWidth - radius, thinWidth + radius, thinWidth + radius);
            leftBottom.set(thinWidth - radius, width - thinWidth - radius, thinWidth + radius, width - thinWidth + radius);
            rightTop.set(width - thinWidth - radius, thinWidth - radius, width - thinWidth + radius, thinWidth + radius);
            rightBottom.set(width - thinWidth - radius, width - thinWidth - radius, width - thinWidth + radius, width - thinWidth + radius);
        }
    }

    private void initTransform() {
        if (transform != null) {
            return;
        }
        transform = new Transform();
        int width = getWidth() >= getHeight() ? getHeight() : getWidth();
        transform.thinWidthMin = thinWidthMaxPercent * width;
        transform.thinWidthMax = thinWidthMinPercent * width;
        transform.thinWidth = transform.thinWidthMin;
        transform.width = width;

        transform.transformRect();

        transform.degrees = 0;
        transform.dx = width / 2;
        transform.dy = width / 2;
        transform.fromDegrees = 0;
    }

    private void startTransform() {
        if (animator != null && animator.isRunning()) {
            return;
        }
        if (animator == null)
            animator = new ValueAnimator();
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        PropertyValuesHolder degreesValuesHolder = PropertyValuesHolder.ofFloat("degrees", 0, 90);
        PropertyValuesHolder thinWidthValuesHolder = PropertyValuesHolder.ofFloat("thinWidth", transform.thinWidthMin, transform.thinWidthMax, transform.thinWidthMin);
        animator.setValues(degreesValuesHolder, thinWidthValuesHolder);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                transform.degrees = (float) animation.getAnimatedValue("degrees");
                transform.thinWidth = (float) animation.getAnimatedValue("thinWidth");
                transform.transformRect();
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //synchronized (LoadingView1.this){
                transform.fromDegrees += 90;
                if (transform.fromDegrees > 270) {
                    transform.fromDegrees = 0;
                }
                PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("degrees", transform.fromDegrees, transform.fromDegrees + 90);
                animator.setValues(valuesHolder, PropertyValuesHolder.ofFloat("thinWidth", transform.thinWidthMin, transform.thinWidthMax, transform.thinWidthMin));
                //}
            }
        });
        animator.start();
    }

}
