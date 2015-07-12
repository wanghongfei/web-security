package cn.fh.security;

import cn.fh.security.model.Config;
import cn.fh.security.model.RoleInfo;
import cn.fh.security.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class contains 2 maps from url to role where url is the request 
 * made by client and the role is the Role needed in order to process 
 * this request.
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
	 * A map used to store normal URL.
	 * e.g.: /user/profile, /user/register
	 */
	private Map<String, RoleInfo> roleMap = new HashMap<String, RoleInfo>();
	/**
	 * A map used to store URL including wildcard.
	 * e.g.: /user/*, /chat/girls/*
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
	 * Get the roles needed for this url
	 * @param url
	 * @return Return null if url does not exist in this manager
	 */
	public RoleInfo get(String url) {
		// first of all, perform accurate match
		RoleInfo info = this.roleMap.get(url);
		if (null != info) {
			return info;
		}

		// if no List found, perform wildcard match
		String wildcardUrl = StringUtils.trimLastUrlToken(url) + "/*";
		info = this.wildcardRoleMap.get(wildcardUrl);
		if (null != info) {
			return info;
		}

		// 如果*配置也没找到
		// 匹配以**结尾的URL
		Set<String> keySet = doubleWildcardRoleMap.keySet();
		for (String rule : keySet) {
            // 去掉rule结尾的**
            // 如, /user** -> /user
			String trimmedRule = rule.substring(0, rule.length() - 2);
			if (logger.isDebugEnabled()) {
				logger.debug("对比: request url = {}, rule url = {}", url, trimmedRule);
			}

			if (url.startsWith(trimmedRule)) {
				info = doubleWildcardRoleMap.get(rule);
				if (logger.isDebugEnabled()) {
					logger.debug("命中, role info = {}", info);
				}
				break;
			}
		}

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
