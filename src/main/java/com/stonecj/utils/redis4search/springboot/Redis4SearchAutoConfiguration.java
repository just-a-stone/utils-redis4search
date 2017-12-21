package com.stonecj.utils.redis4search.springboot;

import com.stonecj.utils.redis4search.common.ApplicationContext;
import com.stonecj.utils.redis4search.common.SpringBeanFactory;
import com.stonecj.utils.redis4search.config.SearchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 描述
 */
@Configuration
public class Redis4SearchAutoConfiguration {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 使用两个类，引入应用的applicationContext
     *
     * @return
     */
    @Bean
    public SpringBeanFactory springBeanFactory() {
        SpringBeanFactory springBeanFactory = new SpringBeanFactory();
        ApplicationContext.setpringBeanFactory(springBeanFactory);
        return springBeanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public SearchConfig searchConfig() {
        return new SearchConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public Redis4SearchProperties redis4SearchProperties(SearchConfig searchConfig) {
        Redis4SearchProperties redis4SearchProperties = new Redis4SearchProperties();
        redis4SearchProperties.setSearchConfig(searchConfig);

        return redis4SearchProperties;
    }

}
