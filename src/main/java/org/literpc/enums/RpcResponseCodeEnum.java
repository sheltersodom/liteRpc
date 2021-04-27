package org.literpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @autor sheltersodom
 * @create 2021-04-18-21:14
 */
@AllArgsConstructor
@Getter
public enum RpcResponseCodeEnum {
    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");

    private final int code;
    private final String message;
}
