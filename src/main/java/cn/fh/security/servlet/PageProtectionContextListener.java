package cn.fh.security.servlet;

import cn.fh.security.RequestConstrainManager;
import cn.fh.security.RoleInfo;
import cn.fh.security.exception.InvalidXmlFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Load page security configuration at startup.
 * <p> You can specify context parameter in {@code web.xml} to redefine the path to your own configuration file:
 * <p><ul>
 *     <li>STATIC_RESOURCE_PATH: This directory contains your static resources such as *.js, *.css, *.jpeg files.
 *     <li>SECURITY_CONFIG_PATH: The path to your own configuration xml file for this framework.
 * </ul>
 * 
 * @author whf
 *
 */
public class PageProtectionContextListener implements ServletContextListener {
	public static Logger logger = LoggerFactory.getLogger(PageProtectionContextListener.class);
	public static RequestConstrainManager rcm;
	
	private static final String NODE_ATTR_URL = "url";
	private static final String NODE_ATTR_ROLE = "role";
	private static final String NODE_ATTR_TO_URL = "to-url";

	private static final String LOGIN_URL = "url";

    /**
     * The context parameter name for static resource path configured in web.xml
     */
	public static final String INIT_PARM_STATIC_RESOURCE_PATH = "STATIC_RESOURCE_PATH";
    public static final String INIT_PARM_SECURITY_CONFIG_PATH = "SECURITY_CONFIG_PATH";

    /**
     * The path of configuration file
     */
    public static String SECURITY_CONFIG_PATH = "/WEB-INF/security-page.xml";
    public static String STATIC_RESOURCE_PATH = "/resources";

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
			logger.debug("静态资源目录:{}", STATIC_RESOURCE_PATH);
            logger.debug("security配置文件:{}", SECURITY_CONFIG_PATH);
            logger.debug("载入页面security配置文件");
		}


		try {
			// load xml file
			Document doc = getXmlDocumentObject(event.getServletContext());
			Element root = doc.getDocumentElement();

			if (false == "page".equals(root.getTagName())) {
				throw new InvalidXmlFileException("标签<" + root.getTagName()
						+ ">不存在");
			}

			// start analysis
			PageProtectionContextListener.rcm = parseConstrain(root);

		} catch (SAXException e) {
			e.printStackTrace();
			throw new InvalidXmlFileException("xml语法错误");
		} catch (ParserConfigurationException e) {
			throw new InvalidXmlFileException("xml解析出错");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("页面security载入完毕");
		}

	}

    private void loadContextParameter(ServletContext ctx) {
        String resourcePath = ctx.getInitParameter(INIT_PARM_STATIC_RESOURCE_PATH);
        if (null != resourcePath) {
            STATIC_RESOURCE_PATH = resourcePath;
        }

        String configPath = ctx.getInitParameter(INIT_PARM_SECURITY_CONFIG_PATH);
        if (null != configPath) {
            SECURITY_CONFIG_PATH = configPath;
        }
    }
	
	/**
	 * perform parse for xml document
	 * @param root The root element of this document
	 * @return
	 */
	private RequestConstrainManager parseConstrain(Element root) {
		//Map<String, List<String>> map = new HashMap<String, List<String>>();
		RequestConstrainManager manager = new RequestConstrainManager();
		
		if (root.hasChildNodes()) {
			NodeList nodes = root.getChildNodes();
			for (int ix = 0 ; ix < nodes.getLength() ; ++ix) {
				Node node = nodes.item(ix);
				if (node instanceof Element) {
					Element tag = (Element) node;
					
					// check tag name
					String tagName = tag.getTagName();
					if (false == "request".equals(tagName) && false == "login-page".equals(tagName)) {
						throw new InvalidXmlFileException("标签<" + tag.getTagName() + ">不存在");
					}

					// 这个标签是用来指定登陆页面URL的
					// 该标签只会出现一次
					if ("login-page".equals(tagName)) {
						String url = tag.getAttribute(LOGIN_URL);
						if (null == url || url.isEmpty()) {
							throw new InvalidXmlFileException("标签<" + tagName + ">缺少'" + LOGIN_URL + "'属性");
						}
						this.rcm.setLoginUrl(url);

						if (logger.isDebugEnabled()) {
							logger.debug("登陆页面url:{}", url);
						}

						continue;
					}
					
					// check the existence of tag attributes
					String url = tag.getAttribute(NODE_ATTR_URL);
					String roles = tag.getAttribute(NODE_ATTR_ROLE);
					// to-url is optional
					String toUrl = tag.getAttribute(NODE_ATTR_TO_URL);
					// not enough attributes, throw runtime exception
					if (url.isEmpty()) {
						throw new InvalidXmlFileException("标签<" + tag.getTagName() + ">缺少'" + NODE_ATTR_URL + "'属性");
					}
					if (roles.isEmpty()) {
						throw new InvalidXmlFileException("标签<" + tag.getTagName() + ">缺少'" + NODE_ATTR_ROLE + "'属性");
					}
					
					//List<String> roleList = new ArrayList<String>();
					String[] roleArray = roles.split(" ");
					
					if (logger.isDebugEnabled()) {
						// traverse and print all roles
						Arrays.stream(roleArray).forEach( (roleName) -> {
							logger.debug("找到role限制:<" + url + "> : " + roleName);
						});
					}
					
/*					for (String r : roleArray) {
						roleList.add(r);
						
						if (logger.isDebugEnabled()) {
							logger.debug("找到role限制:<" + url + "> : " + r);
						}
					}*/
					
					
					manager.put(url, new RoleInfo(toUrl, roleArray));
				}
			}
		}
		
		return manager;
	}
	
	private Document getXmlDocumentObject(ServletContext ctx) throws SAXException, IOException, ParserConfigurationException {
		InputStream in = ctx.getResourceAsStream(PageProtectionContextListener.SECURITY_CONFIG_PATH);
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
	}

}
