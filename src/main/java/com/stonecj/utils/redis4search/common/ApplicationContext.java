package com.stonecj.utils.redis4search.common;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 描述
 */
public class ApplicationContext {

    private static SpringBeanFactory springBeanFactory;


    public static void setpringBeanFactory(SpringBeanFactory springBeanFactory) {
        ApplicationContext.springBeanFactory = springBeanFactory;
    }

    public static <T> T getBean(Class<T> tClass) {
        return springBeanFactory.getApplicationContext().getBean(tClass);
    }

}
