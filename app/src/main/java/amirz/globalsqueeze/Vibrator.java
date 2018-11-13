package amirz.globalsqueeze;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.VibrationEffect;

import amirz.library.MathUtils;

public class Vibrator {
    private static final Vibrator sInstance = Utilities.ATLEAST_OREO
            ? new VibratorV26()
            : new Vibrator();

    public static Vibrator getInstance() {
        return sInstance;
    }

    public void vibrate(Context context, int duration, int intensity) {
        getVibrator(context).vibrate(duration);
    }

    android.os.Vibrator getVibrator(Context context) {
        return (android.os.Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @TargetApi(26)
    private static class VibratorV26 extends Vibrator {
        @Override
        public void vibrate(Context context, int duration, int intensity) {
            intensity = MathUtils.clamp(1, intensity, 255);
            getVibrator(context).vibrate(VibrationEffect.createOneShot(duration, intensity));
        }
    }
}
