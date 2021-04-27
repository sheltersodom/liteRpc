package org.literpc.loadbalance;

import java.util.List;

/**
 * @autor sheltersodom
 * @create 2021-04-23-10:16
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, String InetAddressRpcName) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, InetAddressRpcName);
    }

    protected abstract String doSelect(List<String> serviceAddresses, String inetAddressRpcName);
}
