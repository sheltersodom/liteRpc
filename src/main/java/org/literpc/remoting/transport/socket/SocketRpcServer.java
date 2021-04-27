package org.literpc.remoting.transport.socket;

import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.remoting.transport.AbstractRpcServer;
import org.literpc.remoting.transport.netty.server.NettyRpcServer;
import org.literpc.remoting.transport.netty.server.ServiceProvider;
import org.literpc.remoting.transport.netty.server.support.DefaultServiceProvider;
import org.literpc.utils.ThreadPoolFactoryUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @autor sheltersodom
 * @create 2021-04-20-21:03
 */
@Slf4j
public class SocketRpcServer extends AbstractRpcServer {
    private final ServiceProvider serviceProvider;
    private final ExecutorService threadPool;

    private static SocketRpcServer socketRpcServer;

    private SocketRpcServer() {
        scanServices();
        this.serviceProvider = SingletonFactory.getSingletonFactory().getInstance(DefaultServiceProvider.class);
        this.threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolTfAbsent("socket-server-rpc-pool");
    }

    public static SocketRpcServer getSocketRpcServer() {
        if (socketRpcServer == null) {
            synchronized (NettyRpcServer.class) {
                if (socketRpcServer == null) {
                    socketRpcServer = new SocketRpcServer();
                }
            }
        }
        return socketRpcServer;
    }

    public void registerService(Object service, String rpcServiceName){
        serviceProvider.publishService(service,rpcServiceName);
    }

    public void start(){
        try {
            ServerSocket server=new ServerSocket();
            String host= InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, NettyRpcServer.PORT));

            while(true){
                Socket socket = server.accept();
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
        } catch (IOException e) {
            log.error("occur IOException:", e);
        } finally {
            threadPool.shutdown();
        }
    }
}
