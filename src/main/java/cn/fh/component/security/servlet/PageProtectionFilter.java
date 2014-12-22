package cn.fh.component.security.servlet;

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

import cn.fh.component.security.credential.Credential;
import cn.fh.component.security.utils.CredentialUtils;
import cn.fh.component.security.utils.ResponseUtils;
import cn.fh.component.security.utils.StringUtils;

/**
 * 控制页面访问的过虑器，拦截无对应页面访问权限的请求
 * @author whf
 *
 */
public class PageProtectionFilter implements Filter {
	public static Logger logger = LoggerFactory.getLogger(PageProtectionFilter.class);

	@Override
	public void destroy() {

	}

	/**
	 * 首先检查请求是否为静态资源，若是则放行.
	 * 若不是，检查请求是否需要权限，如果不需要，放行.
	 * 如果需要，检查session是否存在，如果不存在，拦截请求并返回错误信息.
	 * 如果存在，检查是否登陆和session中的用户是否有足够的权限访问该URL
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		String url = null;
		
		// 从URI中去掉上下文名
		url = StringUtils.trimContextFromUrl(req.getContextPath(), req.getRequestURI());

		
		if (logger.isDebugEnabled()) {
			logger.debug("请求url:" + url);
		}
		
		// 请求的是资源文件，无需处理
		if (url.startsWith("/resources")) {
			chain.doFilter(request, response);
		}
		
		// 检查role
		// role不满足
		if (false == checkRole(url, req)) {
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
	 * 判断是否允许用户访问该页面
	 * @param requestURL
	 * @param session
	 * @return
	 */
	private boolean checkRole(String requestURL, HttpServletRequest req) {
		List<String> roleList = PageProtectionServlet.rcm.get(requestURL);
		
		// 该请求不需要role, 放行
		if (null == roleList) {
			return true;
		}
		
		// 请求需要权限
		// 检查session是否存在
		boolean sessionExist = isSessionExisted(req);
		// session不存在，不允许访问
		if (false == sessionExist) {
			return false;
		}

		// session 存在
		// 得到Credential
		HttpSession session = req.getSession();
		Credential credential = CredentialUtils.getCredential(session);
		// 未登陆
		if (null == credential) {
			return false;
		}
		
		
		// 检查role是否满足
		return checkRole(roleList, credential);
	}
	
	/**
	 * 检查session是否存在
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
	 * 检查当前已经登陆用户的role是否满足条件
	 * @param roleList
	 * @param credential
	 * @return
	 */
	private boolean checkRole(List<String> roleList, Credential credential) {
		return roleList.stream()
			.anyMatch( (roleName) -> credential.hasRole(roleName) );

/*		for (String role : roleList) {
			if (false == credential.hasRole(role)) {
				return false;
			}
		}
		
		return true;*/
	}

}
