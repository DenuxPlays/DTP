package dev.denux.internal;

import dev.denux.utils.MiscUtil;
import dev.denux.utils.TomlTable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

//TODO add javadocs
public class TomlParser<T> {

    private final Class<T> classOfT;
    private final T object;

    public TomlParser(Class<T> classOfT) throws ReflectiveOperationException {
        object = classOfT.newInstance();
        this.classOfT = classOfT;
    }

    public T parse(TomlReader reader) throws ReflectiveOperationException {
        for (TomlTable tomlMap : reader.getTomlMaps()) {
            String tableName = tomlMap.getTableName();
            for (TomlTable.Entry entry : tomlMap.getEntries()) {
                String key = entry.getKey();
                Field field;
                try {
                    if (!tableName.equals("")) {
                        field = classOfT.getDeclaredField(tableName);
                        field.setAccessible(true);
                        T newObject = (T) field.get(object);
                        field = field.getType().getDeclaredField(key);
                        setFieldData(field, newObject, entry);
                        continue;
                    } else {
                        field = classOfT.getDeclaredField(key);
                    }
                } catch (NoSuchFieldException e) {
                    System.out.println("Skipping field: " + entry.getKey());
                    e.printStackTrace();
                    continue;
                }
                setFieldData(field, object, entry);
            }
        }
        return object;
    }

    private void setFieldData(Field field, T object, TomlTable.Entry entry) {
        field.setAccessible(true);
        Object value = entry.getValue();
        try {
            switch (entry.getDataType()) {
                case STRING:
                    field.set(object, value.toString().substring(1, value.toString().length() - 1));
                    break;
                case ARRAY:
                    handleArray(object, field, value);
                    break;
                case NUMBER:
                    parseNumber(object, field, value);
                    break;
                case NAN:
                    field.set(object, Double.NaN);
                    break;
                case INFINITE_POSITIVE:
                    field.set(object, Double.POSITIVE_INFINITY);
                    break;
                case INFINITE_NEGATIVE:
                    field.set(object, Double.NEGATIVE_INFINITY);
                    break;
                case BOOLEAN:
                    field.set(object, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void handleArray(T object, Field field, Object values) throws IllegalAccessException {
        if (!field.getType().isArray()) {
            throw new IllegalArgumentException("Field is not an array");
        }
        if (field.getType().getComponentType().isPrimitive()) {
            parsePrimitiveArray(object, field, values);
        } else {
            parseObjectArray(object, field, values);
        }
    }

    private void parseObjectArray(T object, Field field, Object values) throws IllegalAccessException {
        Class<?> type = field.getType().getComponentType();
        List<Object> list = (List<Object>) values;
        int i = 0;
        if (Byte.class.equals(type)) {
            Byte[] array = new Byte[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Byte.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
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

    private void parsePrimitiveArray(T object, Field field, Object values) throws IllegalAccessException {
        Class<?> type = field.getType().getComponentType();
        if (!type.isPrimitive()) {
            throw new IllegalArgumentException("Field type must be primitive");
        }
        int i = 0;
        List<Object> list = (List<Object>) values;
        if (byte.class.equals(type)) {
            byte[] array = new byte[list.size()];
            for (Object obj : list) {
                Array.set(array, i, Byte.valueOf(obj.toString()));
                i++;
            }
            field.set(object, array);
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

    private void parseNumber(T object, Field field, Object value) throws IllegalAccessException {
        Class<?> type = MiscUtil.warpPrimitives(field.getType());
        Number number = (Number) value;
        if (Byte.class.equals(type)) {
            if (number.doubleValue() > Byte.MAX_VALUE) {
                throw new NumberFormatException(String.format("Value: %s is too large for Byte", number));
            }
            field.set(object, number.byteValue());
        } else if (Short.class.equals(type)) {
            if (number.doubleValue() > Short.MAX_VALUE) {
                throw new NumberFormatException(String.format("Value: %s is too large for Short", number));
            }
            field.set(object, number.shortValue());
        } else if (Integer.class.equals(type)) {
            if (number.doubleValue() > Integer.MAX_VALUE) {
                throw new NumberFormatException(String.format("Value: %s is too large for Integer", number));
            }
            field.set(object, number.intValue());
        } else if (Long.class.equals(type)) {
            if (number.doubleValue() > Long.MAX_VALUE) {
                throw new NumberFormatException(String.format("Value: %s is too large for Long", number));
            }
            field.set(object, number.longValue());
        } else if (Float.class.equals(type)) {
            if (number.doubleValue() > Float.MAX_VALUE) {
                throw new NumberFormatException(String.format("Value: %s is too large for Float", number));
            }
            field.set(object, number.floatValue());
        } else if (Double.class.equals(type)) {
            if (number.doubleValue() > Double.MAX_VALUE) {
                throw new NumberFormatException(String.format("Value: %s is too large for Double", number));
            }
            field.set(object, number.doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + type);
        }
    }
}