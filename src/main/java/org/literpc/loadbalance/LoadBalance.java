package org.literpc.loadbalance;

import org.literpc.extension.SPI;

import java.util.List;

/**
 * @autor sheltersodom
 * @create 2021-04-18-22:31
 */
@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);
}
