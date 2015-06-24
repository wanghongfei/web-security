package cn.fh.security.credential;

import java.util.Map;

/**
 * Created by wanghongfei on 15-6-24.
 */
public interface AuthLogic {
    Map<String, Object> loginByToken(String cookieToken);

    String MODEL = "model";
    String USERNAME = "username";
    String ROLE_NAME = "roleName";
    String ROLE_LIST = "roleList";
    String ID= "id";
}
