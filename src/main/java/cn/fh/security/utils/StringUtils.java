package cn.fh.security.utils;

public class StringUtils {
private StringUtils() {}
	
	/**
	 * "apple" --> apple
	 * @param str
	 * @return
	 */
	public static String trimQuotation(String str) {
		return str.substring(1, str.length() - 1);
	}
	
	/**
	 * e.g.. from "/backstage/manage" to "/backstage", from "/admin/user/add" to "/admin/user"
	 * @param url
	 * @return
	 */
	public static String trimLastUrlToken(String url) {
		int lastSplash = url.lastIndexOf('/');
		return url.substring(0, lastSplash);
	}
	
	/**
	 * If the context name is '/', this method does nothing.
	 * If the context name is something else like '/shop', this method removes this string.
	 * <p> e.g.. /shop/buy --> /buy
	 * 
	 * @param contextPath
	 * @param uri
	 * @return
	 */
	public static String trimContextFromUrl(String contextPath, String uri) {
		String url = null;

		if (false == "/".equals(contextPath)) {
			int ctxLen = contextPath.length();
			url = uri.substring(ctxLen);
		} else {
			url = uri;
		}
		
		return url;
	}
}
