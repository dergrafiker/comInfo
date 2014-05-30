package com.commerzinfo.util;

import java.lang.reflect.Field;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static boolean allFieldsFilled(Object o) {
        try {
            if (o == null) {
                return false;
            }
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (null == field.get(o)) {
                    return false;
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Problem while accessing fields of class " + o.getClass().getName(), e);
        }
    }
}
