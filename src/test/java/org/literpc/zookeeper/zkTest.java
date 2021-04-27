package org.literpc.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.literpc.enums.RpcConfigEnum;
import org.literpc.utils.PropertiesFileUtil;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @autor sheltersodom
 * @create 2021-04-23-22:10
 */
public class zkTest {

    CuratorFramework zkClient;

    @Before
    public void testConnect() {
        String zookeeperAddress = "192.168.79.128:2181";

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPersistentNode() throws Exception {
        Assert.assertTrue(zkClient.checkExists().forPath("/liteRpc/org.literpc.api.HelloService/127.0.0.1:9988") == null);
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/liteRpc/org.literpc.api.HelloService/127.0.0.1:9988");
        Assert.assertTrue(zkClient.checkExists().forPath("/liteRpc/org.literpc.api.HelloService/127.0.0.1:9988") != null);
    }

    @Test
    public void testGetChild() throws Exception {
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/liteRpc/org.literpc.api.HelloService/127.0.0.1:9988");
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/liteRpc/org.literpc.api.HelloService/127.0.1.1:9988");
        List<String> rpcServiceChildrenNodes = zkClient.getChildren().forPath("/liteRpc/org.literpc.api.HelloService");
        Assert.assertTrue(rpcServiceChildrenNodes.size() == 2);
        Assert.assertTrue(rpcServiceChildrenNodes.get(0).equals("127.0.0.1:9988"));
        Assert.assertTrue(rpcServiceChildrenNodes.get(1).equals("127.0.1.1:9988"));
    }

    @Test
    public void testWatcher() throws Exception {
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/liteRpc/org.literpc.api.HelloService/127.0.0.1:9988");
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/liteRpc/org.literpc.api.HelloService/127.0.1.1:9988");
        String servicePath = "/liteRpc/org.literpc.api.HelloService";
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        pathChildrenCache.getListenable().addListener((curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            Assert.assertTrue(serviceAddress.get(2).equals("127.0.1.2:9988"));
        });
        pathChildrenCache.start();

        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/liteRpc/org.literpc.api.HelloService/127.0.1.2:9988");

    }

    @After
    public void AfterDelete() throws Exception {
        zkClient.delete().deletingChildrenIfNeeded().forPath("/liteRpc");
    }
}
