package cn.fh.security.credential;

import java.util.List;

/**
 * Interface for user credential
 *
 * @author wanghongfei
 *
 */
public interface Credential {
    /**
     * This integer is the primary key used in underlying database table.
     * @return The primary key of the underlying entity.
     */
	Integer getId();

    /**
     * String used to login.
     * @return username of this account.
     */
	String getUsername();
	void setUsername(String name);

    /**
     * Nick name cannot be used to login.
     * @return The nick name of this user.
     */
	String getNickName();
	void setNickName(String name);
	
	Integer getCredits();
	void setCredits(Integer point);

	List<String> getRoleList();
	boolean hasRole(String roleName);
	
	void addRole(String roleName);
	void addRoles(String[] roleNames);
    void addRoles(List<String> roleList);
	
	/**
	 * 实现类的对象在session中的key名
	 */
	String CREDENTIAL_CONTEXT_ATTRIBUTE = "CURRENT_USER_CREDENTIAL";
}