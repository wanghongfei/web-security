package cn.fh.security;

import cn.fh.security.model.Config;
import cn.fh.security.model.RoleInfo;
import cn.fh.security.utils.LogUtils;
import cn.fh.security.utils.StringUtils;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * URL规则管理器。
 * 本类持有3个Map对象，分别存放了以*, **结尾的URL，和不以任何
 * 通配符结尾的URL，并提供查询功能
 *
 * @author whf
 *
 */
public class RequestConstrainManager {
	public static Logger logger = LoggerFactory
			.getLogger(RequestConstrainManager.class);

	private Config config;

	/**
	 * 登陆页面的URI
	 */
	private String loginUrl;

	/**
	 * 保存不含通配符的uri - role映射.
	 * e.g., /user/profile
	 */
	private Map<String, RoleInfo> roleMap = new HashMap<String, RoleInfo>();
	/**
	 * 保存只在结尾处含有一个*的uri - role映射
	 * e.g., /user/post/*
	 */
	private Map<String, RoleInfo> wildcardRoleMap = new HashMap<String, RoleInfo>();

	/**
	 * 保存双**结尾的URL.
	 * e.g.: /user/**
	 */
	private Map<String, RoleInfo> doubleWildcardRoleMap = new HashMap<>();


	public RequestConstrainManager(Config config) {
		this.config = config;
	}

	/**
	 * 查询指定uri对应的权限规则
	 * @param url
	 * @return 如果该uri不需要权限，返回null
	 */
	public RoleInfo get(String url) {
		// first of all, perform accurate match
		RoleInfo info = doAccurateMatch(url);
        if (null != info) {
            return info;
        }

		// if no List found, perform wildcard match
        info = doSingleWildcardMatch(url);
        if (null != info) {
            return info;
        }

		// 如果*配置也没找到
		// 匹配以**结尾的URL
        info = doDoubleWildcardMatch(url);

		return info;
	}

	/**
	 * Put a url-role map into this manager.
	 * @param url A String representing the URL
	 * @param roleInfo
	 */
	public void put(String url, RoleInfo roleInfo) {

        // 以**结尾的rule放到doubleWildcardRoleMap中
		if (url.endsWith("**")) {
			this.doubleWildcardRoleMap.put(url, roleInfo);

		} else if(url.endsWith("*")) {
			this.wildcardRoleMap.put(url, roleInfo);

		} else {
			// if not, put this url into roleMap
			this.roleMap.put(url, roleInfo);
		}
	}

    /**
     * 执行精确匹配
     * @param url
     * @return
     */
    private RoleInfo doAccurateMatch(String url) {
        return this.roleMap.get(url);
    }

    /**
     * 从以*结尾的规则中查询有没有匹配
     * @param url
     * @return
     */
    private RoleInfo doSingleWildcardMatch(String url) {
        RoleInfo info = null;

        String wildcardUrl = StringUtils.trimLastUrlToken(url) + "/*";
        info = this.wildcardRoleMap.get(wildcardUrl);

        return info;
    }

    /**
     * 从以**结尾的规则中查找有没有匹配。
     * 将**结尾的url最后的2个*去掉，然后调用请求url
     * 的startsWith()方法判断是否匹配
     *
     * @param url
     * @return
     */
    private RoleInfo doDoubleWildcardMatch(String url) {
        RoleInfo info = null;

        Set<String> keySet = doubleWildcardRoleMap.keySet();
        for (String rule : keySet) {
            // 去掉rule结尾的**
            // 如, /user** -> /user
            String trimmedRule = rule.substring(0, rule.length() - 2);
            LogUtils.printLog(logger, Level.DEBUG, "对比: request url = {}, rule url = {}", url, trimmedRule);

            if (url.startsWith(trimmedRule)) {
                info = doubleWildcardRoleMap.get(rule);
                LogUtils.printLog(logger, Level.DEBUG, "命中, role info = {}", info);

                break;
            }
        }

        return info;
    }

	public String getLoginUrl() {
		return loginUrl;
	}

	public Config getConfig() {
		return config;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
}
