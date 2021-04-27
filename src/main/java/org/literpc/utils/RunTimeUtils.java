package org.literpc.utils;

/**
 * @autor sheltersodom
 * @create 2021-04-19-21:39
 */
public abstract class RunTimeUtils {
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
