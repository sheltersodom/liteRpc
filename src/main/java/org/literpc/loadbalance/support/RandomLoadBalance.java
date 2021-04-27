package org.literpc.loadbalance.support;

import org.literpc.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @autor sheltersodom
 * @create 2021-04-23-10:16
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, String inetAddressRpcName) {
        Random r = new Random();
        return serviceAddresses.get(r.nextInt(serviceAddresses.size()));
    }
}
