package org.literpc.remoting.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.enums.SerializationTypeEnum;
import org.literpc.extension.ExtensionLoader;
import org.literpc.register.ServiceDiscovery;
import org.literpc.register.zk.zkServiceDiscovery;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.messagedao.RpcMessage;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.messagedao.RpcResponse;
import org.literpc.remoting.transport.Closeable;
import org.literpc.remoting.transport.RpcRequestTransport;
import org.literpc.remoting.transport.netty.client.handler.ClientHeartbeatHandler;
import org.literpc.remoting.transport.netty.client.handler.RpcResponseHandlerAdapter;
import org.literpc.remoting.transport.netty.client.support.UnprocessedCache;
import org.literpc.remoting.transport.netty.client.support.ChannelProvider;
import org.literpc.remoting.transport.netty.codec.RpcMessageDecoder;
import org.literpc.remoting.transport.netty.codec.RpcMessageEncoder;
import org.literpc.remoting.transport.netty.handler.FieldFrameConfig;
import org.literpc.remoting.transport.netty.handler.FixedFrameHandler;
import org.literpc.remoting.transport.netty.server.ServiceProvider;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @autor sheltersodom
 * @create 2021-04-18-22:02
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport, Closeable {
    private final ServiceDiscovery serviceDiscovery;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final ChannelProvider provider;
    private final UnprocessedCache cache;

    private final LoggingHandler LOGGING_HANDLER = SingletonFactory.getSingletonFactory().getInstance(LoggingHandler.class);
    private final RpcMessageDecoder DECODER_HANDLER = SingletonFactory.getSingletonFactory().getInstance(RpcMessageDecoder.class);
    private final RpcMessageEncoder ENCODER_HANDLER = SingletonFactory.getSingletonFactory().getInstance(RpcMessageEncoder.class);
    private final ClientHeartbeatHandler HEARTBEAT_HANDLER = SingletonFactory.getSingletonFactory().getInstance(ClientHeartbeatHandler.class);
    private final RpcResponseHandlerAdapter RESPONSE_HANDLER = SingletonFactory.getSingletonFactory().getInstance(RpcResponseHandlerAdapter.class);

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().
                group(eventLoopGroup).
                channel(NioSocketChannel.class).
                handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new FixedFrameHandler(new FieldFrameConfig()));
                        pipeline.addLast(LOGGING_HANDLER);
                        pipeline.addLast(DECODER_HANDLER);
                        pipeline.addLast(ENCODER_HANDLER);
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(HEARTBEAT_HANDLER);
                        pipeline.addLast(RESPONSE_HANDLER);
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        provider = SingletonFactory.getSingletonFactory().getInstance(ChannelProvider.class);
        cache = SingletonFactory.getSingletonFactory().getInstance(UnprocessedCache.class);
    }

    public Channel getChannel(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        Channel channel = provider.get(rpcServiceName);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            provider.put(rpcServiceName, channel);
        }
        return channel;
    }

    private Channel doConnect(InetSocketAddress inetSocketAddress) {
        Channel channel = null;
        try {
            channel = bootstrap.connect(inetSocketAddress).sync().channel();
            log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
        return channel;
    }

    @SneakyThrows
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // build rpc service name by rpcRequest
        String rpcServiceName = rpcRequest.getRpcPropertiesName();
        //get server address related channel
        InetAddress address = InetAddress.getLocalHost();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(address, rpcServiceName);
        //get server address related channel
        Channel channel = getChannel(rpcServiceName, inetSocketAddress);

        // build return value
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();

        if (channel.isActive()) {
            //put unprocessed request
            cache.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().
                    data(rpcRequest).codec(SerializationTypeEnum.JAVA.getCode()).
                    messageType(RpcConstants.REQUEST_TYPE).build();
            //judge the message is or not successful sending
            channel.writeAndFlush(rpcMessage).addListener((ChannelFuture future) -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        }

        return resultFuture;
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
