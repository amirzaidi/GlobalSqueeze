package amirz.globalsqueeze;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class MotionTracker implements SensorEventListener {
    private static final String TAG = "MotionTracker";

    private static final int SPEED = SensorManager.SENSOR_DELAY_FASTEST;

    private final SensorManager mSensorManager;
    private final Sensor mLinearSensor;

    private Cb mCallback;
    private final float[] mSamples;
    private int mSampleCount = 0;

    private boolean mEnabled;

    interface Cb {
        void processSamples(float[] samples);
    }

    public MotionTracker(Context context, Cb callback, int samples) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLinearSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mCallback = callback;
        mSamples = new float[samples];
    }

    public void setEnabled(boolean enabled) {
        if (enabled && !mEnabled) {
            mSensorManager.registerListener(this, mLinearSensor, SPEED);
            mEnabled = true;
        } else if (!enabled && mEnabled) {
            mSensorManager.unregisterListener(this);
            mEnabled = false;
        }
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
