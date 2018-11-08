package amirz.globalsqueeze;

import android.util.Log;
import android.util.Range;

import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseOptimized;

public abstract class SqueezeAnalyzer implements MotionTracker.Cb {
    private final NoiseOptimized mNoise;
    private final float[][] abs;
    private final double[] area;
    private final Range<Integer> mSqueezeIndices;
    private final Range<Double> mSqueezeArea;
    private final float mSqueezeThreshold;

    public SqueezeAnalyzer(int samples, Range<Integer> squeezeIndices, Range<Double> squeezeArea,
                           float squeezeThreshold) {
        mNoise = Noise.real().optimized().init(samples, true);

        abs = new float[3][samples / 2 + 1];
        area = new double[3];
        mSqueezeIndices = squeezeIndices;
        mSqueezeArea = squeezeArea;
        mSqueezeThreshold = squeezeThreshold;
    }

    @Override
    public void processSamples(float[][] samples) {
        for (int axis = 0; axis < samples.length; axis++) {
            float[] fft = mNoise.fft(samples[axis]);
            float[] abs = this.abs[axis];

            area[axis] = 0;
            for (int i = 0; i < fft.length / 2; i++) {
                float real = fft[i * 2];
                float imaginary = fft[i * 2 + 1];

                double value = Math.hypot(real, imaginary);
                abs[i] = (float) value;
                area[axis] += value;
            }

            // Scale by area
            for (int i = 0; i < abs.length; i++) {
                abs[i] /= area[axis];
            }
        }

        if (analyzeForSqueeze()) {
            onSqueeze();
        }

        onUpdate(abs);
    }

    private boolean analyzeForSqueeze() {
        float squeezeFactor = 0f;

        for (int axis = 0; axis < 3; axis++) {
            if (!mSqueezeArea.contains(area[axis])) {
                return false;
            }

            for (int i = mSqueezeIndices.getLower(); i <= mSqueezeIndices.getUpper(); i++) {
                squeezeFactor += abs[axis][i];
            }
        }

        Log.e("SqueezeAnalyzer", "Squeeze " + squeezeFactor);
        return squeezeFactor >= mSqueezeThreshold;
    }

    protected void onUpdate(float[][] abs) {
    }

    protected void onSqueeze() {
    }
}
