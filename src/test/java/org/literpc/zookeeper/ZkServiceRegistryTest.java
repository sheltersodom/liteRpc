package org.literpc.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.literpc.register.ServiceDiscovery;
import org.literpc.register.ServiceRegistry;
import org.literpc.register.zk.util.CuratorUtils;
import org.literpc.register.zk.zkServiceDiscovery;
import org.literpc.register.zk.zkServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @autor sheltersodom
 * @create 2021-04-24-20:43
 */
public class ZkServiceRegistryTest {
    @Test
    public void should_register_service_successful_and_lookup_service_by_service_name() throws UnknownHostException {
        ServiceRegistry zkServiceRegistry = new zkServiceRegistry();
        InetSocketAddress givenInetSocketAddress = new InetSocketAddress("127.0.0.1", 9333);
        zkServiceRegistry.registerService("org.literpc.api.HelloService", givenInetSocketAddress);
        ServiceDiscovery zkServiceDiscovery = new zkServiceDiscovery();
        InetSocketAddress acquiredInetSocketAddress = zkServiceDiscovery.lookupService(InetAddress.getLocalHost(), "org.literpc.api.HelloService");
        Assert.assertEquals(givenInetSocketAddress.toString(), acquiredInetSocketAddress.toString());
    }

    @After
    public void AfterDelete() throws Exception {
        CuratorUtils.getZkClient().delete().deletingChildrenIfNeeded().forPath("/liteRpc");
    }

    @Test
    public void testDelete() throws Exception {
        CuratorUtils.getZkClient().delete().deletingChildrenIfNeeded().forPath("/liteRpc");
    }
}
