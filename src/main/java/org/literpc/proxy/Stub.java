package org.literpc.proxy;

/**
 * @autor sheltersodom
 * @create 2021-04-25-20:50
 */
public class Stub {
    private static RpcClientProxy proxy;

    public static <T> T getProxy(String group, String version, Class<T> clazz) {
        if (proxy == null) {
            synchronized (Stub.class) {
                if (proxy == null) {
                    proxy = new RpcClientProxy(group, version);
                }
            }
        }
        return proxy.getProxy(clazz);
    }
}
