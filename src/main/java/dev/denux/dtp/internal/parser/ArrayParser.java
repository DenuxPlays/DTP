package dev.denux.dtp.internal.parser;

import dev.denux.dtp.exception.parse.ArrayParseException;
import dev.denux.dtp.internal.reader.ArrayReader;
import dev.denux.dtp.internal.reader.TomlReader;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ArrayParser<T> {
    private final T object;
    private final Field field;
    private final TomlReader tomlReader;

    protected ArrayParser(T object, Field field, TomlReader tomlReader) {
        this.object = object;
        this.field = field;
        this.tomlReader = tomlReader;
    }

    public void parseArray(Object values) {
        Class<?> cType = field.getType();
        while (cType.isArray()) {
            cType = cType.getComponentType();
        }
        try {
            parseDirector(values, field.getType().getComponentType(), null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new ArrayParseException("Could not parse object array");
        }
    }

    private void parseDirector(Object values, Class<?> type, ArrayReader arrayReader) throws IllegalAccessException {
        if (arrayReader == null) {
            int arrayReaderId = Integer.parseInt(values.toString());
            arrayReader = tomlReader.getArrayReaderByMapKey(arrayReaderId);
        }
        if (type.getComponentType() != null) {
            parseDirector(object, type.getComponentType(), arrayReader);
            return;
        }

        String string = arrayReader.getString();
        field.set(object, parse(string.toCharArray(), field.getType(), new int[]{0}));
    }

    private Object parse(char[] chars, Class<?> resultType, int[] idx) {
        if (resultType.isArray()) {
            // assume we have an array in the string
            if (chars[idx[0]++] != '[') {
                throw new IllegalArgumentException();
            }
            List<Object> resultList = new ArrayList<>();
            while (idx[0] < chars.length) {
                char ch = chars[idx[0]++];
                if (ch == '[') {
                    idx[0]--;
                    resultList.add(parse(chars, resultType.getComponentType(), idx));
                } else if (ch == ']') {
                    break;
                }
                else {
                    idx[0]--;
                    resultList.add(parse(chars, resultType.getComponentType(), idx));
                }
                if (chars[idx[0]] == ',') {
                    idx[0]++;
                }
            }
            Object resultArray = Array.newInstance(resultType.getComponentType(), resultList.size());
            for (int i = 0; i < resultList.size(); i++) {
                Array.set(resultArray, i, resultList.get(i));
            }
            return resultArray;
        } else {
            SimpleValueParser parser = SimpleValueParsers.getParser(resultType);
            return parser.parse(chars, idx);
        }
    }
}
