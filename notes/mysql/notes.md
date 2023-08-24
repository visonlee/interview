## 如何定位慢查询
- 开源工具 
  - 调试工具: Arthas
  - 运维工具: App Dynamic、Prometheus、Skywalking
- Mysql 自带慢日志查询功能
  - slow_query_log=1 #打开开关
  - long_query_time=2 #超过2秒视为慢查询,保存在/var/lib/mysql/localhost-slow.log中


## sql执行得很慢,该如何分析呢
分析思路:
- 聚合查询
- 多表查询
- 表数据量过大
- 深度分页查询

分析方法,可以采用MySQL自带的分析工具EXPLAIN
- 通过key和key_len检查是否命中了索引（索引本身存在是否有失效的情况)
- 通过type字段查看sql是否有进一步的优化空间，是否存在全索引扫描或全盘扫描
- 通过extra建议判断，是否出现了回表的情况，如果出现了，可以尝试添加索引或修改返回字段来
