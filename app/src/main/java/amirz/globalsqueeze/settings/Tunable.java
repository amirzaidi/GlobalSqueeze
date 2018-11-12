package amirz.globalsqueeze.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import amirz.globalsqueeze.R;

public class Tunable {
    private static final String TAG = "Tunable";

    public static final StringRef INTENT_ACTION;
    public static final IntegerRef SQUEEZE_VIBRATE_DURATION;
    public static final IntegerRef SQUEEZE_VIBRATE_INTENSITY;

    private static final List<Ref> sTunables;

    static {
        sTunables = new ArrayList<>();

        INTENT_ACTION = new StringRef(R.string.pref_intent_action,
                R.string.pref_intent_action_default);

        SQUEEZE_VIBRATE_DURATION = new IntegerRef(R.string.pref_squeeze_vibrate_duration,
                R.integer.pref_squeeze_vibrate_duration_default);

        SQUEEZE_VIBRATE_INTENSITY = new IntegerRef(R.string.pref_squeeze_vibrate_intensity,
                R.integer.pref_squeeze_vibrate_intensity_default);
    }

    public static void applyAll(SharedPreferences prefs, Resources res) {
        for (Ref tunable : sTunables) {
            apply(prefs, res, tunable);
        }
    }

    public static boolean apply(SharedPreferences prefs, Resources res, String key) {
        for (Ref tunable : sTunables) {
            if (key.equals(res.getString(tunable.settingId))) {
                apply(prefs, res, tunable);
                return true;
            }
        }
        return false;
    }

    private static void apply(SharedPreferences prefs, Resources res, Ref tunable) {
        String key = res.getString(tunable.settingId);
        TypedValue defaultValue = new TypedValue();
        res.getValue(tunable.defaultId, defaultValue, true);

        Log.d(TAG, "Updating " + key);
        tunable.load(prefs, key, defaultValue);
    }

    public static class BooleanRef extends Ref<Boolean> {
        private BooleanRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
           value = prefs.getBoolean(key, defaultValue.data == 1);
        }
    }

    public static class FloatRef extends Ref<Float> {
        private FloatRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = Float.valueOf(prefs.getString(key, defaultString));
        }
    }

    public static class IntegerRef extends Ref<Integer> {
        private IntegerRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = Integer.valueOf(prefs.getString(key, defaultString));
        }
    }

    public static class StringRef extends Ref<String> {
        private StringRef(int settingId, int entries) {
            super(settingId, entries);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = prefs.getString(key, defaultString);
        }
    }

    private abstract static class Ref<T> {
        T value;
        final int settingId;
        final int defaultId;

        Ref(int settingId, int defaultId) {
            this.settingId = settingId;
            this.defaultId = defaultId;
            sTunables.add(this);
        }

        public T get() {
            return value;
        }

        abstract void load(SharedPreferences prefs, String key, TypedValue defaultValue);
    }
}
