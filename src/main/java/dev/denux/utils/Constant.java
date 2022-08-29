package dev.denux.utils;

import java.util.regex.Pattern;

public class Constant {
    private Constant() {}

    public static final Pattern STRING_REGEX = Pattern.compile("(?<=\")(?:\\\\.|[^\"\\\\])*(?=\")", Pattern.MULTILINE);
}
