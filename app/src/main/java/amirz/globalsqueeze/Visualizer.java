package amirz.globalsqueeze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Visualizer extends View {
    private Paint paint = new Paint();
    private Rect rect = new Rect();
    private float[] data;
    private float[] points;

    public Visualizer(Context context) {
        super(context);
    }

    public Visualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Visualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(float[] data) {
        this.data = data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (data != null) {
            if (points == null || points.length < data.length * 4) {
                points = new float[data.length * 4];
            }

            paint.setStrokeWidth(getHeight() * 0.005f);
            rect.set(0, 0, getWidth(), getHeight());

            float scale = 2.5f;
            for (int i = 0; i < data.length - 1; i++) {
                points[i * 4] = rect.width() * i / (data.length - 1);
                points[i * 4 + 1] = rect.height() / 2 + (data[i] * scale) * (rect.height() / 3);
                points[i * 4 + 2] = rect.width() * (i + 1) / (data.length - 1);
                points[i * 4 + 3] = rect.height() / 2 + (data[i + 1] * scale) * (rect.height() / 3);
            }
            canvas.drawLines(points, paint);
        }

        super.onDraw(canvas);
    }
}
