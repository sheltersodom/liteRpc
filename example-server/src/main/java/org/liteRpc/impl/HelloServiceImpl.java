package org.liteRpc.impl;

import org.literpc.anntation.RpcService;
import org.literpc.api.Hello;
import org.literpc.api.HelloService;

/**
 * @author shuang.kou
 * @createTime 2020年05月10日 07:52:00
 */
@RpcService
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        String result = "Hello description is " + hello.getDescription();
        return result;
    }
}
