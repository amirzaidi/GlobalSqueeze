package amirz.library.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import amirz.library.Logger;

/**
 * Class that provides synchronized preferences using the singleton design pattern.
 * Extensions should add a static getInstance() method.
 */
public abstract class GlobalPreferences {
    private final List<Ref> mPreferences = new ArrayList<>();

    /**
     * Loads all preferences from the SharedPreferences instance.
     * @param prefs Instance from which the data is pulled.
     * @param res Resources used to deserialize the default values as fallback values.
     */
    public void applyAll(SharedPreferences prefs, Resources res) {
        for (Ref tunable : mPreferences) {
            apply(prefs, res, tunable);
        }
    }

    /**
     * Loads one key's preference from the SharedPreferences instance.
     * @param prefs Instance from which the data is pulled.
     * @param res Resources used to deserialize the default value as a fallback value.
     */
    public boolean apply(SharedPreferences prefs, Resources res, String key) {
        for (Ref tunable : mPreferences) {
            if (key.equals(res.getString(tunable.settingId))) {
                apply(prefs, res, tunable);
                return true;
            }
        }
        return false;
    }

    private void apply(SharedPreferences prefs, Resources res, Ref tunable) {
        String key = res.getString(tunable.settingId);
        TypedValue defaultValue = new TypedValue();
        res.getValue(tunable.defaultId, defaultValue, true);

        Logger.log( "Updating " + key);
        tunable.load(prefs, key, defaultValue);
    }

    /**
     * Referenced setting that holds a boolean.
     */
    public class BooleanRef extends Ref<Boolean> {
        public BooleanRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
           value = prefs.getBoolean(key, defaultValue.data == 1);
        }
    }

    /**
     * Referenced setting that holds a floating point number.
     */
    public class FloatRef extends Ref<Float> {
        public FloatRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = Float.valueOf(prefs.getString(key, defaultString));
        }
    }

    /**
     * Referenced setting that holds an integer.
     */
    public class IntegerRef extends Ref<Integer> {
        public IntegerRef(int settingId, int defaultId) {
            super(settingId, defaultId);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = Integer.valueOf(prefs.getString(key, defaultString));
        }
    }

    /**
     * Referenced setting that holds a string.
     */
    public class StringRef extends Ref<String> {
        public StringRef(int settingId, int entries) {
            super(settingId, entries);
        }

        @Override
        void load(SharedPreferences prefs, String key, TypedValue defaultValue) {
            String defaultString = defaultValue.coerceToString().toString();
            value = prefs.getString(key, defaultString);
        }
    }

    private abstract class Ref<T> {
        T value;
        final int settingId;
        final int defaultId;

        Ref(int settingId, int defaultId) {
            this.settingId = settingId;
            this.defaultId = defaultId;
            mPreferences.add(this);
        }

        public T get() {
            return value;
        }

        abstract void load(SharedPreferences prefs, String key, TypedValue defaultValue);
    }

    /**
     * Empty constructor that prevents direct instantiation of this class.
     */
    protected GlobalPreferences() {
    }
}
