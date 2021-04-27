package org.literpc.remoting.transport.netty.client.support;

import org.literpc.remoting.messagedao.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor sheltersodom
 * @create 2021-04-19-20:39
 */
public class UnprocessedCache {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURE =
            new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURE.put(requestId, future);
    }

    public void complete(RpcResponse<Object> response){
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURE.remove(response.getRequestId());
        if (future!=null){
            future.complete(response);
        }else{
            throw new IllegalStateException();
        }
    }
}
