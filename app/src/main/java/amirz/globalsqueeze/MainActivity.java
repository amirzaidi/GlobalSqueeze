package amirz.globalsqueeze;

import android.app.Activity;
import android.os.Bundle;

import static amirz.globalsqueeze.Utilities.SAMPLES;
import static amirz.globalsqueeze.Utilities.SQUEEZE;

public class MainActivity extends Activity {
    private MotionTracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SqueezeService.startForeground(this);

        final Visualizer visualizer = findViewById(R.id.visualizer);
        mTracker = new MotionTracker(this, new SqueezeAnalyzer(SAMPLES, SQUEEZE) {
            @Override
            protected void onUpdate(float[] abs) {
                visualizer.setData(abs);
                visualizer.invalidate();
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
