package com.stonecj.utils.redis4search.config;

import com.stonecj.utils.redis4search.common.SplitType;
import lombok.Data;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 描述
 */
@Data
public class SearchConfig {

    /**
     * redis key 命名空间，后缀
     */
    private String nameSpace;

    /**
     * 是否对检索内容编码
     * <p>
     * 被检索内容，会被分词。再作为redis的key使用
     * 防止中文问题
     */
    private Boolean encode = true;

    private String charset = "utf-8";

    /**
     * 分词方式
     */
    private SplitType splitType = SplitType.PerChar;

    /**
     * 分词工具，动态加载
     *
     * @TODO 加载方式考究
     */
//    private String splitUitl;

    /**
     * 支持按拼音搜索
     * 支持同音字搜索
     */
    private Boolean pinyinSupport = false;
}
