# web-security - 致力于打造一个配置极简的JavaWeb安全框架

## 特点
- 剔除花哨用不到的功能, 只有基于URL的权限验证
- 支持`*`, `**`通配符

## 配置
拦截定义文件`WEB-INF/security-config.xml`:
```
<?xml version="1.0" encoding="UTF-8"?>
<security xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="security-config.xsd">

	<!-- 登陆页面地址 -->
	<login-page>/login</login-page>

	<!-- URL拦截规则定义 -->
	<rules>
        <request path="/user**" roles="ADMIN STUDENT EMPLOYER"/>

	</rules>
</security>
```

web.xml配置:
```
<listener>
    <listener-class>cn.fh.security.servlet.PageProtectionContextListener</listener-class>
</listener>
<filter>
    <filter-name>Page Protection Filter</filter-name>
    <filter-class>cn.fh.security.servlet.PageProtectionFilter</filter-class>
</filter>

<!-- 定义静态文件路径 -->
<context-param>
        <param-name>STATIC_RESOURCE_PATH</param-name>
        <param-value>/assets:/resources:/js:/img:/css</param-value>
</context-param>
```