package amirz.library;

public abstract class BinaryState {
    private boolean mEnabled;

    public BinaryState(boolean enable) {
        mEnabled = enable;
    }

    public final void setEnabled(boolean enable) {
        if (enable && !mEnabled) {
            mEnabled = true;
            onEnable();
        } else if (!enable && mEnabled) {
            mEnabled = false;
            onDisable();
        }
    }

    protected abstract void onEnable();

    protected abstract void onDisable();
}
