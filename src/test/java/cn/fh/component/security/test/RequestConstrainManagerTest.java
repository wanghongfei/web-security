package cn.fh.component.security.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.fh.security.RequestConstrainManager;
import cn.fh.security.model.RoleInfo;

public class RequestConstrainManagerTest {
	RequestConstrainManager rcm;
	
	@Before
	public void init() {
		this.rcm = new RequestConstrainManager();
	}
	
	@Test
	public void test() {
		rcm.put("/hello/kitty", new RoleInfo(null, new String[] {"USER"} ));
		rcm.put("/hello/kitty/doggy", new RoleInfo(null, new String[] {"USER"} ));
		rcm.put("/hello", new RoleInfo(null ,new String[] {"USER"} ));

		rcm.put("/hello/hi/*", new RoleInfo(null, new String[] {"ADMIN"} ));
		rcm.put("/hello/hi/doggy", new RoleInfo(null, new String[] {"NONE"} ));
		rcm.put("/hello/hi/cat", new RoleInfo(null, new String[] {"NONE"} ));
		
		List<String> roleList = null;
		roleList = rcm.get("/hello/hi/abcad").getRoleList();
		Assert.assertFalse(roleList.isEmpty());
		Assert.assertEquals("ADMIN", roleList.get(0));

		RoleInfo info = rcm.get("/hello/hi/abcad/ef");
		Assert.assertNull(info);

		roleList = rcm.get("/hello/kitty").getRoleList();
		Assert.assertFalse(roleList.isEmpty());
		Assert.assertEquals("USER", roleList.get(0));

		roleList = rcm.get("/hello").getRoleList();
		Assert.assertFalse(roleList.isEmpty());
		Assert.assertEquals("USER", roleList.get(0));

		roleList = rcm.get("/hello/hi/doggy").getRoleList();
		Assert.assertFalse(roleList.isEmpty());
		Assert.assertEquals("NONE", roleList.get(0));

		roleList = rcm.get("/hello/hi/cat").getRoleList();
		Assert.assertFalse(roleList.isEmpty());
		Assert.assertEquals("NONE", roleList.get(0));
	}

}
