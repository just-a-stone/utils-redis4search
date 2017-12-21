package com.stonecj.utils.redis4search.service.impl;

import com.stonecj.utils.redis4search.common.ApplicationContext;
import com.stonecj.utils.redis4search.common.SplitType;
import com.stonecj.utils.redis4search.config.SearchConfig;
import org.springframework.beans.BeanUtils;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 描述
 */
public class ParticipleSearchBuilder {
    private String nameSpace = null;
    private String charset = "utf-8";
    private boolean encode = true;
    private SplitType splitType = SplitType.PerChar;

    public static ParticipleSearchBuilder start() {
        ParticipleSearchBuilder builder = new ParticipleSearchBuilder();

        SearchConfig searchConfig = ApplicationContext.getBean(SearchConfig.class);
        BeanUtils.copyProperties(searchConfig, builder);

        return builder;
    }

    public ParticipleSearchBuilder setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return this;
    }

    public ParticipleSearchBuilder setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public ParticipleSearchBuilder setEncode(boolean encode) {
        this.encode = encode;
        return this;
    }

    public ParticipleSearchBuilder setSplitType(SplitType splitType) {
        this.splitType = splitType;
        return this;
    }

    public ParticipleSearch build() {
        return new ParticipleSearch(nameSpace, charset, encode, splitType);
    }
}
