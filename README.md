# 寻友（用户匹配平台） by WSJ 后端

## 一、项目介绍

能够匹配用户的移动端 H5 网站（APP 风格），基于 Spring Boot 后端 + Vue3 前端，包括用户登录、更新个人信息、按标签搜索用户、建房组队、推荐相似用户等功能。

## 二、技术选型

Java SpringBoot 框架

MySQL 数据库

MyBatis-Plus

MyBatis X 自动生成代码

Redis 缓存

Redis 分布式登录

Redisson 分布式锁

Easy Excel 数据导入

Spring Scheduler 定时任务

Swagger + Knife4j 接口文档

Jaccard相似度匹配用户

## 三、简介

1. 用户登录：使用 Redis 实现分布式 Session，解决集群间登录态同步问题；并使用 Hash 代替 String 来存储用户信息，节约内存并便于单字段的修改。
2. 对于项目中复杂的集合处理（比如为队伍列表关联已加入队伍的用户），使用 Java 8 Stream API 和 Lambda 表达式来简化编码。
3. 使用 Easy Excel 读取基础用户信息，并通过自定义线程池 + CompletableFuture 并发编程提高批量导入数据库的性能。
4. 使用 Redis 缓存首页高频访问的用户信息列表，且通过自定义 Redis 序列化器来解决数据乱码、空间浪费的问题。
5. 为解决首次访问系统的用户主页加载过慢的问题，使用 Spring Scheduler 定时任务来实现缓存预热，并通过分布式锁保证多机部署时定时任务不会重复执行。
6. 为解决同一用户重复加入队伍、入队人数超限的问题，使用 Redisson 分布式锁来实现操作互斥，保证了接口幂等性。
7. 使用 Knife4j + Swagger 自动生成后端接口文档，便于调试和维护接口文档。

## 四、运行展示

  主页：

![image-20230921172107902](https://cdn.jsdelivr.net/gh/vincenicky/image_store/blog/image-20230921172107902.png)

组队功能：

![image-20230921172157843](https://cdn.jsdelivr.net/gh/vincenicky/image_store/blog/image-20230921172157843.png)

创建队伍：

![image-20230921172311653](https://cdn.jsdelivr.net/gh/vincenicky/image_store/blog/image-20230921172311653.png)

个人信息及修改：

![image-20230921172349449](https://cdn.jsdelivr.net/gh/vincenicky/image_store/blog/image-20230921172349449.png)
