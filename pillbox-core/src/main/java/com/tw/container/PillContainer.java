package com.tw.container;

import com.google.common.collect.Maps;
import com.tw.container.exception.ComponentNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class PillContainer {

    private PillBox pillbox;
    private Map<Class, Class> classHashMap;
    private Map<Class, Lifecycle> lifecycleMap;
    private PillContainer parent;

    public static PillContainer createFrom(PillContainer parentContainer) {
        return new PillContainer(parentContainer);
    }

    public static PillContainer createRawBox() {
        return new PillContainer();
    }

    private PillContainer(PillContainer parent) {
        classHashMap = Maps.newHashMap();
        lifecycleMap = Maps.newHashMap();
        this.parent = parent;
        merge(parent);
    }

    private void merge(PillContainer parent) {
        PillContainer current = this;
        while (current.parent != null) {
            classHashMap.putAll(current.parent.classHashMap);
            lifecycleMap.putAll(current.parent.lifecycleMap);
            current = current.parent;
        }
    }

    private PillContainer() {
        this(null);
    }

    public <T> void register(Class<T> aClass, Class<? extends T> implementationClass, Lifecycle lifecycle) {
        classHashMap.put(aClass, implementationClass);
        lifecycleMap.put(aClass, lifecycle);
        merge(parent);
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
            InvocationTargetException, ComponentNotFoundException {
        return pillbox.createPill(aClass);

    }

    public <T> void register(Class<T> aClass, T instance) {
    }
}
