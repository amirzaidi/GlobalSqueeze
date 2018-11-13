package amirz.globalsqueeze.settings;

import amirz.globalsqueeze.R;
import amirz.library.settings.GlobalPreferences;

public class Preferences extends GlobalPreferences {
    private static final Preferences sInstance = new Preferences();

    public static Preferences global() {
        return sInstance;
    }

    public final GlobalPreferences.StringRef intentAction =
            new GlobalPreferences.StringRef(R.string.pref_intent_action,
                    R.string.pref_intent_action_default);

    public final GlobalPreferences.IntegerRef squeezeVibrateDuration =
            new GlobalPreferences.IntegerRef(R.string.pref_squeeze_vibrate_duration,
                    R.integer.pref_squeeze_vibrate_duration_default);

    public final GlobalPreferences.IntegerRef squeezeVibrateIntensity =
            new GlobalPreferences.IntegerRef(R.string.pref_squeeze_vibrate_intensity,
                    R.integer.pref_squeeze_vibrate_intensity_default);
}
