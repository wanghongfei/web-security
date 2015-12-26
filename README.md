# web-security - 致力于打造一个配置极简的JavaWeb安全框架

### 配置说明
* 使用`XML`格式做配置文件: `WEB-INF/security-config.xml`:<br />
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

* `WEB-INF/web.xml`:<br />
```
	<listener>
		<listener-class>cn.fh.security.servlet.PageProtectionContextListener</listener-class>
	</listener>
	<filter>
		<filter-name>Page Protection Filter</filter-name>
		<filter-class>cn.fh.security.servlet.PageProtectionFilter</filter-class>
	</filter>
	
	<context-param>
            <param-name>STATIC_RESOURCE_PATH</param-name>
            <param-value>/assets:/resources:/js:/img:/css</param-value>
    </context-param>
```

