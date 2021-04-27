package org.literpc.ExtensionLoader;

import org.junit.Assert;
import org.junit.Test;
import org.literpc.extension.ExtensionLoader;
import org.literpc.loadbalance.LoadBalance;
import org.literpc.loadbalance.support.ConsistentHashLoadBalance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @autor sheltersodom
 * @create 2021-04-24-21:03
 */
public class ExtensionLoaderTest {
    @Test
    public void getLoaderTest() throws FileNotFoundException {
        Assert.assertTrue(ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance") instanceof ConsistentHashLoadBalance);
    }
}
