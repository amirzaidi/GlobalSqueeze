package amirz.library.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import amirz.globalsqueeze.R;
import amirz.library.Logger;

public class Tunable {
    private static final List<Ref> sTunables;

    static {
        sTunables = new ArrayList<>();
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

        Logger.log( "Updating " + key);
        tunable.load(prefs, key, defaultValue);
    }

    public static class BooleanRef extends Ref<Boolean> {
        public BooleanRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
           value = prefs.getBoolean(key, defaultValue.data == 1);
        }
    }

    public static class FloatRef extends Ref<Float> {
        public FloatRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = Float.valueOf(prefs.getString(key, defaultString));
        }
    }

    public static class IntegerRef extends Ref<Integer> {
        public IntegerRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = Integer.valueOf(prefs.getString(key, defaultString));
        }
    }

    public static class StringRef extends Ref<String> {
        public StringRef(int settingId, int entries) {
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

    private Tunable() {
    }
}
