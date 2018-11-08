package amirz.globalsqueeze;

import android.app.Activity;
import android.os.Bundle;

import static amirz.globalsqueeze.Utilities.SAMPLES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_AREA_RANGE;
import static amirz.globalsqueeze.Utilities.SQUEEZE_INDICES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_THRESHOLD;

public class MainActivity extends Activity {
    private MotionTracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SqueezeService.startForeground(this);

        final Visualizer[] visualizers = {
                findViewById(R.id.visualizerX),
                findViewById(R.id.visualizerY),
                findViewById(R.id.visualizerZ)
        };

        mTracker = new MotionTracker(this, new SqueezeAnalyzer(SAMPLES, SQUEEZE_INDICES,
                SQUEEZE_AREA_RANGE, SQUEEZE_THRESHOLD) {
            @Override
            protected void onUpdate(float[][] samples) {
                for (int i = 0; i < samples.length; i++) {
                    visualizers[i].setData(samples[i]);
                    visualizers[i].invalidate();
                }
            }
        }, SAMPLES);

        mTracker.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        mTracker.setEnabled(false);
        mTracker = null;

        super.onDestroy();
    }
}
