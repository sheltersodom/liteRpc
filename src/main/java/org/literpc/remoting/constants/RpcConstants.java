package org.literpc.remoting.constants;

/**
 * @autor sheltersodom
 * @create 2021-04-18-21:25
 */
public abstract class RpcConstants {

    //Magic Number
    public static final byte[] MAGIC_NUMBER = {(byte) 'b', (byte) 'a', (byte) 'b', (byte) 'e' };
    //version information
    public static final byte VERSION = 1;

    //messageType
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    public static final String PING = "ping";
    public static final String PONG = "pong";


    public static final int HEAD_LENGTH = 16;

}
