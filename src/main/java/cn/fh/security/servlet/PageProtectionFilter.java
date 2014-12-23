package cn.fh.security.servlet;

import java.io.IOException;
import java.io.OutputStream;
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
		if (url.startsWith("/resources")) {
			chain.doFilter(request, response);
		}
		
		// check whether the client has enough roles
		if (false == checkRole(url, req)) {
			// response error information to client
			ResponseUtils.responseBadRole((HttpServletResponse) response);
			
			return;
		}
		
		
		
		chain.doFilter(request, response);


	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * check whether the client has enough roles.
	 * if client does not have session and this URL needs roles, return false
	 * 
	 * @param requestURL
	 * @param req
	 * @return
	 */
	private boolean checkRole(String requestURL, HttpServletRequest req) {
		List<String> roleList = PageProtectionServlet.rcm.get(requestURL);
		
		// this request does not need roles, return true
		if (null == roleList) {
			return true;
		}
		
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
		
		
		return checkRole(roleList, credential);
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
