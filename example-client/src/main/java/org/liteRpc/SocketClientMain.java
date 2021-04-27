package org.liteRpc;



import org.literpc.api.Hello;
import org.literpc.api.HelloService;
import org.literpc.proxy.Stub;

/**
 * @author shuang.kou
 * @createTime 2020年05月10日 07:25:00
 */
public class SocketClientMain {
    public static void main(String[] args) throws InterruptedException {
        HelloService service = Stub.getProxy("1", "1", HelloService.class);
        System.out.println(service.hello(new Hello("111", "222")));
    }
}
