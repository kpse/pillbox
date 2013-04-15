package com.tw.container;

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class PillContainer {

    private PillBox pillbox;
    private Map<Class, Class> classHashMap;
    private Map<Class, Lifecycle> lifecycleMap;

    public static PillContainer createRawBox() {
        return new PillContainer();
    }

    public PillContainer() {
        classHashMap = Maps.newHashMap();
        lifecycleMap = Maps.newHashMap();
    }

    public <T> void register(Class<T> aClass, Class<? extends T> implementationClass, Lifecycle lifecycle) {
        classHashMap.put(aClass, implementationClass);
        lifecycleMap.put(aClass, lifecycle);
        pillbox = PillBox.fromMap(classHashMap, lifecycleMap);
    }

    public <T> void register(Class<T> aClass, Class<? extends T> implementationClass) {
        register(aClass, implementationClass, Lifecycle.Transient);
    }

    public <T> T get(Class<T> aClass)
            throws IllegalAccessException,
            InstantiationException,
            ClassNotFoundException,
            NoSuchMethodException,
            NoSuchFieldException,
            InvocationTargetException {
        return pillbox.createPill(aClass);

    }

    public <T> void register(Class<T> aClass, T instance) {
    }
}
