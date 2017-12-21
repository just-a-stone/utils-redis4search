package com.stonecj.utils.redis4search.springboot;

import com.stonecj.utils.redis4search.config.SearchConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc
 */
@Slf4j
@Data
@ConfigurationProperties("redis4search")
public class Redis4SearchProperties {

    private SearchConfig searchConfig;

}
