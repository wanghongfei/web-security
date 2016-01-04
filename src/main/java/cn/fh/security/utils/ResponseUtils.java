package cn.fh.security.utils;

import cn.fh.security.model.RoleInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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

    public static void sendErrorMessage(HttpServletResponse resp) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "PERMISSION_ERROR");
        map.put("result", "false");
        map.put("code", 13);
        map.put("data", "");

        JSONObject jsonObject = new JSONObject(map);
        String json = jsonObject.toJSONString();

        resp.setStatus(403);
        resp.setContentType("application/json");
        resp.getOutputStream().write(json.getBytes());
        resp.getOutputStream().close();

    }


}
