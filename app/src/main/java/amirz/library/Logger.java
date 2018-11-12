package amirz.library;

import android.util.Log;

import amirz.globalsqueeze.BuildConfig;

public class Logger {
    private static final int PRIORITY = BuildConfig.DEBUG
            ? Log.ERROR
            : Log.VERBOSE;

    public static void log(String firstMsg) {
        log(firstMsg, new String[0]);
    }

    public static void log(String firstMsg, String... additionalMsgs) {
        String tag = getTag();
        if (additionalMsgs.length == 0) {
            Log.println(PRIORITY, tag, firstMsg);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(firstMsg);
            for (String str : additionalMsgs) {
                builder.append(", ");
                builder.append(str);
            }
            Log.println(PRIORITY, tag, builder.toString());
        }
    }

    public static void log(String firstMsg, Exception... additionalExceptions) {
        String[] additionalMsgs = new String[additionalExceptions.length];
        for (int i = 0; i < additionalExceptions.length; i++) {
            additionalMsgs[i] = additionalExceptions[i].toString();
        }
        log(firstMsg, additionalMsgs);
    }

    private static String getTag() {
        String[] splitLn = Log.getStackTraceString(new Throwable()).split(System.lineSeparator());
        for (int i = 1; i < splitLn.length; i++) {
            String line = splitLn[i];
            line = Strings.trimStart(line.trim(), "at ");
            line = Strings.before(line, "(");
            if (!line.isEmpty() && !line.startsWith(Logger.class.getName())) {
                String[] splitPkg = line.split("\\.");
                return splitPkg[splitPkg.length - 2];
            }
        }
        return Logger.class.getSimpleName();
    }

    private Logger() {
    }
}
