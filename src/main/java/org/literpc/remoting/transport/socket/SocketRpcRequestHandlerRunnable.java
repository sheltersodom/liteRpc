package org.literpc.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.literpc.Factory.SingletonFactory;
import org.literpc.remoting.messagedao.RpcRequest;
import org.literpc.remoting.messagedao.RpcResponse;
import org.literpc.remoting.transport.handler.RpcRequestHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @autor sheltersodom
 * @create 2021-04-20-21:40
 */
@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable {
    private final RpcRequestHandler rpcRequestHandler;
    private final Socket socket;

    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getSingletonFactory().getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());

        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                assert objectInputStream != null;
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert objectOutputStream != null;
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
