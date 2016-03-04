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
	

	public static void sendRedirect(HttpServletResponse resp, String loginUrl) throws IOException {
		resp.sendRedirect(loginUrl);
	}

    public static void sendErrorMessage(HttpServletResponse resp, String msg, int code) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("message", msg);
        //map.put("result", "false");
        map.put("code", code);
        map.put("data", "");

        JSONObject jsonObject = new JSONObject(map);
        String json = jsonObject.toJSONString();

        resp.setStatus(403);
        resp.setContentType("application/json");
        resp.getOutputStream().write(json.getBytes());
        resp.getOutputStream().close();

    }


}
