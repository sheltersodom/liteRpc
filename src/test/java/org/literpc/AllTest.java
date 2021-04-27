package org.literpc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.literpc.ExtensionLoader.ExtensionLoaderTest;
import org.literpc.loadBalance.ConsistentHashLoadBalanceTest;
import org.literpc.serialize.kyro.KryoSerializerTest;
import org.literpc.zookeeper.ZkServiceRegistryTest;
import org.literpc.zookeeper.zkTest;

/**
 * @autor sheltersodom
 * @create 2021-04-24-22:40
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ExtensionLoaderTest.class,
        ConsistentHashLoadBalanceTest.class,
        KryoSerializerTest.class,
        ZkServiceRegistryTest.class,
        zkTest.class})
public class AllTest {
}
