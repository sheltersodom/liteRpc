package org.literpc.exception;

import org.literpc.enums.RpcExceptionMessageEnum;

/**
 * @autor sheltersodom
 * @create 2021-04-19-17:28
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcExceptionMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcExceptionMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
