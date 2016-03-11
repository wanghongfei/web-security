package cn.fh.security.servlet.filter;

import cn.fh.security.credential.Credential;
import cn.fh.security.model.RoleInfo;
import cn.fh.security.servlet.PageProtectionContextListener;
import cn.fh.security.servlet.SecurityServletRequestWrapper;
import cn.fh.security.utils.ResponseUtils;
import cn.fh.security.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 该过滤器应该放在所有过虑器之前，第一个处理请求。
 * 该过虑器会检查每一个uri是否需要权限才能访问，如果需要则检查session中
 * 当前用户是否有指定角色，有则放行，无则拦下请求，向客户端发送错误信息或者重定向
 * 到配置文件中指定的uri.
 *
 * @author whf
 *
 */
public class RoleSecurityFilter implements SecurityFilter {
    private static Logger logger = LoggerFactory.getLogger(RoleSecurityFilter.class);

    @Override
    public boolean doFilter(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        SecurityServletRequestWrapper reqWrapper = new SecurityServletRequestWrapper(req);

        // remove context name from URI
        String url = StringUtils.trimContextFromUrl(req.getContextPath(), req.getRequestURI());


        logger.debug("请求uri:{}", url);

        // 检查是否是静态资源
        if (true == isStaticResource(url)) {
            return true;
        }

        if (req.getMethod().equals("OPTIONS")) {
            return true;
        }


        // 判断是否已经登陆
        boolean isLoggedIn = isLoggedIn(req);
        logger.debug("credential = {} ", isLoggedIn);



        // 查询该url对应的role
        RoleInfo rInfo = PageProtectionContextListener.rcm.get(url);
        // rInfo为空说明
        // 访问该URL不需要登陆(权限)
        if (null == rInfo) {
            logger.debug("不需要登陆");

            // 放行
            return true;
        }

        // 访问该URL需要登陆
        if (false == isLoggedIn) {
            // 用户没有登陆

            // 如果是POST方法
            // 返回json(forward页面)
            if (req.getMethod().equals("POST") || req.getMethod().equals("PUT") || req.getMethod().equals("DELETE")) {
                //ResponseUtils.sendErrorMessage((HttpServletResponse)response, "PERMISSION_ERROR", 13);
                ResponseUtils.sendForward(req ,(HttpServletResponse) response, PageProtectionContextListener.rcm.getConfig().getAuthErrorForward());
            } else {
                // 是GET方法
                // 重定向
                logger.debug("重定向至:{}", PageProtectionContextListener.rcm.getLoginUrl());
                ResponseUtils.sendRedirect((HttpServletResponse) response, PageProtectionContextListener.rcm.getLoginUrl());
            }

            return false;
        }

        // 检查role是否满足
        if (false == checkRole(rInfo, req)) {
            // 如果是POST方法
            // 返回json(forward页面)
            if (req.getMethod().equals("POST") || req.getMethod().equals("PUT") || req.getMethod().equals("DELETE")) {
                ResponseUtils.sendForward(req ,(HttpServletResponse) response, PageProtectionContextListener.rcm.getConfig().getAuthErrorForward());
            } else {
                // 是GET方法
                // 重定向
                logger.debug("重定向至:{}", PageProtectionContextListener.rcm.getLoginUrl());
                ResponseUtils.sendRedirect((HttpServletResponse) response, PageProtectionContextListener.rcm.getConfig().getAuthErrorRedirect());
            }

            return false;
        }



        return true;
    }

    /**
     * 检查用户是否登陆
     * @param req
     * @return
     */
    private boolean isLoggedIn(HttpServletRequest req) {
        // 检查request中是否有Credential对象
        Credential credential = (Credential) req.getAttribute(Credential.CREDENTIAL_CONTEXT_ATTRIBUTE);
        // client has not logged in, return false
        if (null == credential) {
            return false;
        }

        return true;
    }

    /**
     * check whether the client has enough roles.
     * if client does not have session and this URL needs roles, return false
     *
     * @param req
     * @return
     */
    private boolean checkRole(RoleInfo rInfo, HttpServletRequest req) {
        if (null == rInfo) {
            return true;
        }

        List<String> roleList = rInfo.getRoleList();

        // 返回null说明用户虽然配置了uri安全规则
        // 但没有为该uri指定任何角色.
        // 这种情况默认为任何已登陆使用都可以访问
        if (null == roleList) {
            return true;
        }


        // 查检credential中的用户是否具有指定的角色
        //Credential credential = CredentialUtils.getCredential(req.getSession());
        Credential credential = (Credential) req.getAttribute(Credential.CREDENTIAL_CONTEXT_ATTRIBUTE);
        return roleList.stream()
                .anyMatch( (roleName) -> credential.hasRole(roleName) );
    }

    private boolean isStaticResource(String url) {
        for (String path : PageProtectionContextListener.STATIC_RESOURCE_PATHS) {
            if (url.startsWith(path)) {
                return true;
            }
        }

        return false;
    }
}
