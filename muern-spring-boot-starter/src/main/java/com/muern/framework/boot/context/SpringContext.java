package com.muern.framework.boot.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author gegeza
 * @date 2022/10/11
 */
@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 通过bean的type从上下文中拿出该对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static String getActiveProfile() {
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        return activeProfiles.length == 0 ? "default" : activeProfiles[0];
    }
}
