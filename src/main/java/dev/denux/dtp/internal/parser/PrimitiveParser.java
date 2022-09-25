package dev.denux.dtp.internal.parser;

@FunctionalInterface
public interface PrimitiveParser {

    Object parse(String s);
}