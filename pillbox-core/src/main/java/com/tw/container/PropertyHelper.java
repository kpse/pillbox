package com.tw.container;

import java.lang.reflect.Method;

public class PropertyHelper {
    public static String setterNameOf(String propertyName) {
        return "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    public static String propertyNameOf(Method setter) {
        final String name = setter.getName();
        final String removePrefix = name.replace("set", "");
        return Character.toLowerCase(removePrefix.charAt(0)) + removePrefix.substring(1);
    }
}
