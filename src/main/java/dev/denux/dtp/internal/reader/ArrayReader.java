package dev.denux.dtp.internal.reader;

import dev.denux.dtp.utils.Constant;

public class ArrayReader {

    public final StringBuilder builder = new StringBuilder();

    public ArrayReader readArray(String value) {
        char[] chars = value.toCharArray();
        boolean needEscaping = false;
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            char previousChar = i == 0 ? ' ' : chars[i - 1];
            if (!needEscaping) {
                if (c == '#') {
                    continue;
                }
            }
            if (Constant.STRING_INDICATORS.contains(c)) {
                if (previousChar == '\\') {
                    val.append(c);
                    continue;
                }
                if (!needEscaping) {
                    needEscaping = true;
                }
                val.append(c);
                continue;
            }
            if (c == ',' && !needEscaping && previousChar != '\"') {
                val.append(c);
                continue;
            }
            if (c != ' ') {
                val.append(c);
            }
        }
        builder.append(val);
        return this;
    }

    public String getString() {
        return builder.toString();
    }

    public static boolean endOfMultilineArray(String line) {
        char[] chars = line.toCharArray();
        boolean string = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            char previousChar = i == 0 ? ' ' : chars[i - 1];
            if (Constant.STRING_INDICATORS.contains(c)) {
                if (!string) string = true;
                if (previousChar == '\\') continue;
                string = false;
                continue;
            }
            if (c == ']' && !string) {
                return true;
            }
        }
        return false;
    }
}