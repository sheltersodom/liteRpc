package org.literpc.remoting.transport.netty.client.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.literpc.enums.SerializationTypeEnum;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.messagedao.RpcMessage;
import org.literpc.remoting.transport.netty.client.NettyRpcClient;

/**
 * @autor sheltersodom
 * @create 2021-04-21-11:18
 */
@Slf4j
public class ClientHeartbeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                RpcMessage rpcMessage = RpcMessage.builder().
                        codec(SerializationTypeEnum.JAVA.getCode()).
                        messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE).
                        data(RpcConstants.PING).build();
                ctx.writeAndFlush(rpcMessage);
            }
        }
    }
}
