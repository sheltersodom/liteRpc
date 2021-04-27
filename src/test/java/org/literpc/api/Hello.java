package org.literpc.api;

import lombok.*;

import java.io.Serializable;

/**
 * @autor sheltersodom
 * @create 2021-04-23-21:56
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
