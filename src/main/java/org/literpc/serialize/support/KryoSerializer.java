package org.literpc.serialize.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.literpc.exception.SerializeException;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.messagedao.RpcResponse;
import org.literpc.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @autor sheltersodom
 * @create 2021-04-19-22:37
 */
public class KryoSerializer implements Serializer {

    private ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        Output output = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        } finally {
            try {
                assert byteArrayOutputStream != null;
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert output != null;
            output.close();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {

        ByteArrayInputStream byteArrayInputStream = null;
        Input input = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return t;
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        } finally {
            try {
                assert byteArrayInputStream != null;
                byteArrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert input != null;
            input.close();
        }
    }
}
