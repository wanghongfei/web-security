package cn.fh.security.servlet.session;

import javax.servlet.http.HttpSession;

/**
 * Created by whf on 3/11/16.
 */
public class RedisSessionDAO implements SessionDAO {
    @Override
    public HttpSession loadSession(String sid) {
        return null;
    }

    @Override
    public void flushSession(HttpSession session) {

    }

    @Override
    public void deleteSession(String sid) {

    }
}
