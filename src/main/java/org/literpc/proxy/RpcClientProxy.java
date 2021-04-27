package org.literpc.proxy;

import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.enums.RpcConfigEnum;
import org.literpc.enums.RpcExceptionMessageEnum;
import org.literpc.enums.RpcResponseCodeEnum;
import org.literpc.exception.RpcException;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.messagedao.RpcResponse;
import org.literpc.remoting.transport.RpcRequestTransport;
import org.literpc.remoting.transport.netty.client.NettyRpcClient;
import org.literpc.remoting.transport.socket.SocketRpcClient;
import org.literpc.utils.PropertiesFileUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @autor sheltersodom
 * @create 2021-04-20-22:13
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    private static final String INTERFACE_NAME = "interface";
    private final String RPC_REQUEST_CLIENT = "rpcRequestTransport";
    private final RpcRequestTransport rpcRequestTransport;
    private final String group;
    private final String version;

    public RpcClientProxy(String group, String version) {
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.CLIENT_PROXY.getPropertyValue());
        String className = properties.getProperty(RPC_REQUEST_CLIENT);
        Class<?> clazz = null;
        try {
            clazz = this.getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.rpcRequestTransport = (RpcRequestTransport) SingletonFactory.getSingletonFactory().getInstance(clazz);
        this.group = group == null ? "" : group;
        this.version = version == null ? "" : version;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().
                methodName(method.getName()).parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .version(version).build();
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture =
                    (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        } else if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }
        check(rpcResponse, rpcRequest);
        //TODO:处理得到结果
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcExceptionMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcExceptionMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcExceptionMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
