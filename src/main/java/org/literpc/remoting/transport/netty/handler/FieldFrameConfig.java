package org.literpc.remoting.transport.netty.handler;

import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @autor sheltersodom
 * @create 2021-04-21-13:57
 */
@Data
public class FieldFrameConfig {
    /**
     * default parameters
     */
    private static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    private static final int LENGTH_FIELD_OFFSET = 5;
    private static final int LENGTH_ADJUSTMENT = 7;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    /**
     * Configurable parameters
     */
    private int maxFrameLength = MAX_FRAME_LENGTH;
    private int lengthFieldOffset = LENGTH_FIELD_OFFSET;
    private int lengthFieldLength = LENGTH_FIELD_LENGTH;
    private int lengthAdjustment = LENGTH_ADJUSTMENT;
    private int initialBytesToStrip = INITIAL_BYTES_TO_STRIP;
}
