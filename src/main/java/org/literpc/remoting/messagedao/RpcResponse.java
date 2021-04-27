package org.literpc.remoting.messagedao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.literpc.enums.RpcResponseCodeEnum;

import java.io.Serializable;

/**
 * @autor sheltersodom
 * @create 2021-04-18-20:03
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 6860715823019852021L;
    private String requestId;
    private Integer code;
    private String message;
    /**
     * response body
     */
    private T data;

    /**
     * if the return is success,then will get RpcResponse<T> entity via this method
     *
     * @param data
     * @param requestId
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.FAIL.getMessage());
        response.setRequestId(requestId);
        if (data != null) {
            response.setData(data);
        }
        return response;
    }

    /**
     * if the return throw exception,then will get RpcResponse<T> entity via this method
     *
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum, T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        response.setData(data);
        return response;
    }
}
