package com.tw.container;

import com.google.common.collect.Maps;
import com.tw.container.exception.MultipleConstructorsException;
import com.tw.container.exception.MultipleSetterException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ContainerGuardian {
    public static void assertSingleSetterForEachType(Class<?> implementationClass) throws MultipleSetterException {
        final List<Method> setterMethods = ClassHelper.getSetterMethods(implementationClass);

        final Map<Class, Class> verifyMap = Maps.newHashMap();
        for (Method method : setterMethods) {
            final Class<?> parameterType = method.getParameterTypes()[0];
            if (verifyMap.containsKey(parameterType)) {
                throw new MultipleSetterException();
            }
            verifyMap.put(parameterType, parameterType);
        }

    }

    public static void assertSingleConstructor(Class<?> implementationClass) throws MultipleConstructorsException {
        if (implementationClass.getConstructors().length > 1) {
            throw new MultipleConstructorsException();
        }

    }
}
