package org.literpc.loadbalance.support;

import org.literpc.loadbalance.AbstractLoadBalance;
import org.literpc.loadbalance.LoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor sheltersodom
 * @create 2021-04-19-17:23
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private Map<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();



    public String doSelect(List<String> serviceAddresses, String InetAddressRpcName) {
        int identityHashCode = System.identityHashCode(serviceAddresses);
        ConsistentHashSelector selector = selectors.get(InetAddressRpcName);

        if (Objects.isNull(selector) || selector.identityHashCode != identityHashCode) {
            selector = new ConsistentHashSelector(serviceAddresses, 160, identityHashCode);
            selectors.put(InetAddressRpcName, selector);
        }
        return selector.select(InetAddressRpcName);
    }

    private class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;
        private final int identityHashCode;

        private ConsistentHashSelector(List<String> serviceAddresses, int replicaNUmber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (String serviceAddress : serviceAddresses) {
                for (int i = 0; i < replicaNUmber / 4; i++) {
                    byte[] digest = md5(serviceAddress + i);
                    for (int h = 0; h < 4; h++) {
                        long hash = hash(digest, h);
                        virtualInvokers.put(hash, serviceAddress);
                    }
                }
            }
        }

        private long hash(byte[] digest, int idx) {
            //number可以是，0,1,2,3 long 类型64 bit位
            //最后0xFFFFFFFFL;保证4字节位表示数值。相当于Ingter型数值。所以hash环的值域是[0,Integer.max_value]
            //每次取digest4个字节（|操作），组成4字节的数值。
            //当number 为 0,1,2,3时，分别对应digest第
            // 1，2,3,4;
            // 5,6,7，8；
            // 9,10,11,12;
            // 13,14,15,16;字节
            //4批
            return (//digest的第4(number 为 0时),8(number 为 1),12(number 为 2),16(number 为 3)字节，&0xFF后，左移24位
                    (long) (digest[3 + idx * 4] & 0xFF) << 24 |
                    //digest的第3,7,11,15字节，&0xFF后，左移16位
                    (long) (digest[2 + idx * 4] & 0xFF) << 16 |
                     //digest的第2,6,10,14字节，&0xFF后，左移8位
                    (long) (digest[1 + idx * 4] & 0xFF) << 8 |
                     //digest的第1,5,9,13字节，&0xFF
                    (long) (digest[idx * 4] & 0xFF)) & 0xFFFFFFFFL;
        }

        private byte[] md5(String key) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return md.digest();
        }


        public String select(String InetAddressRpcName) {
            byte[] bytes = md5(InetAddressRpcName);
            long hash = hash(bytes, 0);
            Map.Entry<Long, String> entry = virtualInvokers.ceilingEntry(hash);
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }
}
