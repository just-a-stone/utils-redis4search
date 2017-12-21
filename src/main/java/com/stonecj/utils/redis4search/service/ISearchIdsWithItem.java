package com.stonecj.utils.redis4search.service;

import java.io.IOException;
import java.util.Set;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 描述
 */
public interface ISearchIdsWithItem {

    /**
     * 根据item搜索相关id集合
     *
     * @param item 搜索词
     * @return 搜索到对象的id集合
     */
    Set<String> searchIdsWithItem(String item) throws IOException;

    Set<String> searchIdsWithItem(String item, Integer count) throws IOException;
}
