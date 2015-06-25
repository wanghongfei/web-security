# web-security - 致力于打造一个配置极简的JavaWeb安全框架

### 配置说明
* 使用`JSON`格式做配置文件: `WEB-INF/security-config.json`:<br />
```
{
    "enableAutoLogin": true, // 是否启用Cookie自动登陆验证
    "loginCookieName": "token", // 用于Cookie登陆的cookie名，仅当enableAutoLogin=true时有效
    "authBeanName": "defaultAccountService", // 用于Cookie自动登陆的业务逻辑的Spring Bean名
    "loginUrl": "/loginURL", // 登陆页面的URL
    
    // 拦截规则
    "interceptRole": [
        {
            "roleList": ["ADMIN"], // 表示访问该URL需要的角色名
            "toUrl": "/toUrl", // 如果验证失败，重写向到此URL
            "url": "/url" // 要验证权限的URL
        },
        {
            "roleList": ["ADMIN", "USER"],
            "toUrl": "/toUrl",
            "url": "/url"
        }
    ],

}
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

