package com.stonecj.utils.redis4search.service.impl;

import com.stonecj.utils.redis4search.common.ApplicationContext;
import com.stonecj.utils.redis4search.common.SplitType;
import com.stonecj.utils.redis4search.service.IAddIdAndIndexItem;
import com.stonecj.utils.redis4search.service.ISearchIdsWithItem;
import com.stonecj.utils.redis4search.springboot.Redis4SearchProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 * @desc 逐字搜索 or 按词搜索
 */
@Slf4j
public class ParticipleSearch implements IAddIdAndIndexItem, ISearchIdsWithItem {

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
    private Boolean encode;

    private String charset;

    /**
     * 分词方式
     */
    private SplitType splitType;

    /**
     * 分词工具，动态加载
     *
     * @TODO 加载方式考究
     */
    private String splitUitl;

    private String indexPrefix;

    private String resultPrefix;

    private StringRedisTemplate stringRedisTemplate;

    private Redis4SearchProperties properties;

    public ParticipleSearch() {
        this(null);
    }

    public ParticipleSearch(String nameSpace) {
        this(nameSpace, null, true, SplitType.PerChar);
    }

    public ParticipleSearch(String nameSpace, String charset, boolean encode, SplitType splitType) {
        initBean();

        if (StringUtils.isEmpty(nameSpace)) {
            this.nameSpace = properties.getSearchConfig().getNameSpace();
        } else {
            this.nameSpace = nameSpace;
        }

        if (StringUtils.isEmpty(charset)) {
            this.charset = properties.getSearchConfig().getCharset();
        } else {
            this.charset = charset;
        }

        this.encode = encode;
        this.splitType = splitType;

        // 初始化redis前缀
        this.indexPrefix = nameSpace + ":" + "index:";
        this.resultPrefix = nameSpace + ":" + "result:";
    }

    @Override
    public void addIdAndIndexItem(String id, String searched) throws IOException {
        List<String> words = doSplit(searched);

        if (words == null || words.size() == 0) {
            //分词结果为空，直接退出
            return;
        }

        // 对分词base64编码
        words = doEncodeAndConcat(words);

        // 建立索引
        words.forEach(word -> stringRedisTemplate.opsForSet().add(word, id));

    }

    @Override
    public Set<String> searchIdsWithItem(String item) throws IOException {
        return searchIdsWithItem(item, null);
    }

    @Override
    public Set<String> searchIdsWithItem(String item, Integer count) throws IOException {
        List<String> words = doSplit(item);

        if (words == null || words.size() == 0) {
            return Collections.EMPTY_SET;
        }

        words = doEncodeAndConcat(words);

        if (words.size() == 1) {
            if (count == null) {
                return stringRedisTemplate.opsForSet().members(words.get(0));
            } else {
                Cursor<String> cursor = stringRedisTemplate.opsForSet()
                        .scan(words.get(0),
                                ScanOptions
                                        .scanOptions()
                                        .count(count.longValue())
                                        .build()
                        );

                Set<String> res = new TreeSet<>();
                while (cursor.hasNext()) {
                    res.add(cursor.next());
                }

                return res;
            }
        } else {
            if (count == null) {
                return stringRedisTemplate.opsForSet().intersect(words.get(0), words.subList(1, words.size()));
            } else {

                String tmp = resultPrefix;
                if (encode) {
                    tmp += Base64Utils.encodeToString(item.getBytes(Charset.forName(charset)));
                } else {
                    tmp += item;
                }

                Boolean exists = stringRedisTemplate.expire(tmp, 10, TimeUnit.MINUTES);
                if (!exists) {
                    // 关键字取交集
                    stringRedisTemplate.opsForSet().intersectAndStore(words.get(0), words.subList(1, words.size()), tmp);
                    // 结果缓存10分钟
                    stringRedisTemplate.expire(tmp, 10, TimeUnit.MINUTES);
                }

                // 取前N条
                Cursor<String> cursor = stringRedisTemplate.opsForSet().scan(tmp, ScanOptions.scanOptions().count(count).build());
                Set<String> res = new TreeSet<>();
                while (cursor.hasNext()) {
                    res.add(cursor.next());
                }

                return res;
            }
        }
    }

    private void initBean() {
        if (stringRedisTemplate == null) {
            stringRedisTemplate = ApplicationContext.getBean(StringRedisTemplate.class);
        }
        if (properties == null) {
            properties = ApplicationContext.getBean(Redis4SearchProperties.class);
        }
    }

    /**
     * 分词
     *
     * @param searched 被分词文本
     * @return 分词结果
     * @throws IOException
     */
    private List<String> doSplit(String searched) throws IOException {
        List<String> words;
        switch (splitType) {
            case PerChar:
                words = Arrays.stream(searched.split(""))
                        .filter(word -> StringUtils.hasText(word))
                        .collect(Collectors.toList());
                break;
            case WithTool:
                // @TODO 各分词工具，怎么处理（例如：ik）
                IKAnalyzer analyzer = new IKAnalyzer();
                analyzer.setUseSmart(true);

                TokenStream ts = analyzer.tokenStream("content", searched);
                ts.addAttribute(CharTermAttribute.class);

                words = new ArrayList<>();
                while (ts.incrementToken()) {
                    words.add(ts.getAttribute(CharTermAttribute.class).toString());
                }

                break;
            default:
                words = null;
        }
        return words;
    }

    private List<String> doEncodeAndConcat(List<String> words) {
        if (encode) {
            Charset charset1 = Charset.forName(charset);

            //如果需要对index编码
            words = words.stream()
                    .map(word -> indexPrefix + new String(Base64Utils.encode(word.getBytes(charset1))))
                    .collect(Collectors.toList());
        } else {
            words = words.stream()
                    .map(word -> indexPrefix + word)
                    .collect(Collectors.toList());
        }
        return words;
    }
}
