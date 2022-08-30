package dev.denux.utils;

import java.util.regex.Pattern;

public class Constant {
    private Constant() {}

    public static final Pattern STRING_REGEX = Pattern.compile("(?<=\")(?:\\\\.|[^\"\\\\])*(?=\")", Pattern.MULTILINE);
    public static final Pattern RFC3339_REGEX = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})([Tt ](\\d{2}):(\\d{2}):?(\\d{2})?(\\.\\d+)?)?([Zz]|([+-])(\\d{2}):(\\d{2}))?");
    public static final Pattern RFC3339_TIME_REGEX = Pattern.compile("^(\\d{2}):(\\d{2}):?(\\d{2})?(\\.\\d*)?");

}
