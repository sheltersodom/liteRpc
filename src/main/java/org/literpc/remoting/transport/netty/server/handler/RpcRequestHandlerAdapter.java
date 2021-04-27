package org.literpc.remoting.transport.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.enums.RpcConfigEnum;
import org.literpc.enums.RpcResponseCodeEnum;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.messagedao.RpcMessage;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.messagedao.RpcResponse;
import org.literpc.remoting.transport.handler.RpcRequestHandler;

/**
 * @autor sheltersodom
 * @create 2021-04-21-10:17
 */
@Slf4j
public class RpcRequestHandlerAdapter extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    public RpcRequestHandlerAdapter() {
        this.rpcRequestHandler = SingletonFactory.getSingletonFactory().getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcMessage) {
            log.info("server receive msg: [{}] ", msg);
            RpcMessage message = (RpcMessage) msg;
            byte messageType = message.getMessageType();
            RpcMessage rpcMessage = new RpcMessage();
            byte codec = message.getCodec();
            rpcMessage.setCodec(codec);
            if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                rpcMessage.setData(RpcConstants.PONG);
            } else if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest rpcRequest = (RpcRequest) message.getData();
                Object result = rpcRequestHandler.handle(rpcRequest);
                log.info(String.format("server get result: %s", result.toString()));
                rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    if (result instanceof Throwable) {
                        Throwable e = (Throwable) result;
                        RpcResponse<Throwable> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL, e);
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    }
                } else {
                    RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                    rpcMessage.setData(rpcResponse);
                }

            }
            ctx.writeAndFlush(rpcMessage);
        }
    }
}
