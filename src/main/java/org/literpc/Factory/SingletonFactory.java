package org.literpc.Factory;

import org.literpc.proxy.RpcClientProxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor sheltersodom
 * @create 2021-04-23-16:47
 */
public class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();
    private static SingletonFactory singletonFactory;

    private SingletonFactory() {
    }

    public static SingletonFactory getSingletonFactory() {
        if (singletonFactory == null) {
            synchronized (SingletonFactory.class) {
                if (singletonFactory == null) {
                    singletonFactory = new SingletonFactory();
                }
            }
        }
        return singletonFactory;
    }

    public <T> T getInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalStateException("class Type must not be null");
        }
        String key = clazz.getName();
        Object instance = OBJECT_MAP.get(key);
        if (instance == null) {
            OBJECT_MAP.computeIfAbsent(key, k -> createInstance(clazz));
            instance = OBJECT_MAP.get(key);
        }
        return clazz.cast(instance);
    }

    private <T> T createInstance(Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

}
