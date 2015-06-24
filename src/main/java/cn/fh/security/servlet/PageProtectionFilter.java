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
 * A filter that intercepts all requests and determine whether server should 
 * process this request.
 * 
 * @author whf
 *
 */
public class PageProtectionFilter implements Filter, ApplicationContextAware {
	public static Logger logger = LoggerFactory.getLogger(PageProtectionFilter.class);

	private static ApplicationContext appContext;

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
			logger.debug("请求url:{}", url);
		}


		// the request is for static resource, just let it go.
		for (String path : PageProtectionContextListener.STATIC_RESOURCE_PATHS) {
			if (url.startsWith(path)) {
				chain.doFilter(request, response);
				return;
			}
		}

		// 得到JSON配置对象
		Config config = PageProtectionContextListener.rcm.getConfig();

		// 如果启用了cookie登陆
		// 则执行cookie自动登陆逻辑
		if (config.isEnableAutoLogin() != null && true == config.isEnableAutoLogin()) {
			logger.info("cookie登陆");
			loginByCookie(config, req);
		}


		// check whether the client has enough roles
		RoleInfo rInfo = PageProtectionContextListener.rcm.get(url);
		// 访问该URL不需要登陆
		if (null == rInfo) {
			chain.doFilter(request, response);
			return;
		}

		// 访问该URL需要登陆
		if (false == isLoggedIn(req)) {
			// 用户没有登陆
			// 重定向到login页面
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

			AuthLogic authBean = (AuthLogic) appContext.getBean(authBeanName);
			Map<String, Object> map = authBean.loginByToken(authCookie.getValue());
			if (null == map) {
				logger.info("cookie登陆失败");
				return;
			}

			Credential cre = new DefaultCredential((Integer) map.get(AuthLogic.ID), (String)map.get(AuthLogic.USERNAME));
			cre.addRole((String)map.get(AuthLogic.ROLE_NAME));
			CredentialUtils.createCredential(req.getSession(), cre);


			logger.info("用户通过cookie成功登陆");
		} else {
			logger.info("未发现token");
		}

	}

	/**
	 * 检查用户是否登陆
	 * @param req
	 * @return
	 */
	private boolean isLoggedIn(HttpServletRequest req) {
		// check the existence of session
		boolean sessionExist = isSessionExisted(req);
		// no session exists, return false
		if (false == sessionExist) {
			return false;
		}

		// session exists
		// check whether client is logged in
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
		
		// this request does not need roles, return true
		if (null == roleList) {
			return true;
		}


		return checkRole(roleList, CredentialUtils.getCredential(req.getSession()));
	}
	
	/**
	 * check the existence of session
	 * @param req
	 * @return
	 */
	private boolean isSessionExisted(HttpServletRequest req) {
		if (null == req.getSession(false)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * check whether the client has permission to let server process its request.
	 * @param roleList
	 * @param credential
	 * @return
	 */
	private boolean checkRole(List<String> roleList, Credential credential) {
		return roleList.stream()
			.anyMatch( (roleName) -> credential.hasRole(roleName) );
	}

}
