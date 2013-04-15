package com.tw.container;

import com.google.common.collect.Maps;

import java.util.Map;

public class PillContext {
    public static final String PILL_ID = "id";
    public static final String SCOPE = "scope";
    public static final String CLASS_KEY = "class";
    public static final String SINGLETON_SCOPE = "singleton";

    private final Map<String, Map<String, Object>> map;
    private final Map<String, Object> allInstancesCache;

    public PillContext(Map pillMap) {
        map = Maps.newHashMap(pillMap);
        allInstancesCache = Maps.newHashMap();
    }

    public Map<String, Object> getPill(String pillName) {
        Map<String, Object> properties = map.get(pillName);
        if (null == properties) {
            properties = Maps.newHashMap();
        }
        properties.put(PILL_ID, pillName);
        return properties;
    }

    public Class<?> getPillClass(String pillName) throws ClassNotFoundException {
        Map<String, Object> pill = getPill(pillName);
        return Class.forName(pill.get(CLASS_KEY).toString());
    }

    public Object lookupFromCache(String pillName) {
        Map objectInfo = getPill(pillName);
        if (objectInfo.containsKey(SCOPE) && SINGLETON_SCOPE.equals(objectInfo.get(SCOPE))) {
            return allInstancesCache.get(pillName);
        }
        return null;
    }

    public void cache(Map<String, Object> objectInfo, Object target) {
        String cacheKey = (String) objectInfo.get(PILL_ID);
        if (allInstancesCache.containsKey(cacheKey)) {
            return;
        }
        allInstancesCache.put(cacheKey, target);

    }
}
