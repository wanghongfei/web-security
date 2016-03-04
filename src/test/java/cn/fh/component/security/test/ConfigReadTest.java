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
    public void testXMLLoader() throws IOException, DocumentException {
        XmlLoader.loadXml(Thread.currentThread().getContextClassLoader().getResourceAsStream("security.xml"));
    }
}
