package com.tw.container;

public enum Lifecycle {
    Transient("prototype"), Singleton("singleton");
    private final String scopeName;

    private Lifecycle(String scopeName) {
        this.scopeName = scopeName;
    }

    public static boolean isSingleton(Object lifecycle) {
        return Singleton.scopeName().equals(lifecycle);
    }

    public String scopeName() {
        return scopeName;
    }
}
