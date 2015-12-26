package cn.fh.security.servlet;

import cn.fh.security.RequestConstrainManager;
import cn.fh.security.exception.InvalidXmlFileException;
import cn.fh.security.model.Config;
import cn.fh.security.utils.JsonLoader;
import cn.fh.security.utils.XmlLoader;
import com.alibaba.fastjson.JSONException;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 * Load page security configuration at startup.
 * <p> You can specify context parameter in {@code web.xml} to redefine the path to your own configuration file:
 * <p><ul>
 *     <li>STATIC_RESOURCE_PATH: This directory contains your static resources such as *.js, *.css, *.jpeg files.
 *     <li>SECURITY_CONFIG_PATH: The path to your own configuration xml file for this framework.
 * </ul>
 *
 * 在servlet容器启动时加载配置文件.
 * 
 * @author whf
 *
 */
public class PageProtectionContextListener implements ServletContextListener {
	public static Logger logger = LoggerFactory.getLogger(PageProtectionContextListener.class);
	public static RequestConstrainManager rcm;

	/**
	 * @deprecated
	 */
	private static final String NODE_ATTR_URL = "url";
	/**
	 * @deprecated
	 */
	private static final String NODE_ATTR_ROLE = "role";
	/**
	 * @deprecated
	 */
	private static final String NODE_ATTR_TO_URL = "to-url";
	/**
	 * @deprecated
	 */
	private static final String LOGIN_URL = "url";

    /**
     * The context parameter name for static resource path configured in web.xml
     */
	public static final String INIT_PARM_STATIC_RESOURCE_PATH = "STATIC_RESOURCE_PATH";
    public static final String INIT_PARM_SECURITY_CONFIG_PATH = "SECURITY_CONFIG_PATH";

    /**
     * The path of configuration file
     */
    public static String SECURITY_CONFIG_PATH = "/WEB-INF/security-config.xml";
    public static String[] STATIC_RESOURCE_PATHS = { "/resources" };

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Load and parse page security configuration
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
        loadContextParameter(event.getServletContext());

		if (logger.isDebugEnabled()) {
			for (String path : STATIC_RESOURCE_PATHS) {
				logger.debug("静态资源目录:{}", path);
			}
            logger.debug("security配置文件:{}", SECURITY_CONFIG_PATH);
            logger.debug("载入页面security配置文件");
		}

		// 加载JSON配置文件
		try {
			//Config config = JsonLoader.loadJson(event.getServletContext(), SECURITY_CONFIG_PATH);
            Config config = XmlLoader.loadXml(event.getServletContext(), SECURITY_CONFIG_PATH);
			PageProtectionContextListener.rcm = config.buildManager();
			PageProtectionContextListener.rcm.setLoginUrl(config.getLoginUrl());

		} catch (JSONException e) {
			e.printStackTrace();
			throw new IllegalStateException("JSON格式非法");

		} catch (DocumentException e) {
            e.printStackTrace();
            throw new InvalidXmlFileException("XML file syntax error");

        }

		logger.info("页面security载入完毕");

	}


	/**
	 * 从web.xml中读取容器上下文变量
	 * @param ctx
	 */
    private void loadContextParameter(ServletContext ctx) {
		// 静态资源目录变量
        String resourcePath = ctx.getInitParameter(INIT_PARM_STATIC_RESOURCE_PATH);
        if (null != resourcePath) {
			STATIC_RESOURCE_PATHS = resourcePath.split(":");
        }

		// 读取配置文件路径
        String configPath = ctx.getInitParameter(INIT_PARM_SECURITY_CONFIG_PATH);
        if (null != configPath) {
            SECURITY_CONFIG_PATH = configPath;
        }
    }
	
}
