package org.literpc.remoting.transport.netty.client.support;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this class has only responsibility  only cache inetSocketAddress-channel map
 * @autor sheltersodom
 * @create 2021-04-18-22:12
 */
public class ChannelProvider {
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public Channel get(String rpcServiceName) {
        String key = rpcServiceName;
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                remove(rpcServiceName);
            }
        }
        return null;
    }

    public void put(String rpcServiceName, Channel channel) {
        String key = rpcServiceName;
        channelMap.put(key, channel);
    }

    public void remove(String rpcServiceName) {
        String key = rpcServiceName;
        channelMap.remove(key);
    }

}
