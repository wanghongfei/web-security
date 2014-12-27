package cn.fh.security;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable.
 * 
 * @author whf
 *
 */
public class RoleInfo {
	private List<String> roleList;
	/**
	 * Jump to this url if validation fails
	 */
	private String toUrl;
	
	public RoleInfo(String toUrl, String... roles) {
		this.toUrl = toUrl;
		this.roleList = Arrays.asList(roles);
	}
	
	public List<String> getRoleList() {
		return roleList;
	}
	public String getToUrl() {
		return toUrl;
	}
	
	
}
