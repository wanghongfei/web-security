package cn.fh.security.utils;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtils {
	private ResponseUtils() {
	}
	
	/**
	 * 返回权限不足错误信息，关闭输出流
	 * @param resp
	 * @throws IOException
	 */
	public static void responseBadRole(HttpServletResponse resp) throws IOException {
		OutputStream out = resp.getOutputStream();
		
		resp.setContentType("text/plain");
		resp.setStatus(403);
		
		out.write("bad role".getBytes());
		out.close();
	}
}
