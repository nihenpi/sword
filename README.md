# sword
## 技术栈
 基于springboot
 
 - MVC：SpringMVC
 - 安全框架：Spring Security
 - 容器：Spring
 - 持久层：Spring Data Jpa
 - 本机缓存：ehcache
 - 远程缓存：使用redis作NoSql数据库
 - 数据库：Mysql

## 项目结构
### base 主要提供项目的基础支持
 - bean：主要保存通用消息，实体类的公用超类
 - exception：封装的异常
 - jpa：基于spring data jpa 封装的一些工具类和repository的超类，提供持久层相关操作的支持
 - serializer: 对于序列化提供相关的支持
 - service：service类基本封装
 - utils：提供通用的工具类
 - web：提供web层的相关支持
 
### cache 缓存模块，提供缓存操作相关的template

### redis redis模块，提供对于与redis通信的支持

### security 安全模块
 - core：包含权限过滤器以及安全管理器
 - jwt：使用jwt负责token的生成以及验证

## 系统结构

  ![avatar](https://s1.ax1x.com/2018/08/06/PrcGvT.png)
