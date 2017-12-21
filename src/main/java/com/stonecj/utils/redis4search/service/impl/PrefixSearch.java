package com.stonecj.utils.redis4search.service.impl;

import com.stonecj.utils.redis4search.common.ApplicationContext;
import com.stonecj.utils.redis4search.service.IAddIdAndIndexItem;
import com.stonecj.utils.redis4search.service.ISearchIdsWithItem;
import com.stonecj.utils.redis4search.springboot.Redis4SearchProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 根据前缀搜索
 * 首个字作为key, 添加首个字相同的进入zset，分数相同 按添加的字符内容排序
 * <p>
 * 参考地址：
 * @url http://oldblog.antirez.com/post/autocomplete-with-redis.html
 */
public class PrefixSearch implements IAddIdAndIndexItem, ISearchIdsWithItem {


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
//    private Boolean encode;

//    private String charset;

    private String indexPrefix;

    private StringRedisTemplate stringRedisTemplate;

    private Redis4SearchProperties properties;


    public PrefixSearch(String nameSpace) {
        initBean();

        this.nameSpace = nameSpace;

        // 初始化redis前缀
        this.indexPrefix = nameSpace + ":" + "index:";
    }


    @Override
    public void addIdAndIndexItem(String id, String searched) throws IOException {
        List<String> words = doSplit(searched);

        if (words == null || words.size() == 0) {
            //分词结果为空，直接退出
            return;
        }

        // 直接存入中文
        // 如果对中文编码，查询也要解码结果
//        words = doEncode(words);

        words.forEach(word -> stringRedisTemplate.opsForZSet().add(indexPrefix + words.get(0), word, 0));
    }

    @Override
    public Set<String> searchIdsWithItem(String item) throws IOException {
        return searchIdsWithItem(item, null);
    }

    @Override
    public Set<String> searchIdsWithItem(String item, Integer count) throws IOException {
        String key = indexPrefix + item.substring(0, 1);

        long indexFrom = stringRedisTemplate.opsForZSet().rank(key, item);
        if (count == null) {
            return stringRedisTemplate.opsForZSet().range(key, indexFrom, -1);
        } else {
            return stringRedisTemplate.opsForZSet().range(key, indexFrom, indexFrom + count);
        }

    }


    private List<String> doSplit(String searched) {
        List<String> words = new ArrayList<>(searched.length());

        for (int i = 0; i < words.size(); i++) {
            words.add(searched.substring(0, i + 1));
        }

        return words;
    }

    private void initBean() {
        if (stringRedisTemplate == null) {
            stringRedisTemplate = ApplicationContext.getBean(StringRedisTemplate.class);
        }
        if (properties == null) {
            properties = ApplicationContext.getBean(Redis4SearchProperties.class);
        }
    }

}
