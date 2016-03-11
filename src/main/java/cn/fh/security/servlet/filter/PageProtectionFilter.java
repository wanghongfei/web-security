package cn.fh.security.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

        for (SecurityFilter filter : filters) {
            logger.debug("executing filter: {}", filter.getClass().getName());

            boolean hasNext = filter.doFilter( (HttpServletRequest) request, (HttpServletResponse) response );
            // 如果本filter返回false
            // 表示不需要执行后面的filter了
            if (!hasNext) {
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
