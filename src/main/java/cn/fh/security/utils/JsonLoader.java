package cn.fh.security.utils;

import cn.fh.security.model.Config;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wanghongfei on 15-6-24.
 */
public class JsonLoader {
    private JsonLoader() {}

    /**
     * 从Servlet上下文中加载JSON配置
     * @param ctx
     * @param path
     * @return
     * @throws IOException
     */
    public static Config loadJson(ServletContext ctx, String path) throws IOException {
        InputStream in = ctx.getResourceAsStream(path);
        String json = stream2String(in);

        return JSON.parseObject(json, Config.class);
    }

    /**
     * 从流中读取JSON配置
     * @param in
     * @return
     * @throws IOException
     */
    public static Config loadJson(InputStream in) throws IOException {
        String json = stream2String(in);

        return JSON.parseObject(json, Config.class);

    }


    private static String stream2String(InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);
        char[] buf = new char[500];
        int len = 0;
        StringBuilder sb = new StringBuilder();
        while ( (len = reader.read(buf)) != -1 ) {
            sb.append(buf, 0, len);
        }

        return sb.toString();
    }
}
