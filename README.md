# web-security - 致力于打造一个配置极简的JavaWeb安全框架

### 为啥安全框架配置都这么繁杂！忍不了了！！
### example
	<?xml version="1.0" encoding="UTF-8"?>
	<page xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:noNamespaceSchemaLocation="security-page.xsd">
	
		<request url="/backstage/statistics" role="ADMIN USER" />
		<request url="/backstage/*" role="ADMIN USER" />
	</page>
