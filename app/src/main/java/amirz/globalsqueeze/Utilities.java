package amirz.globalsqueeze;

import android.os.Build;
import android.util.Range;

public class Utilities {
    public static final boolean ATLEAST_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public static final int SAMPLES = 64;
    public static final Range<Integer> SQUEEZE_INDICES = new Range<>(3, 4);
    public static final Range<Double>[] SQUEEZE_AREA_RANGE;
    public static final float SQUEEZE_THRESHOLD = 0.6f;
    public static final int VIBRATE_DURATION = 150;
    public static final int VIBRATE_INTESITY = 25;

    static {
        SQUEEZE_AREA_RANGE = new Range[3];
        SQUEEZE_AREA_RANGE[0] = new Range<>(100d, 500d);
        SQUEEZE_AREA_RANGE[1] = new Range<>(30d, 400d);
        SQUEEZE_AREA_RANGE[2] = new Range<>(40d, 500d);
    }

    public static float sqr(float in) {
        return in * in;
    }

    private Utilities() {
    }
}
