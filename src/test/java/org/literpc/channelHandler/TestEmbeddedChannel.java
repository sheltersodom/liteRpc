package org.literpc.channelHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Before;
import org.junit.Test;
import org.literpc.Factory.SingletonFactory;
import org.literpc.enums.SerializationTypeEnum;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.messagedao.RpcMessage;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.transport.netty.codec.RpcMessageDecoder;
import org.literpc.remoting.transport.netty.codec.RpcMessageEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @autor sheltersodom
 * @create 2021-04-25-19:15
 */
public class TestEmbeddedChannel {
    RpcMessage rpcMessage;

    @Before
    public void Setup() {
        RpcRequest rpcRequest = RpcRequest.builder().methodName("hello")
                .parameters(new Object[]{"sayhelooloo", "sayhelooloosayhelooloo"})
                .interfaceName("org.literpc.api.HelloService")
                .paramTypes(new Class<?>[]{String.class, String.class})
                .requestId(UUID.randomUUID().toString())
                .version("version1")
                .build();
        rpcMessage = RpcMessage.builder().
                data(rpcRequest).codec(SerializationTypeEnum.KYRO.getCode()).
                messageType(RpcConstants.REQUEST_TYPE).build();
    }

    @Test
    public void channelCoderTest() {
        RpcMessageDecoder h1 = SingletonFactory.getSingletonFactory().getInstance(RpcMessageDecoder.class);
        RpcMessageEncoder h2 = SingletonFactory.getSingletonFactory().getInstance(RpcMessageEncoder.class);
        EmbeddedChannel channel = new EmbeddedChannel(h1, h2);

        channel.writeOutbound(rpcMessage);
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
        List<Object> list = new ArrayList<>();
        RpcMessageEncoder encoder = new RpcMessageEncoder();
        encoder.encode(buf, rpcMessage, list);
        channel.writeInbound(buf);
    }
}
