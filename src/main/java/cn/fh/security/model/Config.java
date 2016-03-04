package cn.fh.security.model;

import cn.fh.security.RequestConstrainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 封装用户配置文件
 * Created by wanghongfei on 15-6-24.
 */
public class Config {
    private static Logger logger = LoggerFactory.getLogger(Config.class);

    /**
     * 拦截规则
     */
    private List<RoleInfo> interceptRule;
    /**
     * 登陆页面地址
     */
    private String loginUrl;
    /**
     * 权限不足时redirect的地址
     */
    private String authErrorUrl;



    public Config() {}

    public Config(List<RoleInfo> roleInfoList, String loginUrl) {
        this.interceptRule = roleInfoList;
        this.loginUrl = loginUrl;
    }

    public RequestConstrainManager buildManager() {
        if (null == interceptRule) {
            throw new IllegalStateException("没有拦截列表");
        }

        RequestConstrainManager manager = new RequestConstrainManager(this);
        for (RoleInfo info : interceptRule) {
/*            for (String role : info.getRoleList()) {
                logger.info("找到rule:{}->{}", info.getUrl(), role);
            }*/
            manager.put(info.getUrl(), info);
        }

        return manager;
    }

    public List<RoleInfo> getInterceptRule() {
        return interceptRule;
    }

    public void setInterceptRule(List<RoleInfo> interceptRule) {
        this.interceptRule = interceptRule;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getAuthErrorUrl() {
        return authErrorUrl;
    }

    public void setAuthErrorUrl(String authErrorUrl) {
        this.authErrorUrl = authErrorUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}
