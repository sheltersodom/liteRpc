package org.literpc.serialize.kyro;

import org.junit.Assert;
import org.junit.Test;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.serialize.support.KryoSerializer;

import java.util.UUID;

/**
 * @autor sheltersodom
 * @create 2021-04-23-21:51
 */
public class KryoSerializerTest {
    @Test
    public void KryoSerializerTest(){
        RpcRequest rpcRequest=RpcRequest.builder().methodName("hello")
                .parameters(new Object[]{"sayhelooloo", "sayhelooloosayhelooloo"})
                .interfaceName("org.literpc.api.HelloService")
                .paramTypes(new Class<?>[]{String.class,String.class})
                .requestId(UUID.randomUUID().toString())
                .version("version1")
                .build();
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] bytes = kryoSerializer.serialize(rpcRequest);
        RpcRequest request = kryoSerializer.deserialize(bytes, RpcRequest.class);
        Assert.assertEquals(request.getVersion(),rpcRequest.getVersion());
        Assert.assertEquals(request.getRequestId(),rpcRequest.getRequestId());
    }

}
