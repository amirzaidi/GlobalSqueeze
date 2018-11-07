package amirz.globalsqueeze;

import android.util.Range;

import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseOptimized;

public abstract class SqueezeAnalyzer implements MotionTracker.Cb {
    private static final double AREA_MIN = 0.5;
    private static final float SQUEEZE_THRESHOLD = 0.175f;

    private final NoiseOptimized noise;
    private final float[] abs;
    private final Range<Integer> mSqueezeIndices;

    public SqueezeAnalyzer(int samples, Range<Integer> squeezeIndices) {
        noise = Noise.real().optimized().init(samples, true);

        abs = new float[samples / 2 + 1];
        mSqueezeIndices = squeezeIndices;
    }

    @Override
    public void processSamples(float[] samples) {
        float[] fft = noise.fft(samples);

        double area = 0;
        for (int i = 0; i < fft.length / 2; i++) {
            float real = fft[i * 2];
            float imaginary = fft[i * 2 + 1];

            double value = Math.hypot(real, imaginary);
            abs[i] = (float) value;
            area += value;
        }

        // Scale by area
        area = Math.max(AREA_MIN, area);
        for (int i = 0; i < abs.length; i++) {
            abs[i] /= area;
        }

        onUpdate(abs);

        float squeezeFactor = 0;
        for (int i = mSqueezeIndices.getLower(); i <= mSqueezeIndices.getUpper(); i++) {
            squeezeFactor += abs[i];
        }

        if (squeezeFactor >= SQUEEZE_THRESHOLD) {
            onSqueeze();
        }
    }

    protected void onUpdate(float[] abs) {
    }

    protected void onSqueeze() {
    }
}
