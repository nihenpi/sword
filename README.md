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
### base
 - bean：主要保存通用消息，实体类的公用超类
 - exception：封装的异常
 - jpa：基于spring data jpa 封装的一些工具类和repository的超类，提供持久层相关操作的支持
 - serializer: 对于序列化提供相关的支持
 - service：service类基本封装
 - utils：提供通用的工具类
 - web：提供web层的相关支持
 - 
