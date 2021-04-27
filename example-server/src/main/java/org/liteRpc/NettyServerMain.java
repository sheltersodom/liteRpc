package org.liteRpc;


import org.literpc.anntation.RpcServiceScan;
import org.literpc.api.HelloService;
import org.literpc.remoting.transport.netty.server.NettyRpcServer;

/**
 * Server: Automatic registration service via @RpcService annotation
 *
 * @author shuang.kou
 * @createTime 2020年05月10日 07:25:00
 */
@RpcServiceScan(value = "org.liteRpc.impl")
public class NettyServerMain {
    public static void main(String[] args) {
        NettyRpcServer nettyRpcServer = NettyRpcServer.getNettyRpcServer();
        nettyRpcServer.start();
    }
}
