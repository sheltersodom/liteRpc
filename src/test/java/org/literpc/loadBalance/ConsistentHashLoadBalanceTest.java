package org.literpc.loadBalance;

import org.junit.Assert;
import org.junit.Test;
import org.literpc.extension.ExtensionLoader;
import org.literpc.loadbalance.LoadBalance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConsistentHashLoadBalanceTest {
    @Test
    public void TestConsistentHashLoadBalance() {
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
        List<String> serviceUrlList = new ArrayList<>(Arrays.asList("127.0.0.1:9997", "127.0.0.1:9998", "127.0.0.1:9999"));
        String userRpcServiceName = "org.literpc.api.HelloService";
        String userServiceAddress = loadBalance.selectServiceAddress(serviceUrlList, userRpcServiceName);
        Assert.assertEquals("127.0.0.1:9997",userServiceAddress);
    }
}