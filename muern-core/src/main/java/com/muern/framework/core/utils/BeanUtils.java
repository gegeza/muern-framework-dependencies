package com.muern.framework.core.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gegeza
 * @date 2022/08/30
 */
public final class BeanUtils extends org.springframework.beans.BeanUtils {
    /** 对象拷贝 忽略null */
    public static void copyPropertiesIgnoreNull(Object source, Object target) throws BeansException {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        Arrays.stream(pds).filter(pd -> src.getPropertyValue(pd.getName()) == null).forEach(pd -> emptyNames.add(pd.getName()));
        copyProperties(source, target, emptyNames.toArray(new String[emptyNames.size()]));
    }
}
