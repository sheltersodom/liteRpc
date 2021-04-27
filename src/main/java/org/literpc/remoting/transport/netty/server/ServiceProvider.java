package org.literpc.remoting.transport.netty.server;

/**
 * @autor sheltersodom
 * @create 2021-04-19-21:56
 */
public interface ServiceProvider {
    void addService(Object service, String rpcServiceName);

    Object getService(String rpcServiceName);

    void publishService(Object service, String rpcServiceName);
}
