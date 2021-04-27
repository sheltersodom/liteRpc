package org.literpc.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.literpc.enums.SerializationTypeEnum;
import org.literpc.extension.ExtensionLoader;
import org.literpc.remoting.constants.RpcConstants;
import org.literpc.remoting.messagedao.RpcMessage;
import org.literpc.serialize.Serializer;
import org.literpc.serialize.support.KryoSerializer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 * +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 * |   magic   code        |version | body Length         | messageType| codec|     sequenceId      | zero |
 * +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 * |                                                                                                       |
 * |                                         body                                                          |
 * |                                                                                                       |
 * |                                        ... ...                                                        |
 * +-------------------------------------------------------------------------------------------------------+
 *
 * @autor sheltersodom
 * @create 2021-04-19-22:18
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcMessageEncoder extends MessageToMessageEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        //write magic number
        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        out.writeByte(RpcConstants.VERSION);

        byte messageType = rpcMessage.getMessageType();

        //write message length
        byte[] bodyBytes = null;
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE || messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            bodyBytes = serializer.serialize(rpcMessage.getData());
            out.writeInt(bodyBytes.length);
        }


        //write messageType
        out.writeByte(messageType);
        out.writeByte(rpcMessage.getCodec());

        //write sequenceId
        out.writeInt(ATOMIC_INTEGER.getAndIncrement());
        //full zero
        out.writeByte((byte) 0);

        if (bodyBytes != null) {
            out.writeBytes(bodyBytes);
        }

        outList.add(out);

    }

    public void encode(ByteBuf buf, RpcMessage rpcMessage, List<Object> outList) {
        //write magic number
        buf.writeBytes(RpcConstants.MAGIC_NUMBER);
        buf.writeByte(RpcConstants.VERSION);

        byte messageType = rpcMessage.getMessageType();

        //write message length
        byte[] bodyBytes = null;
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE || messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            bodyBytes = serializer.serialize(rpcMessage.getData());
            buf.writeInt(bodyBytes.length);
        }


        //write messageType
        buf.writeByte(messageType);
        buf.writeByte(rpcMessage.getCodec());

        //write sequenceId
        buf.writeInt(ATOMIC_INTEGER.getAndIncrement());
        //full zero
        buf.writeByte((byte) 0);

        if (bodyBytes != null) {
            buf.writeBytes(bodyBytes);
        }

        outList.add(buf);

    }
}
