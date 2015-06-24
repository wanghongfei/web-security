package cn.fh.security.model;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable.
 * 
 * @author whf
 *
 */
public class RoleInfo {
	private String url;
	private List<String> roleList;
	/**
	 * Redirect to this url if validation fails
	 */
	private String toUrl = "";

	public RoleInfo() {}
	
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

	public String getUrl() {
		return url;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
