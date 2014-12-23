# web-security - 致力于打造一个配置极简的JavaWeb安全框架

### Configuration Example
* `WEB-INF/security-page.xml`:
	<?xml version="1.0" encoding="UTF-8"?>
	<page xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:noNamespaceSchemaLocation="security-page.xsd">
	
		<request url="/backstage/statistics" role="ADMIN USER" />
		<request url="/backstage/*" role="ADMIN USER" />
	</page>

* `WEB-INF/web.xml`:
	<listener>
		<listener-class>cn.fh.security.servlet.PageProtectionServlet</listener-class>
	</listener>
	<filter>
		<filter-name>Page Filter</filter-name>
		<filter-class>cn.fh.security.servlet.PageProtectionFilter</filter-class>
	</filter>


That's all.
