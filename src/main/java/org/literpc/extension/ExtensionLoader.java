package org.literpc.extension;

import lombok.extern.slf4j.Slf4j;
import org.literpc.utils.PropertiesFileUtil;

import javax.xml.validation.Schema;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import java.io.*;
import java.net.URL;
import java.security.cert.Extension;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor sheltersodom
 * @create 2021-04-23-10:21
 */
@Slf4j
public class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final String FILE_SUFFIX = ".properties";


    private final Class<T> type;
    /**
     * only cache for the extension
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_CACHE = new ConcurrentHashMap<>();
    /**
     * cache the mapping between class and object Ensure that there is only one instance in the heap space
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCE = new ConcurrentHashMap<>();
    /**
     * cache the mapping between alias in file and object
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    /**
     * save the file key-value
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalStateException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalStateException("Extension type must be an interface.");
        }
        if (Objects.isNull(type.getAnnotation(SPI.class))) {
            throw new IllegalStateException("Extension type must be annotated by @SPI");
        }

        ExtensionLoader<?> extensionLoader = EXTENSION_CACHE.get(type);
        if (Objects.isNull(extensionLoader)) {
            EXTENSION_CACHE.putIfAbsent(type, new ExtensionLoader<>(type));
            extensionLoader = EXTENSION_CACHE.get(type);
        }
        return (ExtensionLoader<S>) extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }

        Holder<Object> holder = cachedInstances.get(name);
        if (Objects.isNull(holder)) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        //DCL
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCE.get(clazz);
        try {
            if (instance == null) {
                EXTENSION_INSTANCE.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCE.get(clazz);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage());
        }
        return instance;


    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> classes) {
        String fileName = SERVICE_DIRECTORY + type.getName() + FILE_SUFFIX;
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Properties properties = PropertiesFileUtil.readPropertiesFile(fileName);
            loadResource(classes, classLoader, properties);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private void loadResource(Map<String, Class<?>> classes, ClassLoader classLoader, Properties properties) {
        try {
            for (String key : properties.stringPropertyNames()) {
                String clazzName = properties.getProperty(key);
                Class<?> clazz = classLoader.loadClass(clazzName);
                classes.put(key, clazz);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    class Holder<T> {

        private volatile T value;

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
        }
    }

}
