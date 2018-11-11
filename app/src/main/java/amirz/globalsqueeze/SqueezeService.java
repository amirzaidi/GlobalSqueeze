package amirz.globalsqueeze;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import amirz.globalsqueeze.settings.Tunable;

import static amirz.globalsqueeze.Utilities.ATLEAST_OREO;
import static amirz.globalsqueeze.Utilities.SAMPLES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_AREA_RANGE;
import static amirz.globalsqueeze.Utilities.SQUEEZE_INDICES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_THRESHOLD;

public class SqueezeService extends Service
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SqueezeService";

    private static final String FOREGROUND_CHANNEL = "Foreground";
    private static final int FOREGROUND_ID = 1;
    private static final int MIN_DELAY = 1000;

    public static void startForeground(Context context) {
        Intent intent = new Intent(context, SqueezeService.class);
        if (ATLEAST_OREO) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private MotionTracker mTracker;
    private ScreenLock mLock;
    private long mLastLaunch;

    public SqueezeService() {
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();

        if (ATLEAST_OREO) {
            NotificationChannel channel = new NotificationChannel(
                    FOREGROUND_CHANNEL, FOREGROUND_CHANNEL, NotificationManager.IMPORTANCE_NONE);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_squeeze);
        startForeground(FOREGROUND_ID, builder.build());

        mTracker = new MotionTracker(this, new SqueezeAnalyzer(SAMPLES, SQUEEZE_INDICES,
                SQUEEZE_AREA_RANGE, SQUEEZE_THRESHOLD) {
            @Override
            protected void onSqueeze() {
                SqueezeService.this.onSqueeze();
            }
        }, SAMPLES);

        // Handle screen lock
        mLock = new ScreenLock(this, locked -> mTracker.setEnabled(!locked));

        SharedPreferences prefs = Utilities.prefs(this);

        Tunable.applyAll(prefs, getResources());
        setParams();

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (Tunable.apply(prefs, getResources(), key)) {
            setParams();
        }
    }

    private void setParams() {

    }

    private void onSqueeze() {
        Log.e(TAG, "Squeeze");

        long currentTime = System.currentTimeMillis();
        String type = Tunable.INTENT_ACTION.get();
        if (type != null && currentTime - mLastLaunch > MIN_DELAY) {
            mLastLaunch = currentTime;

            Vibrator.getInstance().vibrate(this,
                    Tunable.SQUEEZE_VIBRATE_DURATION.get(),
                    Tunable.SQUEEZE_VIBRATE_INTENSITY.get());

            startIntent(type);
        }
    }

    private void startIntent(String type) {
        Log.e(TAG, "Starting " + type);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        switch (type) {
            case "google":
                intent.setAction(Intent.ACTION_VOICE_COMMAND);
                break;
            case "assist":
                intent.setAction("amirz.assistmapper.MAIN");
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            default:
                return;
        }

        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        SharedPreferences prefs = Utilities.prefs(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        mLock.close();

        mTracker.setEnabled(false);
        mTracker = null;

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
