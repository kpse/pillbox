package com.tw.container;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Iterators.filter;

public class ClassHelper {

    public static List<Method> getSetterMethods(Class<?> implementationClass) {
        final List<Method> methods = Lists.newArrayList(implementationClass.getDeclaredMethods());

        return Lists.newArrayList(filter(methods.iterator(), new Predicate<Method>() {
            @Override
            public boolean apply(Method method) {
                return method.getName().startsWith("set") && method.getParameterTypes().length == 1;
            }
        }));
    }
}
