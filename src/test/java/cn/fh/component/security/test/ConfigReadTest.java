package cn.fh.component.security.test;

import cn.fh.security.model.Config;
import cn.fh.security.utils.JsonLoader;
import cn.fh.security.utils.XmlLoader;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by wanghongfei on 15-6-24.
 */
public class ConfigReadTest {
    @Test
    public void test() throws Exception {
/*        List<RoleInfo> infoList = new ArrayList<>();
        infoList.add(new RoleInfo("/toUrl", "ADMIN", "DATA"));

        Config config = new Config(infoList, "/loginURL");
        String json = JSON.toJSONString(config);
        System.out.println(json);*/

        Config config = JsonLoader.loadJson(new FileInputStream("config.json"));
        //config.initialize();
        int a = 0;
    }

    @Test
    public void testXMLLoader() throws IOException, DocumentException {
        XmlLoader.loadXml(Thread.currentThread().getContextClassLoader().getResourceAsStream("security.xml"));
    }
}
