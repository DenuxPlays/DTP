package dev.denux.dtp.internal.parser;

import dev.denux.dtp.utils.PrimitiveUtil;

import java.util.HashMap;
import java.util.Map;

public class SimpleValueParsers {

    private static final Map<Class<?>,SimpleValueParser> _parsersByType = new HashMap<>();

    static {
        addPrimitiveParser(byte.class, Byte::parseByte);
        addPrimitiveParser(short.class, Short::parseShort);
        addPrimitiveParser(int.class, Integer::parseInt);
        addPrimitiveParser(long.class, Long::parseLong);
        addPrimitiveParser(double.class, Double::parseDouble);
        addPrimitiveParser(float.class, Float::parseFloat);
        addPrimitiveParser(boolean.class, Boolean::parseBoolean);
        addPrimitiveParser(char.class, s -> {
            if (s.length() != 1) {
                throw new NumberFormatException(s);
            }
            return s.charAt(0);
        });
        _parsersByType.put(String.class, makeStringParser());
    }

    private static SimpleValueParser makeStringParser() {
        return (chars, idx) -> {
            char quoteType = chars[idx[0]++];
            switch (quoteType) {
                case '\'':
                case '"':
                    break;
                default:
                    throw new IllegalArgumentException("Invalid String start character: " + quoteType);
            }
            StringBuilder sb = new StringBuilder();
            boolean quoting = false;
            while (idx[0] < chars.length) {
                char ch = chars[idx[0]++];
                if (!quoting) {
                    if (ch == quoteType) {
                        break;
                    }
                    if (ch == '\\') {
                        quoting = true;
                    }
                }
                sb.append(ch);
            }
            return sb.toString();
        };
    }

    private static void addPrimitiveParser(Class<?> primitiveType, PrimitiveParser parser) {
        SimpleValueParser simpleValueParser = primitiveParserToSimpleValueParser(parser);
        _parsersByType.put(primitiveType, simpleValueParser);
        _parsersByType.put(PrimitiveUtil.wrap(primitiveType), simpleValueParser);
    }

    private static SimpleValueParser primitiveParserToSimpleValueParser(PrimitiveParser parser) {
        return (chars, idx) -> {
            StringBuilder sb = new StringBuilder();
            while (idx[0] < chars.length) {
                char ch = chars[idx[0]++];
                if (ch == ']' || ch == ',') {
                    idx[0]--;
                    break;
                }
                sb.append(ch);
            }
            return parser.parse(sb.toString());
        };
    }

    public static SimpleValueParser getParser(Class<?> resultType) {
        return _parsersByType.get(resultType);
    }
}
