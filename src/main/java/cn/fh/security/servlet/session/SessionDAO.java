package cn.fh.security.servlet.session;

import javax.servlet.http.HttpSession;

/**
 * Created by whf on 3/11/16.
 */
public interface SessionDAO {
    /**
     * 读取完整session信息
     * @param sid
     * @return
     */
    HttpSession loadSession(String sid);

    /**
     * 刷新session信息到存储仓库中
     * @param session
     */
    void flushSession(HttpSession session);

    /**
     * 删除session
     * @param sid
     */
    void deleteSession(String sid);
}
