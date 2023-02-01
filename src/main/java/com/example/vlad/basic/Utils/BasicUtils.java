package com.example.vlad.basic.Utils;

/**
 * @author vshlimovich
 * Some very basic utils
 */
public final class BasicUtils {
    /**
     * @param argDepth the depth of the call stack.  2 means the immediate calling
     * @return the info about the calling method
     */
    public static final String getEnclosingMethodInfo(final int argDepth) {
        return
            Thread.currentThread().getStackTrace()[argDepth].getClassName() + "." +
            Thread.currentThread().getStackTrace()[argDepth].getMethodName() + "(" +
            Thread.currentThread().getStackTrace()[argDepth].getLineNumber() + ")";
    }
    /**
     * @return the info about the calling method
     */
    public static final String getEnclosingMethodInfo() {
        return getEnclosingMethodInfo(3);
    }
}
