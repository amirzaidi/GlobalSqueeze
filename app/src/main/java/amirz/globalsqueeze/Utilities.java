package amirz.globalsqueeze;

import android.os.Build;
import android.util.Range;

public class Utilities {
    public static final boolean ATLEAST_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public static final int SAMPLES = 64;
    public static final Range<Integer> SQUEEZE_INDICES = new Range<>(3, 4);
    public static final Range<Double> SQUEEZE_AREA_RANGE = new Range<>(15d, 200d);
    public static final float SQUEEZE_THRESHOLD = 1f;
    public static final int VIBRATE_DURATION = 150;

    private Utilities() {
    }
}
