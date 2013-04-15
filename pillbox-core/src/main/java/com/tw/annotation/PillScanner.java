package com.tw.annotation;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PillScanner {
    private Map pillMap;

    public void scanPackage(String packageName) {
        try {
            List<Class> clazzs = getClassNamesFromPackage(packageName);
            pillMap = buildPillMap(clazzs);
        } catch (IOException e) {
        }

    }

    public Map getPillMap() {
        return pillMap;
    }

    private List<Class> getClassNamesFromPackage(String packageName) throws IOException {
        final String packageNameInFileSystem = packageName.replace(".", "/");
        URL packageURL = Thread.currentThread().getContextClassLoader().getResource(packageName);
        List<File> files = Lists.newArrayList(new File(packageURL.getFile()).listFiles());
        return Lists.transform(files, new Function<File, Class>() {
            @Override
            public Class apply(@Nullable File file) {
                try {
                    return buildClass(packageNameInFileSystem, file);
                } catch (ClassNotFoundException e) {
                    return Object.class;
                }
            }
        });

    }

    private Class buildClass(String packageName, File actual) throws ClassNotFoundException {
        final String className = packageName + "." + actual.getName();
        return Class.forName(className.replaceFirst("\\.class", ""));
    }

    private Map buildPillMap(List<Class> clazzs) {
        Map<Object, Object> pillMap = Maps.newHashMap();
        for (Class clazz : clazzs) {
            Map<Object, Object> brufenMap = Maps.newHashMap();
            Pill pillAnnotation = (Pill) clazz.getAnnotation(Pill.class);
            brufenMap.put("class", clazz.getName());
            brufenMap.put("properties", buildInjections(clazz));
            pillMap.put(pillAnnotation.name(), brufenMap);
        }
        return pillMap;
    }

    private Map buildInjections(Class clazz) {
        final HashMap<String, String> injectionMap = Maps.newHashMap();
        for (Field field : clazz.getDeclaredFields()) {
            AutoInject injectAnnotation = field.getAnnotation(AutoInject.class);
            injectionMap.put(field.getName(), injectAnnotation.name());
        }
        return injectionMap;
    }
}
