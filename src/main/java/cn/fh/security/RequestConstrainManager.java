package cn.fh.security;

import cn.fh.security.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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

	/**
	 * A map used to store normal URL.
	 * e.g.: /user/profile, /user/register
	 */
	//private Map<String, List<String>> roleMap = new HashMap<String, List<String>>();
	private Map<String, RoleInfo> roleMap = new HashMap<String, RoleInfo>();
	/**
	 * A map used to store URL including wildcard.
	 * e.g.: /user/*, /chat/girls/*
	 */
	//private Map<String, List<String>> wildcardRoleMap = new HashMap<String, List<String>>();
	private Map<String, RoleInfo> wildcardRoleMap = new HashMap<String, RoleInfo>();

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

		return info;
	}

	/**
	 * Put a url-role map into this manager.
	 * @param url A String representing the URL
	 * @param roleInfo
	 */
	//public void put(String url, List<String> roleList) {
	public void put(String url, RoleInfo roleInfo) {
		char lastChar = url.charAt(url.length() - 1);

		// if there's `*` in this url, put this url into wildcardRoleMap
		if ('*' == lastChar) {
			this.wildcardRoleMap.put(url, roleInfo);
		} else {
			// if not, put this url into roleMap
			this.roleMap.put(url, roleInfo);
		}
	}
}
