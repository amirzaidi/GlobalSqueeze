package amirz.globalsqueeze;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenLock extends BroadcastReceiver implements AutoCloseable {
    private final Context mContext;
    private final ScreenLockHandler mHandler;

    public ScreenLock(Context context, ScreenLockHandler handler) {
        mContext = context;
        mHandler = handler;

        onReceive(context, null);
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        lockFilter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(this, lockFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyguardManager km = context.getSystemService(KeyguardManager.class);
        mHandler.onLockChange(km.inKeyguardRestrictedInputMode());
    }

    @Override
    public void close() {
        mContext.unregisterReceiver(this);
    }

    interface ScreenLockHandler {
        void onLockChange(boolean locked);
    }
}
