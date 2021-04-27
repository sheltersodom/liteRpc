package org.literpc.remoting.transport;

import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.anntation.RpcService;
import org.literpc.anntation.RpcServiceScan;
import org.literpc.enums.RpcConfigEnum;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.transport.netty.server.ServiceProvider;
import org.literpc.remoting.transport.netty.server.support.DefaultServiceProvider;
import org.literpc.utils.ReflectUtil;

import java.util.Set;

/**
 * @autor sheltersodom
 * @create 2021-04-23-17:28
 */
@Slf4j
public abstract class AbstractRpcServer {
    private final ServiceProvider serviceProvider = SingletonFactory.getSingletonFactory().getInstance(DefaultServiceProvider.class);

    public void scanServices() {
        String mainClassName = null;
        Class<?> startClass = null;
        try {
            mainClassName = ReflectUtil.getStackTrace();
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(RpcServiceScan.class)) {
                log.error("Start class missing @ ServiceScan Annotation");
                throw new IllegalStateException("Start class missing @ ServiceScan Annotation");
            }
        } catch (ClassNotFoundException e) {
            log.error("出现未知错误");
            throw new IllegalStateException("An unknown error occurred");
        }

        String backPackage = startClass.getAnnotation(RpcServiceScan.class).value();
        if ("".equals(backPackage)) {
            backPackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }

        Set<Class<?>> classSet = ReflectUtil.getClasses(backPackage);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(RpcService.class)) {
                Object instance = SingletonFactory.getSingletonFactory().getInstance(clazz);
                String serviceName = clazz.getAnnotation(RpcService.class).name();
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> inf : interfaces) {
                        serviceProvider.publishService(instance, inf.getCanonicalName() + RpcConstants.VERSION);
                    }
                } else {
                    serviceProvider.publishService(instance, serviceName);
                }
            }
        }
    }
}
