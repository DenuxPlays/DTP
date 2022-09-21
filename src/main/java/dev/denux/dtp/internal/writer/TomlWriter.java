package dev.denux.dtp.internal.writer;

import dev.denux.dtp.exception.TomlWriteException;
import dev.denux.dtp.utils.PrimitiveUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TomlWriter {

    private final Object object;
    private final Class<?>[] subClasses;

    public TomlWriter(Object object) {
        this.object = object;
        subClasses = object.getClass().getDeclaredClasses();
    }

    public String writeToString() {
        StringBuilder builder = new StringBuilder();
        Field[] fields = object.getClass().getDeclaredFields();
        Map<Class<?>, Field> subClasses = new HashMap<>();
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
                } else if (typeIsClazz(clazz)) {
                    subClasses.put(clazz, field);
                    continue;
                }
            } catch (IllegalAccessException ignored) {
                ignored.printStackTrace();
            }
            if (i != fields.length - 1) {
                builder.append("\n");
            }
        }
        try {
            Set<Map.Entry<Class<?>, Field>> entrySet = subClasses.entrySet();
            if (entrySet.size() != 0) {
                builder.append("\n");
            }
            for (Map.Entry<Class<?>, Field> entry : entrySet) {
                Class<?> clazz = entry.getKey();
                Field field = entry.getValue();
                if (clazz.getDeclaredFields().length != 0) {
                    builder.append("\n").append("[").append(field.getName()).append("]").append("\n");
                    builder.append(new TomlWriter(field.get(object)).writeToString());
                }
            }
        } catch (IllegalAccessException ignored) {
            ignored.printStackTrace();
        }
        return builder.toString();
    }

    private boolean typeIsClazz(Class<?> clazz) {
        for (Class<?> subClass : subClasses) {
            subClass = PrimitiveUtil.wrap(subClass);
            if (subClass.equals(clazz)) {
                return true;
            }
        }
        return false;
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
