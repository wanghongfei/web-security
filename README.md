# web-security - 致力于打造一个配置极简的JavaWeb安全框架

### Configuration Example
* `WEB-INF/security-page.xml`:<br />
```
	<?xml version="1.0" encoding="UTF-8"?>
	<page xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:noNamespaceSchemaLocation="security-page.xsd">

		<request url="/backstage/statistics" role="ADMIN USER" />
		<request url="/backstage/*" role="ADMIN USER" />
		<request url="/admin/*" role="ADMIN USER" to-url="/error" />

	</page>
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


That's all.
