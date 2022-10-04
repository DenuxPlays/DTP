package dev.denux.dtp.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Constant {
    private Constant() {}

    public static final Pattern RFC3339_REGEX = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})([Tt ](\\d{2}):(\\d{2}):?(\\d{2})?(\\.\\d+)?)?([Zz]|([+-])(\\d{2}):(\\d{2}))?");
    public static final Pattern RFC3339_TIME_REGEX = Pattern.compile("^(\\d{2}):(\\d{2}):?(\\d{2})?(\\.\\d*)?");

    public static final List<Character> STRING_INDICATORS = Arrays.asList('"', '\'');
}
