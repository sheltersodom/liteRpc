package org.literpc.register;

import org.literpc.extension.SPI;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @autor sheltersodom
 * @create 2021-04-18-22:25
 */
@SPI
public interface ServiceDiscovery {

    InetSocketAddress lookupService(InetAddress address, String rpcServiceName);
}
