package dev.denux.dtp.internal.parser;

import dev.denux.dtp.exception.TomlParseException;
import dev.denux.dtp.internal.entities.TomlTable;
import dev.denux.dtp.internal.reader.TomlReader;
import dev.denux.dtp.utils.PrimitiveUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
                } catch (NoSuchFieldException ignored) {
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
                    if (field.getType().isEnum()) {
                        try {
                            value = field.getType().getDeclaredMethod("valueOf", String.class).invoke(object,
                                    value.toString());
                        } catch (InvocationTargetException | NoSuchMethodException ignored) {
                            throw new TomlParseException("Could not get method \"value\" of from enum class.");
                        }
                        field.set(object, value);
                        break;
                    }
                    field.set(object, value.toString());
                    break;
                case ARRAY:
                    new ArrayParser<>(object, field).parseArray(value);
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
                    field.set(object, Boolean.parseBoolean(value.toString()));
                    break;
                case DATETIME:
                    field.set(object, LocalDateTime.parse(value.toString()));
                    break;
                case TIME:
                    field.set(object, LocalTime.parse(value.toString()));
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void parseNumber(T object, Field field, Object value) throws IllegalAccessException {
        Class<?> type = PrimitiveUtil.wrap(field.getType());
        Number number = Double.valueOf(value.toString());
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