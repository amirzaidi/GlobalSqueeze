package amirz.globalsqueeze;

import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static amirz.globalsqueeze.Utilities.ATLEAST_OREO;
import static amirz.globalsqueeze.Utilities.SAMPLES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_AREA_RANGE;
import static amirz.globalsqueeze.Utilities.SQUEEZE_INDICES;
import static amirz.globalsqueeze.Utilities.SQUEEZE_THRESHOLD;

public class SqueezeService extends Service {
    private static final String TAG = "SqueezeService";

    private static final String FOREGROUND_CHANNEL = "Foreground";
    private static final int FOREGROUND_ID = 1;
    private static final int MIN_DELAY = 750;

    public static void startForeground(Context context) {
        Intent intent = new Intent(context, SqueezeService.class);
        if (ATLEAST_OREO) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private MotionTracker mTracker;
    private long mLastLaunch;

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            KeyguardManager km = context.getSystemService(KeyguardManager.class);
            boolean enabled = !km.inKeyguardRestrictedInputMode();

            Log.e(TAG, "Changing state to " + enabled);
            mTracker.setEnabled(enabled);
        }
    };

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
        mScreenStateReceiver.onReceive(this, null);
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        lockFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenStateReceiver, lockFilter);
    }

    private void onSqueeze() {
        Log.e(TAG, "Squeeze");

        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastLaunch > MIN_DELAY) {
            Intent opa = new Intent(Intent.ACTION_VOICE_COMMAND);
            opa.setPackage("com.google.android.googlequicksearchbox");
            opa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(opa);

            mLastLaunch = currentTime;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        unregisterReceiver(mScreenStateReceiver);

        mTracker.setEnabled(false);
        mTracker = null;

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }
}
