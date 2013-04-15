package com.tw.container;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.istack.internal.Nullable;
import com.tw.annotation.PillScanner;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterators.transform;

public class PillBox {

    public static final String CONSTRUCTOR_ARGS_KEY = "constructor-args";
    public static final String PROPERTIES_KEY = "properties";

    private final PillContext pillContext;

    private PillBox(PillContext pillContext) {
        this.pillContext = pillContext;

    }

    protected Object createRawPill(String pillName) throws ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchFieldException {
        final Map<String, Object> objectInfo = pillContext.getPill(pillName);

        Object target = pillContext.lookupFromCache(pillName);
        if (target != null) {
            return target;
        }
        return createObject(objectInfo, pillContext.getPillClass(pillName));
    }

    private Object createObject(Map<String, Object> objectInfo, Class<?> clazz)
            throws ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException,
            NoSuchFieldException {
        final Map pillArgs = objectInfo.containsKey(CONSTRUCTOR_ARGS_KEY) ?
                (Map) objectInfo.get(CONSTRUCTOR_ARGS_KEY) :
                Maps.newHashMap();

        Object object = createObjectWithConstructor(clazz, pillArgs);
        Map<String, String> properties = objectInfo.containsKey(PROPERTIES_KEY) ?
                (Map<String, String>) objectInfo.get(PROPERTIES_KEY)
                : Maps.<String, String>newHashMap();
        Object target = setProperties(object, properties);
        pillContext.cache(objectInfo, target);
        return target;
    }

    private Object setProperties(Object object, Map<String, String> properties)
            throws NoSuchFieldException,
            IllegalAccessException,
            ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            InvocationTargetException {
        Class<?> objectClass = object.getClass();
        for (Map.Entry<String, String> prop : properties.entrySet()) {
            Method m = objectClass.getMethod(setterNameOf(prop.getKey()), pillContext.getPillClass(prop.getValue()));
            m.invoke(object, createRawPill(prop.getValue()));
        }
        return object;
    }

    private String setterNameOf(String propertyName) {
        return "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    private Object createObjectWithConstructor(Class<?> clazz, Map argPills)
            throws NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        final List<Class<?>> classesOfArgs = getClassesOfArgs(argPills);
        Constructor<?> ctor = clazz.getConstructor(classesOfArgs.toArray(new Class<?>[0]));
        final List<Object> objectsOfArgs = getObjectsOfArgs(argPills);
        return ctor.newInstance(objectsOfArgs.toArray());
    }

    private List<Object> getObjectsOfArgs(Map<String, String> argPills) {
        return Lists.newArrayList(transform(argPills.entrySet().iterator(), new Function<Map.Entry<String, String>, Object>() {
            @Override
            public Object apply(@Nullable Map.Entry<String, String> entry) {
                try {
                    return createRawPill(entry.getValue());
                } catch (ClassNotFoundException e) {
                } catch (InvocationTargetException e) {
                } catch (NoSuchMethodException e) {
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                } catch (NoSuchFieldException e) {
                }
                return null;
            }
        }));
    }

    private List<Class<?>> getClassesOfArgs(Map<String, String> args) {
        return Lists.newArrayList(transform(args.entrySet().iterator(), new Function<Map.Entry<String, String>, Class<?>>() {
            @Override
            public Class<?> apply(@Nullable Map.Entry<String, String> entry) {
                try {
                    return pillContext.getPillClass(entry.getValue());
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
        }));
    }

    public static PillBox loadContext(String path) throws FileNotFoundException {
        PillContext pillContext = new ContextLoader().getContextDefinition(path);
        return new PillBox(pillContext);
    }

    public <T> T createPill(String name) throws NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException, InvocationTargetException, ClassNotFoundException {
        return (T) createRawPill(name);
    }

    public static PillBox fromScanner(PillScanner pillScanner) {
        return new PillBox(new PillContext(pillScanner.getPillMap()));
    }
}
