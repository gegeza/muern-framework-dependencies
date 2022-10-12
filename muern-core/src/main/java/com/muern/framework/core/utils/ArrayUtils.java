package com.muern.framework.core.utils;

import java.lang.reflect.Array;

/**
 * @author gegeza
 * @date 2022/09/13
 */
public final class ArrayUtils {

    public static Object[] addAll(Object[] array1, Object[] array2) {
        if (array1 == null) {
            return array2.clone();
        } else if (array2 == null) {
            return array1.clone();
        }
        Object[] joinedArray = (Object[]) Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
}
