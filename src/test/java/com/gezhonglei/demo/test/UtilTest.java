package com.gezhonglei.demo.test;

import java.io.IOException;

import org.dom4j.DocumentException;
import org.junit.Test;

import com.gezhonglei.commons.util.JsonUtils;
import com.gezhonglei.commons.xml.BaseException;
import com.gezhonglei.commons.xml.XmlUtil;
import com.gezhonglei.demo.entity.RestConfig;

public class UtilTest {
	@Test
	public void testPath() throws DocumentException, BaseException, IOException {
		String path = UtilTest.class.getResource("/").getPath() + "restclient.xml";
		RestConfig config = XmlUtil.getEntity(path, RestConfig.class);
		String json = JsonUtils.toJSONString(config);
		System.out.println(JsonUtils.format(json, "  "));
		
		String xmlstr = XmlUtil.marshal(config);
		System.out.println(xmlstr);
		System.out.println("----------------");
		System.out.println(XmlUtil.format(xmlstr));
	}
}
