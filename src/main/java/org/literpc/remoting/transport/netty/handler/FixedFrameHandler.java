package org.literpc.remoting.transport.netty.handler;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @autor sheltersodom
 * @create 2021-04-21-11:51
 */
public class FixedFrameHandler extends LengthFieldBasedFrameDecoder {
    public FixedFrameHandler(FieldFrameConfig fieldFrameConfig) {
        this(fieldFrameConfig.getMaxFrameLength(), fieldFrameConfig.getLengthFieldOffset(),
                fieldFrameConfig.getLengthFieldLength(), fieldFrameConfig.getLengthAdjustment(),
                fieldFrameConfig.getInitialBytesToStrip());
    }


    public FixedFrameHandler(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
