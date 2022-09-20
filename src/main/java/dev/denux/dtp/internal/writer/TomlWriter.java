package dev.denux.dtp.internal.writer;

import dev.denux.dtp.exception.TomlWriteException;
import dev.denux.dtp.utils.PrimitiveUtil;

import java.lang.reflect.Field;

public class TomlWriter {

    public final Object object;

    public TomlWriter(Object object) {
        this.object = object;
    }

    public String writeToString() {
        StringBuilder builder = new StringBuilder();
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            try {
                field.setAccessible(true);
                Class<?> clazz = PrimitiveUtil.wrap(field.getType());
                Object fieldObj = field.get(object);
                if (fieldObj == null) {
                    try {
                        fieldObj = clazz.newInstance();
                    } catch (InstantiationException exception) {
                        throw new TomlWriteException(String.format("Due to the value of %s " +
                                "being null we tried to create a new instance of %s but this also" +
                                "failed. Skipping field.", field.getName(), clazz.getSimpleName()));
                    }
                }
                if (String.class.equals(clazz)) {
                    handleString(field, fieldObj, builder);
                } else if (Number.class.isAssignableFrom(clazz)) {
                    handleNumber(field, fieldObj, builder);
                } else if (Boolean.class.equals(clazz)) {
                    handleOther(field, fieldObj, builder);
                }
            } catch (IllegalAccessException ignored) {
                continue;
            }
            if (i != fields.length - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private void handleString(Field field, Object fieldObject, StringBuilder builder) {
        String str = String.format("%s = \"%s\"", field.getName(), fieldObject.toString());
        builder.append(str);
    }

    private void handleOther(Field field, Object fieldObject, StringBuilder builder) {
        String str = String.format("%s = %s", field.getName(), fieldObject.toString());
        builder.append(str);
    }

    private void handleNumber(Field field, Object fieldObject, StringBuilder builder) {
        Number number = Double.parseDouble(fieldObject.toString());
        if (number.equals(Double.NaN)) {
            fieldObject = "nan";
        } else if (number.equals(Double.POSITIVE_INFINITY)) {
            fieldObject = "+inf";
        } else if (number.equals(Double.NEGATIVE_INFINITY)) {
            fieldObject = "-inf";
        }
        handleOther(field, fieldObject, builder);
    }
}
