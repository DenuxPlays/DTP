package dev.denux.dtp.internal.writer;

import dev.denux.dtp.exception.write.TomlWriteException;
import dev.denux.dtp.util.ArrayUtil;
import dev.denux.dtp.util.PrimitiveUtil;

import java.lang.reflect.Field;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TomlWriter {

    private final Object object;
    private final Class<?>[] subClasses;
    private boolean subClass = false;

    public TomlWriter(Object object) {
        this.object = object;
        subClasses = object.getClass().getDeclaredClasses();
    }

    public TomlWriter(Object object, boolean subClass) {
        this.object = object;
        subClasses = object.getClass().getDeclaredClasses();
        this.subClass = subClass;
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
                } else if (TemporalAccessor.class.isAssignableFrom(clazz)) {
                    handleOther(field, fieldObj, builder);
                } else if (Boolean.class.equals(clazz)) {
                    handleOther(field, fieldObj, builder);
                } else if (field.getType().isArray()) {
                    Class<?> cType = field.getType();
                    String arrayAsString;
                    while (cType.isArray()) {
                        cType = cType.getComponentType();
                    }
                    boolean isPrimitive = cType.isPrimitive();

                    if (isPrimitive) {
                        arrayAsString = buildPrimitiveArray(fieldObj, field.getType());
                    } else {
                        arrayAsString = Arrays.deepToString((Object[]) fieldObj);
                    }
                    //solves issue where an array is always one n above their actual type.
                    arrayAsString = arrayAsString.substring(1, arrayAsString.length() - 1);
                    handleOther(field, arrayAsString, builder);
                } else if (typeIsUsableClazz(clazz)) {
                    subClasses.put(clazz, field);
                    continue;
                }
                if (i != fields.length - 1) {
                    builder.append("\n");
                }
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
        try {
            Set<Map.Entry<Class<?>, Field>> entrySet = subClasses.entrySet();
            int i = 0;
            for (Map.Entry<Class<?>, Field> entry : entrySet) {
                i++;
                Class<?> clazz = entry.getKey();
                Field field = entry.getValue();
                if (clazz.getDeclaredFields().length != 0) {
                    if (i == 1) {
                        if (!builder.toString().isEmpty()) {
                            builder.append("\n").append("\n");
                        }
                    }
                    builder.append("[").append(field.getName()).append("]").append("\n");
                    builder.append(new TomlWriter(field.get(object), true).writeToString());
                    if (i != entrySet.size()) {
                        builder.append("\n").append("\n");
                    }
                }
            }
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        //builder.deleteCharAt(builder.lastIndexOf("\n"));
        return builder.toString();
    }

    private boolean typeIsUsableClazz(Class<?> clazz) {
        for (Class<?> subClass : subClasses) {
            subClass = PrimitiveUtil.wrap(subClass);
            if (subClass.equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    private void handleString(Field field, Object fieldObject, StringBuilder builder) {
        fieldObject = String.format("\"%s\"", fieldObject.toString());
        handleOther(field, fieldObject, builder);
    }

    private void handleOther(Field field, Object fieldObject, StringBuilder builder) {
        String str = String.format("%s = %s", field.getName(), fieldObject.toString());
        if (subClass) {
            builder.append("\t").append(str);
        } else {
            builder.append(str);
        }
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

    private String buildPrimitiveArray(Object fieldObject, Class<?> clazz) {
        List<Object> list = new ArrayList<>();
        ArrayUtil.addPrimitiveArrayToList(fieldObject, clazz, list);
        return Arrays.deepToString(list.toArray());
    }
}
