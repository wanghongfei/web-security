package cn.fh.security.model;

import java.util.Arrays;
import java.util.List;

/**
 * 一条规则
 *
 * @author whf
 *
 */
public class RoleInfo {
	/**
	 * 该条规则适用的uri
	 */
	private String url;
	/**
	 * 该条规则所包含的角列表
	 */
	private List<String> roleList;
	/**
	 * 当验证失败时跳转的uri
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
