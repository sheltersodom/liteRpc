package org.literpc.remoting.transport.netty.server.support;

import lombok.extern.slf4j.Slf4j;
import org.literpc.enums.RpcExceptionMessageEnum;
import org.literpc.exception.RpcException;
import org.literpc.extension.ExtensionLoader;
import org.literpc.register.ServiceRegistry;
import org.literpc.register.zk.zkServiceRegistry;
import org.literpc.remoting.transport.netty.server.NettyRpcServer;
import org.literpc.remoting.transport.netty.server.ServiceProvider;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor sheltersodom
 * @create 2021-04-19-22:01
 */
@Slf4j
public class DefaultServiceProvider implements ServiceProvider {
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
//    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    private final ServiceRegistry serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");

    @Override
    public void addService(Object service, String rpcServiceName) {
        if(serviceMap.containsKey(rpcServiceName)) {
            return;
        }
        serviceMap.put(rpcServiceName,service);
        log.info("Add service: {} and interfaces:{}", rpcServiceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if(service==null){
            throw new RpcException(RpcExceptionMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(Object service, String rpcServiceName) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(service,rpcServiceName);
            serviceRegistry.registerService(rpcServiceName,new InetSocketAddress(host, NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }
}
