package cn.fh.security.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.fh.security.RoleInfo;
import cn.fh.security.credential.Credential;
import cn.fh.security.utils.CredentialUtils;
import cn.fh.security.utils.ResponseUtils;
import cn.fh.security.utils.StringUtils;

/**
 * A filter that intercepts all requests and determine whether server should 
 * process this request.
 * 
 * @author whf
 *
 */
public class PageProtectionFilter implements Filter {
	public static Logger logger = LoggerFactory.getLogger(PageProtectionFilter.class);

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
			logger.debug("请求url:" + url);
		}
		
		// the request is for static resource, just let it go.
		for (String path : PageProtectionContextListener.STATIC_RESOURCE_PATHS) {
			if (url.startsWith(path)) {
				chain.doFilter(request, response);
				return;
			}
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
	 * @param requestURL
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

/*		// check the existence of session
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
		}*/
		
		
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
