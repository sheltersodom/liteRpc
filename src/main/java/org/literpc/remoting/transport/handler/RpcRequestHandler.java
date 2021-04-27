package org.literpc.remoting.transport.handler;

import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.exception.RpcException;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.transport.netty.server.ServiceProvider;
import org.literpc.remoting.transport.netty.server.support.DefaultServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @autor sheltersodom
 * @create 2021-04-20-22:34
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getSingletonFactory().getInstance(DefaultServiceProvider.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcPropertiesName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        try {
            Method method = service.getClass().getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return result;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return e.getCause();
        }
    }

}
