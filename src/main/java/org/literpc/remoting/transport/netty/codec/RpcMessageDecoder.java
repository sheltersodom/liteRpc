package org.literpc.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.literpc.enums.SerializationTypeEnum;
import org.literpc.extension.ExtensionLoader;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.messagedao.RpcMessage;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.messagedao.RpcResponse;
import org.literpc.serialize.Serializer;
import org.literpc.serialize.support.KryoSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * @autor sheltersodom
 * @create 2021-04-19-22:18
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outList) throws Exception {
        checkMagicNumber(in);
        checkVersion(in);
        int bodyLength = in.readInt();
        byte messageType = in.readByte();
        byte codec = in.readByte();
        int sequenceId = in.readInt();
        //skip the filled zero
        in.readByte();
        RpcMessage rpcMessage = RpcMessage.builder().codec(codec).
                messageType(messageType).sequenceId(sequenceId).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            outList.add(rpcMessage);
            return;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            outList.add(rpcMessage);
            return;
        }
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            //deserialize the object
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest rpcRequest = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(rpcRequest);
            } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                RpcResponse rpcRequest = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(rpcRequest);
            }
        }
        outList.add(rpcMessage);
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
