package amirz.globalsqueeze;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class MotionTracker implements AutoCloseable, SensorEventListener {
    private static final String TAG = "MotionTracker";

    private static final int SPEED = SensorManager.SENSOR_DELAY_FASTEST;

    private final SensorManager mSensorManager;

    private Cb mCallback;
    private final float[] mSamples;
    private int mSampleCount = 0;

    interface Cb {
        void processSamples(float[] samples);
    }

    public MotionTracker(Context context, Cb callback, int minSamples) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor linearSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, linearSensor, SPEED);

        mCallback = callback;
        mSamples = new float[minSamples];
    }

    @Override
    public void close() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mSamples[mSampleCount++] = event.values[0];

        if (mSampleCount == mSamples.length) {
            mCallback.processSamples(mSamples);
            mSampleCount = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.e(TAG, "onAccuracyChanged");
    }
}
