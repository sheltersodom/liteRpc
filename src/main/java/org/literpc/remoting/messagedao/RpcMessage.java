package org.literpc.remoting.messagedao;

import lombok.*;

import java.io.Serializable;

/**
 * @autor sheltersodom
 * @create 2021-04-18-21:18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class RpcMessage implements Serializable {
    //rpc message type
    private byte messageType;
    //serialization type
    private byte codec;
    //sequence Id
    private int sequenceId;
    //request data
    private Object data;
}
