package org.literpc.remoting.transport.netty.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.messagedao.RpcMessage;
import org.literpc.remoting.messagedao.RpcResponse;
import org.literpc.remoting.transport.netty.client.support.UnprocessedCache;
import org.literpc.remoting.transport.netty.server.support.DefaultServiceProvider;

/**
 * @autor sheltersodom
 * @create 2021-04-21-11:03
 */
@Slf4j
public class RpcResponseHandlerAdapter extends ChannelInboundHandlerAdapter {
    private final UnprocessedCache cache;

    public RpcResponseHandlerAdapter() {
        this.cache = SingletonFactory.getSingletonFactory().getInstance(UnprocessedCache.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof RpcMessage){
            RpcMessage rpcMessage = (RpcMessage) msg;
            byte messageType = rpcMessage.getMessageType();
            if(messageType== RpcConstants.HEARTBEAT_RESPONSE_TYPE){
                log.info("heart [{}]", rpcMessage.getData());
            }else if(messageType==RpcConstants.RESPONSE_TYPE){
                RpcResponse<Object> response = (RpcResponse<Object>) rpcMessage.getData();
                cache.complete(response);
            }
        }
    }
}
