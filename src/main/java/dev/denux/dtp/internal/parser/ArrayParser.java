package dev.denux.dtp.internal.parser;

import dev.denux.dtp.exception.parse.ArrayParseException;
import dev.denux.dtp.internal.reader.ArrayReader;
import dev.denux.dtp.internal.reader.TomlReader;
import dev.denux.dtp.utils.ArrayUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
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
        boolean isPrimitive = cType.isPrimitive();
        if (isPrimitive) {
            try {
                parsePrimitiveArray(values, field.getType().getComponentType(), null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new ArrayParseException("Could not parse object array");
            }
        } else {
            try {
                parseObjectArray(values, field.getType().getComponentType(), null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new ArrayParseException("Could not parse object array");
            }
            return;
        }

        if (!field.getType().isArray()) {
            throw new IllegalArgumentException("Field is not an array");
        }
        if (field.getType().getComponentType().isPrimitive()) {
            try {
                parsePrimitiveArray(values, field.getType(), null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new ArrayParseException("Could not parse primitive type array");
            }
        } else {
            try {
                parseObjectArray(values, field.getType().getComponentType(), null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new ArrayParseException("Could not parse object array");
            }
        }
    }

    private void parseObjectArray(Object values, Class<?> type, ArrayReader arrayReader) throws IllegalAccessException {
        if (arrayReader == null) {
            int arrayReaderId =  Integer.parseInt(values.toString());
            arrayReader = tomlReader.getArrayReaderByMapKey(arrayReaderId);
        }
        if (type.getComponentType() != null) {
            parsePrimitiveArray(object, type.getComponentType(), arrayReader);
            return;
        }

        String string = arrayReader.getString();
        List<String> list = ArrayUtil.stringToObjectArray(string, type, null);
        int i = 0;
        if (Byte.class.equals(type)) {
            System.out.println(Arrays.toString(ArrayUtil.listToObjectArray(list)));
            field.set(object, ArrayUtil.listToByteArray(list));
        } else if (Short.class.equals(type)) {
            Short[] array = new Short[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Short.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (Integer.class.equals(type)) {
            Integer[] array = new Integer[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Integer.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (Long.class.equals(type)) {
            Long[] array = new Long[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Long.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (Float.class.equals(type)) {
            Float[] array = new Float[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Float.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (Double.class.equals(type)) {
            Double[] array = new Double[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Double.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (String.class.equals(type)) {
            String[] array = new String[list.size()];
            for (Object obj : list) {
                Array.set(array, i, obj.toString());
                i++;
            }
            field.set(object, array);
        } else if (Boolean.class.equals(type)) {
            Boolean[] array = new Boolean[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Boolean.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
        }
    }

    private void parsePrimitiveArray(Object values, Class<?> type, ArrayReader arrayReader) throws IllegalAccessException {
        if (arrayReader == null) {
            int arrayReaderId =  Integer.parseInt(values.toString());
            arrayReader = tomlReader.getArrayReaderByMapKey(arrayReaderId);
        }
        if (type.getComponentType() != null) {
            parsePrimitiveArray(object, type.getComponentType(), arrayReader);
            return;
        }

        String string = arrayReader.getString();
        List<String> list = ArrayUtil.stringToObjectArray(string, type, null);
        int i = 0;
        if (byte.class.equals(type)) {
            System.out.println(Arrays.toString(ArrayUtil.listToObjectArray(list)));
            field.set(object, ArrayUtil.listToObjectArray(list));
        } else if (short.class.equals(type)) {
            short[] array = new short[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Short.parseShort(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (int.class.equals(type)) {
            int[] array = new int[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Integer.parseInt(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (long.class.equals(type)) {
            long[] array = new long[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Long.parseLong(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (float.class.equals(type)) {
            float[] array = new float[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Float.parseFloat(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (double.class.equals(type)) {
            double[] array = new double[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Double.parseDouble(obj.toString()));
                i++;
            }
            field.set(object, array);
        } else if (boolean.class.equals(type)) {
            boolean[] array = new boolean[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Boolean.parseBoolean(obj.toString()));
                i++;
            }
            field.set(object, array);
        }
    }
}
