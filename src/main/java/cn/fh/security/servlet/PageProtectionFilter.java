package cn.fh.security.servlet;

import cn.fh.security.credential.AuthLogic;
import cn.fh.security.credential.Credential;
import cn.fh.security.credential.DefaultCredential;
import cn.fh.security.model.Config;
import cn.fh.security.model.RoleInfo;
import cn.fh.security.utils.CredentialUtils;
import cn.fh.security.utils.ResponseUtils;
import cn.fh.security.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 该过滤器应该放在所有过虑器之前，第一个处理请求。
 * 该过虑器会检查每一个uri是否需要权限才能访问，如果需要则检查session中
 * 当前用户是否有指定角色，有则放行，无则拦下请求，向客户端发送错误信息或者重定向
 * 到配置文件中指定的uri.
 *
 * @author whf
 *
 */
public class PageProtectionFilter implements Filter, ApplicationContextAware {
	public static Logger logger = LoggerFactory.getLogger(PageProtectionFilter.class);

	private static ApplicationContext appContext;
	private static AuthLogic authBean;

	@Override
	public void destroy() {

	}

	/**
	 * check every request and determine whether the client has enough roles to 
	 * let server process its request.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		String url = null;
		
		// remove context name from URI
		url = StringUtils.trimContextFromUrl(req.getContextPath(), req.getRequestURI());

		
		if (logger.isDebugEnabled()) {
			logger.debug("请求uri:{}", url);
		}

        // 检查是否是静态资源
        if (true == isStaticResource(url)) {
            chain.doFilter(request, response);
            return;
        }


		// 判断是否已经登陆
		boolean isLoggedIn = isLoggedIn(req);

		// 得到JSON配置对象
		Config config = PageProtectionContextListener.rcm.getConfig();

		// 如果启用了cookie登陆
		// 则执行cookie自动登陆逻辑
		if (config.isEnableAutoLogin() != null && true == config.isEnableAutoLogin()) {
			// 如果用户已经是登陆状态
			// 则不执行cookie登陆逻辑
			if (logger.isDebugEnabled()) {
				logger.debug("isLoggedIN = ====" + isLoggedIn);
			}
			if (false == isLoggedIn) {
				if (logger.isDebugEnabled()) {
					logger.debug("cookie登陆");
				}

				loginByCookie(config, req);
				isLoggedIn = isLoggedIn(req);
			}
		}


		// check whether the client has enough roles
		RoleInfo rInfo = PageProtectionContextListener.rcm.get(url);
		// 访问该URL不需要登陆(权限)
		if (null == rInfo) {
			if (logger.isDebugEnabled()) {
				logger.debug("不需要登陆");
			}

			chain.doFilter(request, response);
			return;
		}

		// 访问该URL需要登陆
		if (false == isLoggedIn) {
			// 用户没有登陆
			// 重定向到login页面
			if (logger.isDebugEnabled()) {
				logger.debug("重定向至:{}", PageProtectionContextListener.rcm.getLoginUrl());
			}
			ResponseUtils.sendRedirect((HttpServletResponse) response, PageProtectionContextListener.rcm.getLoginUrl());
			return;
		}

		// 检查role是否满足
		if (false == checkRole(rInfo, req)) {
			// response error information to client
			ResponseUtils.responseBadRole((HttpServletResponse) response, rInfo);
			
			return;
		}
		
		
		
		chain.doFilter(request, response);


	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appContext = applicationContext;
	}

	private void loginByCookie(Config config, HttpServletRequest req) {
		// 得到Cookie
		Cookie[] cookies = req.getCookies();
		if (null == cookies) {
			return;
		}

		// 查找登陆用的Cookie
		Optional<Cookie> cookieOpt = Arrays.stream(cookies)
				.filter(co -> co.getName().equals(config.getLoginCookieName()))
				.findFirst();

		// 从IoC容器中得到根据token登陆的业务逻辑bean
		// 执行并得到Credential
		// 最后将Credential放入session, 完成登陆
		if (cookieOpt.isPresent()) {
			Cookie authCookie = cookieOpt.get();
			String authBeanName = config.getAuthBeanName();
			if (null == authBeanName || authBeanName.isEmpty()) {
				throw new IllegalStateException("authBeanName未指定");
			}

			// 只有当authBean是第一次从容器中获取时才从容器中取bean
			if (null == authBean) {
				authBean = (AuthLogic) appContext.getBean(authBeanName);
			}

			// 调用authBean的cookie登陆业务方法
			Map<String, Object> map = authBean.loginByToken(authCookie.getValue());
			if (null == map) {
				logger.info("cookie登陆失败");
				return;
			}

			// 创建Credential
			Credential cre = new DefaultCredential((Integer) map.get(AuthLogic.ID), (String)map.get(AuthLogic.USERNAME));
			cre.addRole((String) map.get(AuthLogic.ROLE_NAME));

			// 将Credential放到session中
			CredentialUtils.createCredential(req.getSession(), cre);

			req.getSession().setAttribute("user", map.get(AuthLogic.MODEL));
			req.getSession().setAttribute("role", map.get(AuthLogic.ROLE_LIST));


			if (logger.isDebugEnabled()) {
				logger.debug("用户通过cookie成功登陆");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("未发现token, cookie登陆失败");
			}
		}

	}

	/**
	 * 检查用户是否登陆
	 * @param req
	 * @return
	 */
	private boolean isLoggedIn(HttpServletRequest req) {
		// 先检查session是否存在
		boolean sessionExist = isSessionExisted(req);
		if (false == sessionExist) {
			return false;
		}

		// 检查session中是否有Credential对象
		HttpSession session = req.getSession();
		Credential credential = CredentialUtils.getCredential(session);
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


		// 查检session中的用户是否具有指定的角色
		Credential credential = CredentialUtils.getCredential(req.getSession());
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
	
	/**
	 * check the existence of session
	 * @param req
	 * @return
	 */
	private boolean isSessionExisted(HttpServletRequest req) {
		if (null == req.getSession(false)) {
			if (logger.isDebugEnabled()) {
				logger.debug("session不存在");
			}
			return false;
		}
		
		return true;
	}

}
