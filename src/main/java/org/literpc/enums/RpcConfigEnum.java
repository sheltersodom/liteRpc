package org.literpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @autor sheltersodom
 * @create 2021-04-19-16:05
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address"),
    CLIENT_PROXY("clientProxy.properties");

    private final String propertyValue;
}
