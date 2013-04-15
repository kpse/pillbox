package com.tw.container;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.tw.annotation.PillScanner;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterators.filter;
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
        return createObject(objectInfo, pillContext.getPillImplClass(pillName));
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
            Method m = objectClass.getMethod(PropertyHelper.setterNameOf(prop.getKey()), pillContext.getPillClass(prop.getValue()));
            m.invoke(object, createRawPill(prop.getValue()));
        }
        return object;
    }

    private Object createObjectWithConstructor(Class<?> clazz, Map argPills)
            throws NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        final List<Class<?>> classesOfArgs = getClassesOfArgs(argPills);
        Constructor<?> ctor = matchConstructor(clazz, classesOfArgs);
        final List<Object> objectsOfArgs = getObjectsOfArgs(argPills);
        return ctor.newInstance(objectsOfArgs.toArray());
    }

    private Constructor<?> matchConstructor(Class<?> clazz, List<Class<?>> classesOfArgs) throws NoSuchMethodException {
        try {
            return clazz.getConstructor(classesOfArgs.toArray(new Class<?>[0]));
        } catch (NoSuchMethodException e) {
            return clazz.getConstructors()[0];
        } catch (SecurityException e) {

        }
        return null;
    }

    private List<Object> getObjectsOfArgs(Map<String, String> argPills) {
        return Lists.newArrayList(transform(argPills.entrySet().iterator(), new Function<Map.Entry<String, String>, Object>() {
            @Override
            public Object apply(Map.Entry<String, String> entry) {
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
            public Class<?> apply(Map.Entry<String, String> entry) {
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

    public static PillBox fromMap(Map<Class, Class> classHashMap, Map<Class, Lifecycle> lifecycleMap) {
        Map<Object, Object> pillMap = Maps.newHashMap();
        for (Map.Entry<Class, Class> clazzEntry : classHashMap.entrySet()) {
            Map<Object, Object> objectMap = Maps.newHashMap();
            objectMap.put("class", clazzEntry.getValue().getCanonicalName());
            objectMap.put("constructor-args", buildConstructorArgs(clazzEntry.getValue()));
            objectMap.put("properties", buildSetterArgs(clazzEntry.getValue()));
            objectMap.put("scope", lifecycleMap.get(clazzEntry.getKey()).scopeName());
            pillMap.put(clazzEntry.getKey().getCanonicalName(), objectMap);
            pillMap.put(clazzEntry.getValue().getCanonicalName(), objectMap);
        }
        return new PillBox(new PillContext(pillMap));
    }

    private static Map buildSetterArgs(Class implClass) {
        final List<Method> methods = Lists.newArrayList(implClass.getDeclaredMethods());
        final UnmodifiableIterator<Method> setterMethods = filter(methods.iterator(), new Predicate<Method>() {
            @Override
            public boolean apply(Method method) {
                return method.getName().startsWith("set");
            }
        });
        final HashMap<String, String> propertiesMap = Maps.newHashMap();
        for (final Method setterMethod : Lists.newArrayList(setterMethods)) {
            propertiesMap.put(PropertyHelper.propertyNameOf(setterMethod), setterMethod.getParameterTypes()[0].getCanonicalName());
        }
        return propertiesMap;
    }

    private static Map<String, String> buildConstructorArgs(Class implClass) {
        return reportConstructorArgs(implClass.getConstructors()[0]);
    }

    private static Map<String, String> reportConstructorArgs(Constructor<?> constructor) {
        final Map<String, String> map = Maps.newHashMap();
        for (Class<?> para : constructor.getParameterTypes()) {
            map.put(para.getCanonicalName(), para.getCanonicalName());
        }
        return map;
    }

    public <T> T createPill(Class<T> aClass) throws NoSuchMethodException, IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchFieldException {
        return createPill(aClass.getCanonicalName());
    }

}
