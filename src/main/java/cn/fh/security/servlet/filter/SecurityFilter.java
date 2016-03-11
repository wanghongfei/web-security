package cn.fh.security.servlet.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whf on 3/11/16.
 */
public interface SecurityFilter {
    /**
     * 执行业务逻辑。
     * @param request
     * @param response
     * @return 返回true表示继续执行下一个过虑器, 返回false表示已经向client
     *          发送完响应, 处理结束
     */
    boolean doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
