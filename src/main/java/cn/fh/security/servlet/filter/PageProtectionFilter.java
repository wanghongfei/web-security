package cn.fh.security.servlet.filter;

import cn.fh.security.exception.UnsupportedFilterException;
import cn.fh.security.servlet.PageProtectionContextListener;
import cn.fh.security.servlet.session.NativeHttpSession;
import cn.fh.security.servlet.session.RedisSessionDAO;
import cn.fh.security.servlet.session.SecurityServletRequestWrapper;
import cn.fh.security.servlet.session.SessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 该过虑器会将实际业务逻辑代理给 SecurityFilter 实现类
 * @author whf
 */
public class PageProtectionFilter implements Filter {
	public static Logger logger = LoggerFactory.getLogger(PageProtectionFilter.class);

    private static List<SecurityFilter> filters = new ArrayList<>(5);

    private SessionDAO sessionDAO = new RedisSessionDAO();

    static {
        filters.add(new RoleSecurityFilter());
    }

	@Override
	public void destroy() {

	}

	/**
     * 遍历filters, 依次执行每一个过虑器
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // 如果开启了session cluster
        // 执行session cluster相关逻辑
        if (PageProtectionContextListener.SESSION_CLUSTER) {

            // 尝试从存储仓库中查询session
            HttpSession session = sessionDAO.loadSession(getSid(req));

            logger.debug("session loaded, result => {}", session);

            // 将request对象替换成自定义的wrapper对象
            req = new SecurityServletRequestWrapper(req, session);
        }

        for (SecurityFilter filter : filters) {
            logger.debug("executing filter: {}", filter.getClass().getName());

            boolean hasNext = filter.doFilter(req, (HttpServletResponse) response );
            // 如果本filter返回false
            // 表示不需要执行后面的filter了
            if (!hasNext) {
                return;
            }
        }


        chain.doFilter(req, response);

        // 执行session cluster相关逻辑
        if (PageProtectionContextListener.SESSION_CLUSTER) {
            // 刷新session数据

            // 先判断有无session
            HttpSession session = req.getSession(false);
            // 如果没有session, 不做任何处理
            if (null == session) {
                return;
            }

            if (false == session instanceof NativeHttpSession) {
                throw new UnsupportedFilterException("filter " + session.getClass() + " unsupported!");
            }

            NativeHttpSession nativeSession = (NativeHttpSession) session;
            if (nativeSession.isDirty()) {
                logger.debug("flushing session");
                sessionDAO.flushSession(nativeSession);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * 从filter链中查找指定的filter
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T findFilter(Class<T> clazz) {
        for (SecurityFilter filter : filters) {
            if (filter.getClass() == clazz) {
                return (T) filter;
            }
        }

        return null;
    }

    private String getSid(HttpServletRequest req) {
        return req.getHeader("sid");
    }
}
