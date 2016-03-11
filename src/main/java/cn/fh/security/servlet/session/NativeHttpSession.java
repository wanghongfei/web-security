package cn.fh.security.servlet.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

/**
 * Web-Security框架管理的HttpSession实现
 * Created by whf on 3/11/16.
 */
public class NativeHttpSession implements HttpSession {
    private long startTime = -1;
    private long lastAccessTime = -1;
    private int maxInactive = -1;

    private String sid;

    private ServletContext ctx;

    private Map<String, Object> parameterMap;

    /**
     * 标记数据是否发生变化
     */
    private boolean dirty = false;

    public NativeHttpSession(ServletContext ctx) {
        this.ctx = ctx;

        parameterMap = new HashMap<>();
        dirty = true;
    }


    @Override
    public long getCreationTime() {
        if (-1 == this.startTime) {
            this.startTime = new Date().getTime();
            this.lastAccessTime = startTime;
        }

        return this.startTime;
    }

    @Override
    public String getId() {
        if (null == sid) {
            sid = UUID.randomUUID().toString();
        }

        return this.sid;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessTime;
    }

    @Override
    public ServletContext getServletContext() {
        return this.ctx;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactive = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactive;
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Object getValue(String s) {
        return parameterMap.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new EnumerationAdapter(parameterMap.keySet());
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String key, Object val) {
        dirty = true;
        parameterMap.put(key, val);
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    public void putValue(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {
        dirty = true;
        parameterMap.remove(s);
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    public void removeValue(String s) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * 将Iterable配置Enumeration接口
     */
    private class EnumerationAdapter implements Enumeration<String> {
        private Iterable<String> itable;
        private Iterator<String> it;

        public EnumerationAdapter(Iterable<String> itable) {
            this.itable = itable;
        }

        @Override
        public boolean hasMoreElements() {
            if (null == it) {
                this.it = itable.iterator();
            }

            return it.hasNext();
        }

        @Override
        public String nextElement() {
            if (null == it) {
                this.it = itable.iterator();
            }

            return it.next();
        }
    }
}
