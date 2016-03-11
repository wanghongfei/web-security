package cn.fh.security.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * Created by whf on 3/11/16.
 */
public class SecurityServletRequestWrapper extends HttpServletRequestWrapper {
    private static Logger log = LoggerFactory.getLogger(SecurityServletRequestWrapper.class);

    private HttpSession session;

    public SecurityServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public HttpSession getSession() {
        log.debug("getSession() invoked!");

        return getSession(true);
    }

    /**
     * 返回自定义的HttpSession实现
     * @param create
     * @return
     */
    @Override
    public HttpSession getSession(boolean create) {
        log.debug("getSession(boolean) invoked!");
        if (create && null == session) {
            log.debug("creating new Session object!");
            session = new NativeHttpSession(getServletContext());
        }

        return session;
    }
}
