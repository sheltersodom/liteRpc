package org.literpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @autor sheltersodom
 * @create 2021-04-19-20:58
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {
    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    JAVA((byte) 0x03, "java");

    private final byte code;
    private final String name;

    public static String getName(int code) {
        for (SerializationTypeEnum value : SerializationTypeEnum.values()) {
            if (value.getCode() == code) {
                return value.name;
            }
        }
        return null;
    }
}
