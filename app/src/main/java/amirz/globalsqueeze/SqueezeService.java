package amirz.globalsqueeze;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import amirz.globalsqueeze.settings.Preferences;
import amirz.library.Logger;

import static amirz.globalsqueeze.Utilities.ATLEAST_OREO;
import static amirz.globalsqueeze.Utilities.SAMPLES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_AREA_RANGE;
import static amirz.globalsqueeze.Utilities.SQUEEZE_INDICES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_THRESHOLD;

public class SqueezeService extends Service
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String INTENT_KILL = "amirz.globalsqueeze.KILL";
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
        Logger.log( "onCreate");
        super.onCreate();

        if (ATLEAST_OREO) {
            NotificationChannel channel = new NotificationChannel(
                    FOREGROUND_CHANNEL, FOREGROUND_CHANNEL, NotificationManager.IMPORTANCE_NONE);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_squeeze);
        builder.setContentText("Detecting squeezes..");
        builder.addAction(getKillAction());
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

        Preferences.global().applyAll(prefs, getResources());
        setParams();

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (Preferences.global().apply(prefs, getResources(), key)) {
            setParams();
        }
    }

    private void setParams() {
        Logger.log("Parameters changed");
    }

    private NotificationCompat.Action getKillAction() {
        IntentFilter killFilter = new IntentFilter();
        killFilter.addAction(INTENT_KILL);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);
                SqueezeService.this.stopSelf();
            }
        }, killFilter);

        Intent intent = new Intent(INTENT_KILL);
        PendingIntent pend = PendingIntent.getBroadcast(this, 1, intent, 0);
        return new NotificationCompat.Action.Builder(0, "Exit", pend).build();
    }

    private void onSqueeze() {
        Logger.log("Squeeze");

        long currentTime = System.currentTimeMillis();
        String type = Preferences.global().intentAction.get();
        if (type != null && currentTime - mLastLaunch > MIN_DELAY) {
            mLastLaunch = currentTime;

            Vibrator.getInstance().vibrate(this,
                    Preferences.global().squeezeVibrateDuration.get(),
                    Preferences.global().squeezeVibrateIntensity.get());

            startIntent(type);
        }
    }

    private void startIntent(String type) {
        Logger.log("Starting " + type);

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
        Logger.log( "onDestroy");

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
