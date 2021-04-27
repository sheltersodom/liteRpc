package org.liteRpc;


import org.literpc.anntation.RpcServiceScan;
import org.literpc.remoting.transport.netty.server.NettyRpcServer;
import org.literpc.remoting.transport.socket.SocketRpcClient;
import org.literpc.remoting.transport.socket.SocketRpcServer;

/**
 * Server: Automatic registration service via @RpcService annotation
 *
 * @author shuang.kou
 * @createTime 2020年05月10日 07:25:00
 */
@RpcServiceScan(value = "org.liteRpc.impl")
public class SocketServerMain {
    public static void main(String[] args) {
        SocketRpcServer socketRpcServer = SocketRpcServer.getSocketRpcServer();
        socketRpcServer.start();
    }
}
