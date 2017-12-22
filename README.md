# utils-redis4search
使用redis做全文检索



### 场景

1. 替代数据库操作：like '%xxxx%'
   1. 在mysql innodb中，不支持字段左侧的模糊匹配，即使加了索引也不会有效果
2. 替代数据库操作：like 'xxxx%'
3. 带语义的全文检索
   1. 将索引对象分词，建立索引
   2. warning，查询时输入没有有效分词，会查不出结果
      1. 例如：了，呢  这种语气词，无意义。



### maven构建

> 项目没有添加到maven仓库



```shell
mvn install
```

```xml
<groupId>com.stonecj.utils</groupId>
<artifactId>redis4search</artifactId>
<version>0.0.1-SNAPSHOT</version>
```





### 依赖

1、springboot项目

2、StringRedisTemplate作为redis操作类

```java
# 自行创建, 项目会自动从spring容器获取

@Bean
public StringRedisTemplate stringRedisTemplate() {
    return new StringRedisTemplate(redisConnectionFactory);
}
```





### 使用

1. 全文检索

```java
# 全文检索对象创建（可以复用，但不允许并发）
ParticipleSearch participleSearch = ParticipleSearchBuilder.start()
                            .setNameSpace("oa:oa-java-job:haha:") 	# 设置redis键值前缀
                            .build();

# 添加索引对象
participleSearch.addIdAndIndexItem("id", "索引词");   # 默认逐字分割, "索|引|词"
  
Set<String> ids = participleSearch.searchIdsWithItem("引词");
```

2. 前缀检索

```java
# 前缀检索对象创建（可以复用，但不允许并发）
PrefixSearch prefixSearch = PrefixSearchBuilder.start()
                            .setNameSpace("oa:oa-java-job:haha:") 	# 设置redis键值前缀
                            .build();

# 添加索引对象
prefixSearch.addIdAndIndexItem("id", "索引词");   # "索"/"索引"/"索引词"
  
Set<String> ids = prefixSearch.searchIdsWithItem("索引");
```



