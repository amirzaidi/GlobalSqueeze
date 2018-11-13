package amirz.library;

import android.util.Log;
import android.util.SparseArray;

import amirz.globalsqueeze.BuildConfig;

public class Logger {
    private static final int PRIORITY = BuildConfig.DEBUG
            ? Log.ERROR
            : Log.VERBOSE;

    private static final SparseArray<String> sTraceCache = new SparseArray<>();

    public static void log(String msg, Throwable... tr) {
        String[] trMsg = new String[tr.length];
        for (int i = 0; i < tr.length; i++) {
            trMsg[i] = tr[i].toString();
        }
        log(msg, trMsg);
    }

    private static synchronized void log(String msg, String... tr) {
        String tag = getTag();
        if (tr.length == 0) {
            Log.println(PRIORITY, tag, msg);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(msg);
            for (String str : tr) {
                builder.append(", ");
                builder.append(str);
            }
            Log.println(PRIORITY, tag, builder.toString());
        }
    }

    private static String getTag() {
        String trace = Log.getStackTraceString(new Throwable());

        int hashCode = trace.hashCode();
        String className = sTraceCache.get(hashCode);
        if (className == null) {
            className = Logger.class.getSimpleName();

            String[] splitLn = trace.split(System.lineSeparator());
            for (int i = 1; i < splitLn.length; i++) {
                String line = splitLn[i];
                line = Strings.trimStart(line.trim(), "at ");
                line = Strings.before(line, "(");
                if (!line.isEmpty() && !line.startsWith(Logger.class.getName())) {
                    String[] splitPkg = line.split("\\.");
                    className = splitPkg[splitPkg.length - 2];
                    break;
                }
            }

            sTraceCache.put(hashCode, className);
        }

        return className;
    }

    private Logger() {
    }
}
