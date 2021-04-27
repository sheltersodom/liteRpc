package org.literpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @autor sheltersodom
 * @create 2021-04-19-17:30
 */
@AllArgsConstructor
@Getter
public enum RpcExceptionMessageEnum {
    CLIENT_CONNECT_SERVER_FAILURE("The client failed to connect to the server"),
    SERVICE_INVOCATION_FAILURE("Service call failed"),
    SERVICE_CAN_NOT_BE_FOUND("The specified service was not found"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("The registered service does not implement any interfaces"),
    REQUEST_NOT_MATCH_RESPONSE("Return result error! The corresponding requests and returns do not match");

    private final String message;
}
