package org.literpc.remoting.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.proxy.RpcClientProxy;
import org.literpc.proxy.Stub;
import org.literpc.remoting.transport.AbstractRpcServer;
import org.literpc.remoting.transport.netty.client.handler.ClientHeartbeatHandler;
import org.literpc.remoting.transport.netty.client.handler.RpcResponseHandlerAdapter;
import org.literpc.remoting.transport.netty.codec.RpcMessageDecoder;
import org.literpc.remoting.transport.netty.codec.RpcMessageEncoder;
import org.literpc.remoting.transport.netty.handler.FieldFrameConfig;
import org.literpc.remoting.transport.netty.handler.FixedFrameHandler;
import org.literpc.remoting.transport.netty.server.handler.RpcRequestHandlerAdapter;
import org.literpc.remoting.transport.netty.server.handler.ServerHeartbeatHandler;
import org.literpc.remoting.transport.netty.server.support.DefaultServiceProvider;
import org.literpc.utils.RunTimeUtils;
import sun.rmi.runtime.RuntimeUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @autor sheltersodom
 * @create 2021-04-19-21:29
 */
@Slf4j
public class NettyRpcServer extends AbstractRpcServer {
    public static final int PORT = 9988;
    private final ServiceProvider serviceProvider = SingletonFactory.getSingletonFactory().getInstance(DefaultServiceProvider.class);
    private static NettyRpcServer nettyRpcServer;

    private NettyRpcServer() {
        scanServices();
    }

    public static NettyRpcServer getNettyRpcServer() {
        if (nettyRpcServer == null) {
            synchronized (NettyRpcServer.class) {
                if (nettyRpcServer == null) {
                    nettyRpcServer = new NettyRpcServer();
                }
            }
        }
        return nettyRpcServer;
    }

    public void registerService(Object service, String rpcServiceName) {
        serviceProvider.publishService(service, rpcServiceName);
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventLoopGroup serviceHandlerGroup = new DefaultEventLoopGroup(RunTimeUtils.cpus() * 2);
        LoggingHandler LOGGING_HANDLER = SingletonFactory.getSingletonFactory().getInstance(LoggingHandler.class);
        RpcMessageDecoder DECODER_HANDLER = SingletonFactory.getSingletonFactory().getInstance(RpcMessageDecoder.class);
        RpcMessageEncoder ENCODER_HANDLER = SingletonFactory.getSingletonFactory().getInstance(RpcMessageEncoder.class);
        ServerHeartbeatHandler HEARTBEAT_HANDLER = SingletonFactory.getSingletonFactory().getInstance(ServerHeartbeatHandler.class);
        RpcRequestHandlerAdapter REQUEST_HANDLER = SingletonFactory.getSingletonFactory().getInstance(RpcRequestHandlerAdapter.class);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //???????????? Nagle ????????????????????????????????????????????????????????????????????????????????????
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //????????????????????????????????????????????????????????????????????????????????????,????????????????????????????????????????????????????????????????????????????????????????????????
                    .childOption(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new FixedFrameHandler(new FieldFrameConfig()));
                            pipeline.addLast(LOGGING_HANDLER);
                            pipeline.addLast(DECODER_HANDLER);
                            pipeline.addLast(ENCODER_HANDLER);
                            pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                            pipeline.addLast(HEARTBEAT_HANDLER);
                            pipeline.addLast(serviceHandlerGroup, REQUEST_HANDLER);
                        }
                    });
            //Bind the port and wait for the successful binding
            ChannelFuture future = bootstrap.bind(PORT).sync();
            //Register listener monitoring server port closed
            future.channel().closeFuture().addListener((ChannelFutureListener) future1 -> {
                log.warn("shutdown bossGroup and workerGroup");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                serviceHandlerGroup.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        }

    }
}
