package org.literpc.register.zk;

import org.literpc.register.ServiceRegistry;
import org.literpc.register.zk.util.CuratorUtils;

import java.net.InetSocketAddress;

/**
 * @autor sheltersodom
 * @create 2021-04-19-19:53
 */
public class zkServiceRegistry implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        CuratorUtils.createPersistentNode(rpcServiceName, inetSocketAddress);
    }
}
