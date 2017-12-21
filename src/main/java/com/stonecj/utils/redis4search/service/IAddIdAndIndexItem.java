package com.stonecj.utils.redis4search.service;

import java.io.IOException;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 描述
 */
public interface IAddIdAndIndexItem {

    /**
     * 添加索引对象
     *
     * @param id       被索引对象id
     * @param searched 被索引对象-搜索内容
     */
    void addIdAndIndexItem(String id, String searched) throws IOException;
}
