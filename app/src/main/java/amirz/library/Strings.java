package amirz.library;

public class Strings {
    public static final String empty = "";

    public static String trimStart(String in, String trim) {
        if (in.startsWith(trim)) {
            in = in.substring(trim.length());
        }
        return in;
    }

    public static String before(String in, String delimiter) {
        int index = in.indexOf(delimiter);
        int endIndex = index < 0
                ? in.length() - 1
                : index;

        return in.substring(0, endIndex);
    }

    private Strings() {
    }
}
