package org.literpc.remoting.transport.socket;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.literpc.exception.RpcException;
import org.literpc.extension.ExtensionLoader;
import org.literpc.register.ServiceDiscovery;
import org.literpc.register.zk.zkServiceDiscovery;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.transport.RpcRequestTransport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @autor sheltersodom
 * @create 2021-04-20-20:51
 */
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @SneakyThrows
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcPropertiesName();
        InetAddress address = InetAddress.getLocalHost();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(address, rpcServiceName);
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(inetSocketAddress);
            //send data to server
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            //receive data from server
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("Failed to call service", e);
        } finally {
            try {
                assert objectInputStream != null;
                objectInputStream.close();
                assert objectOutputStream != null;
                objectOutputStream.close();
                assert socket != null;
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
