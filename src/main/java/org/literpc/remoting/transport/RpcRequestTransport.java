package org.literpc.remoting.transport;

import org.literpc.extension.SPI;
import org.literpc.remoting.messagedao.RpcRequest;

/**
 *
 * send RpcRequest
 * @autor sheltersodom
 * @create 2021-04-18-22:00
 */
@SPI
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
