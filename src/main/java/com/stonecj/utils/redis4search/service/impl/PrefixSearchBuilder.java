package com.stonecj.utils.redis4search.service.impl;

import com.stonecj.utils.redis4search.common.ApplicationContext;
import com.stonecj.utils.redis4search.config.SearchConfig;
import org.springframework.beans.BeanUtils;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 描述
 */
public class PrefixSearchBuilder {
    private String nameSpace;

    public static PrefixSearchBuilder start() {
        PrefixSearchBuilder builder = new PrefixSearchBuilder();

        SearchConfig searchConfig = ApplicationContext.getBean(SearchConfig.class);
        BeanUtils.copyProperties(searchConfig, builder);

        return builder;
    }

    public PrefixSearchBuilder setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return this;
    }

    public PrefixSearch build() {
        return new PrefixSearch(nameSpace);
    }
}
