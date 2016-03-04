package cn.fh.security.utils;

import cn.fh.security.exception.ConfigFileNotFoundException;
import cn.fh.security.exception.InvalidXmlFileException;
import cn.fh.security.model.Config;
import cn.fh.security.model.RoleInfo;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * XML配置文件读取器
 * Created by whf on 12/26/15.
 */
public class XmlLoader {
    private static Logger log = LoggerFactory.getLogger(XmlLoader.class);

    /**
     * 从servlet上下文中读取xml配置
     * @param ctx
     * @param path
     * @return
     * @throws DocumentException
     */
    public static Config loadXml(ServletContext ctx, String path) throws DocumentException {
        SAXReader xmlReader = new SAXReader();

        InputStream in = ctx.getResourceAsStream(path);
        if (null == in) {
            throw new ConfigFileNotFoundException(path + " doesn't exist");
        }

        Document doc = xmlReader.read(in);

        return doParse(doc);
    }

    /**
     * 从classpath中读取xml配置文件
     * @param classpath
     * @return
     */
    public static Config loadXml(String classpath) throws DocumentException {
        String path = classpath.substring(classpath.indexOf(':') + 1);

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (null == in) {
            throw new ConfigFileNotFoundException(path + " doesn't exist");
        }

        return loadXml(in);
    }

    /**
     * 直接从输入流中读取xml配置
     * @param inStream
     * @return
     * @throws DocumentException
     */
    public static Config loadXml(InputStream inStream) throws DocumentException {
        SAXReader xmlReader = new SAXReader();
        Document doc = xmlReader.read(inStream);

        return doParse(doc);

    }

    private static Config doParse(Document doc) throws InvalidXmlFileException {
        Config config = new Config();

        Element root = doc.getRootElement();

        // 读取登陆页面节点
        Element loginNode = root.element("login-page");
        if (null != loginNode) {
            String loginPage = loginNode.getText();
            config.setLoginUrl(loginPage);

            log.info("login page = {}", loginPage);
        }

        // 读取auth-error-redirect
        Element authNode = root.element("auth-error-redirect");
        if (null != authNode) {
            String authPage = authNode.getText();
            config.setAuthErrorRedirect(authPage);

            log.info("authentication redirect page = {}", authPage);
        }

        // 读取auth-error-forward
        Element forwardNode = root.element("auth-error-forward");
        if (null != forwardNode) {
            String authPage = forwardNode.getText();
            config.setAuthErrorForward(authPage);

            log.info("authentication forward page = {}", authPage);
        }

        // 读取拦截规则
        Element ruleElem = root.element("rules");
        if (null != ruleElem) {
            List<Element> ruleList = ruleElem.elements();

            // 遍历规则节点
            List<RoleInfo> roleList = new ArrayList<>(ruleList.size());
            for (Element elem : ruleList) {
                // 读取属性
                Attribute pathAttr = elem.attribute("path");
                nullCheck(pathAttr, "path attribute cannot be empty");
                String path = pathAttr.getValue();

                Attribute roleAttr = elem.attribute("roles");
                nullCheck(roleAttr, "roles attribute cannot be empty");
                String roles = roleAttr.getValue();

                String method = null;
                Attribute methodAttr = elem.attribute("method");
                if (null == methodAttr) {
                    method = "GET";
                } else {
                    method = methodAttr.getValue();
                }

                RoleInfo info = new RoleInfo();
                List<String> rList = parseRoleList(roles);
                info.setRoleList(rList);
                info.setUrl(path);
                info.setToUrl(config.getLoginUrl());

                roleList.add(info);

                log.info("rule found: {} {} -> {}", method, path, rList);
            }
            config.setInterceptRule(roleList);
        }

        return config;
    }

    private static List<String> parseRoleList(String str) {
        if (null == str || str.isEmpty()) {
            throw new InvalidXmlFileException("invalid role attribute");
        }

        return Arrays.asList( str.split(" ") );
    }

    private static void nullCheck(Object o, String msg) {
        if (null == o) {
            throw new InvalidXmlFileException(msg);
        }
    }
}
