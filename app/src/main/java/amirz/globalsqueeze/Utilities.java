package amirz.globalsqueeze;

import android.os.Build;
import android.util.Range;

public class Utilities {
    public static final boolean ATLEAST_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public static final int SAMPLES = 128;
    public static final Range<Integer> SQUEEZE = new Range<>(7, 8);

    private Utilities() {
    }
}
