package amirz.globalsqueeze;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import amirz.library.BinaryState;
import amirz.library.Logger;

public class MotionTracker extends BinaryState implements SensorEventListener {
    private static final int SPEED = SensorManager.SENSOR_DELAY_FASTEST;

    private final SensorManager mSensorManager;
    private final Sensor mLinearSensor;

    private Cb mCallback;
    private final float[][] mSamples;
    private int mSampleCount = 0;

    interface Cb {
        void processSamples(float[][] samples);
    }

    public MotionTracker(Context context, Cb callback, int samples) {
        super(false);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLinearSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mCallback = callback;
        mSamples = new float[3][samples];
    }

    @Override
    protected void onEnable() {
        mSensorManager.registerListener(this, mLinearSensor, SPEED);
    }

    @Override
    protected void onDisable() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (int i = 0; i < 3; i++) {
            mSamples[i][mSampleCount] = event.values[i];
        }

        if (++mSampleCount == mSamples[0].length) {
            mCallback.processSamples(mSamples);
            mSampleCount = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Logger.log("onAccuracyChanged");
    }
}
