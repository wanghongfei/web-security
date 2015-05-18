package cn.fh.security.utils;

import cn.fh.security.RoleInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class ResponseUtils {
	private ResponseUtils() {
	}
	
	/**
	 * Return error information and close OutputStream
	 * @param resp
	 * @throws IOException
	 */
	public static void responseBadRole(HttpServletResponse resp, RoleInfo rInfo) throws IOException {
		OutputStream out = resp.getOutputStream();
		
		// redirect to error url
		if (false == rInfo.getToUrl().isEmpty()) {
			resp.sendRedirect(rInfo.getToUrl());
			return;
		}
		
		resp.setContentType("text/plain");
		resp.setStatus(403);
		
		out.write("bad role".getBytes());
		out.close();
	}

	public static void sendRedirect(HttpServletResponse resp, String loginUrl) throws IOException {
		resp.sendRedirect(loginUrl);
	}
}
