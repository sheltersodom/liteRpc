package org.literpc.remoting.messagedao;

import lombok.*;

import java.io.Serializable;

/**
 * @autor sheltersodom
 * @create 2021-04-18-18:14
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1725448094784422017L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    /**
     * service version
     */
    private String version;//?

    public String getRpcPropertiesName() {
        return this.getInterfaceName() + this.getVersion();
    }
}
