package amirz.globalsqueeze.settings;

import amirz.globalsqueeze.R;
import amirz.library.settings.Tunable;

public class Tunables {
    public static final Tunable.StringRef INTENT_ACTION;
    public static final Tunable.IntegerRef SQUEEZE_VIBRATE_DURATION;
    public static final Tunable.IntegerRef SQUEEZE_VIBRATE_INTENSITY;

    static {
        INTENT_ACTION = new Tunable.StringRef(R.string.pref_intent_action,
                R.string.pref_intent_action_default);

        SQUEEZE_VIBRATE_DURATION = new Tunable.IntegerRef(R.string.pref_squeeze_vibrate_duration,
                R.integer.pref_squeeze_vibrate_duration_default);

        SQUEEZE_VIBRATE_INTENSITY = new Tunable.IntegerRef(R.string.pref_squeeze_vibrate_intensity,
                R.integer.pref_squeeze_vibrate_intensity_default);
    }
}
