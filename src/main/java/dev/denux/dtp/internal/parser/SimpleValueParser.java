package dev.denux.dtp.internal.parser;

@FunctionalInterface
public interface SimpleValueParser {

    Object parse(char[] chars, int[] idx);
}
