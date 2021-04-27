package org.literpc.serialize;

import org.literpc.extension.SPI;

/**
 * @autor sheltersodom
 * @create 2021-04-19-22:31
 */
@SPI
public interface Serializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
