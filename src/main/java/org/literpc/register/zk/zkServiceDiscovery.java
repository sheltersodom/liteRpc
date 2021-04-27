package org.literpc.register.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.literpc.enums.RpcExceptionMessageEnum;
import org.literpc.exception.RpcException;
import org.literpc.extension.ExtensionLoader;
import org.literpc.loadbalance.LoadBalance;
import org.literpc.loadbalance.support.ConsistentHashLoadBalance;
import org.literpc.register.ServiceDiscovery;
import org.literpc.register.zk.util.CuratorUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @autor sheltersodom
 * @create 2021-04-19-17:21
 */
@Slf4j
public class zkServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public zkServiceDiscovery() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(InetAddress address, String rpcServiceName) {
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcExceptionMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, address + "/" + rpcServiceName);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
