# web-security - 致力于打造一个配置极简的JavaWeb安全框架

## 特点
- 剔除花哨用不到的功能, 只有基于URL的role权限验证
- 支持`*`, `**`通配符
- (Developing) 内置 Session Cluster 支持, 默认将session保存至Redis, 可以实现自己的`SessionDAO`修改Session存储方式


## 配置
拦截定义文件`WEB-INF/security-config.xml`:
```
<?xml version="1.0" encoding="UTF-8"?>
<security xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="security-config.xsd">

	<!-- 登陆页面地址 -->
	<login-page>/login</login-page>
	<!-- GET请求访问权限不足时重定向的地址 -->
	<auth-error-redirect>/error</auth-error-redirect>
	<!-- POST请求访问权限不足时forward地址(可以渲染一个json页面) -->
	<auth-error-forward>/json</auth-error-forward>

	<!-- URL拦截规则定义 -->
	<rules>
        <request path="/user**" roles="ADMIN STUDENT EMPLOYER"/>

	</rules>
</security>
```

普通Java Web项目中web.xml配置:
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

<!-- 启用session集群功能 -->
<context-param>
        <param-name>SESSION_CLUSTER</param-name>
        <param-value>true</param-value>
</context-param>
```

Spring Boot下的配置方法:
```
    /**
     * 注册过虑器
     * @return
     */
    @Bean
    public FilterRegistrationBean registerFilters() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new PageProtectionFilter());
        bean.addUrlPatterns("/*");
        // 指定配置文件位置, 默认为/WEB-INF/security-config.xml
        bean.addInitParameter("SECURITY_CONFIG_PATH", "security/security-config.xml");
        bean.setName("Web Security Filter");

        return bean;
    }

    /**
     * 注册Listener
     * @return
     */
    @Bean
    public PageProtectionContextListener registerListener() {
        return new PageProtectionContextListener();
    }
```

## 自定义SessionDAO
`sessionDAO`是`PageProtectionFilter`的成员变量，框架通过该接口实现session存储，默认为空实现。
用户实现自定义`SessionDAO`时, 通过Spring将自己的实现注入到该成员变量中即可。
