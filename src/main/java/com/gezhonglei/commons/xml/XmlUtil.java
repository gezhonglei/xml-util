package com.gezhonglei.commons.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Xml工具
 * 
 * <p>
 * <ul>
 * <li><b>xml序列与反序化</b>：xml解析为实体，实体转换为xml字符串。</li>
 * <li><b>xml格式化</b>：使xml缩进排版易读。</li>
 * <li><b>xml转义与转义处理</b></li>
 * </ul>
 * </p>
 *
 */
public final class XmlUtil {

	//private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

	public static <T> T unmarshal(String xmlStr, Class<T> clazz) throws DocumentException, BaseException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new StringReader(xmlStr));
		Element element = doc.getRootElement();
		return new Parser().parse(element, clazz, null, false);
	}

	public static String marshal(Object object) {
		return new XmlSerializer().serialize(object);
	}

	public static <T> T getEntity(String path, Class<T> clazz)
			throws DocumentException, BaseException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(path);
		Element element = doc.getRootElement();
		return new Parser().parse(element, clazz, null, false);
	}

	public static String format(String str) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new StringReader(str));
		
		OutputFormat formater = OutputFormat.createPrettyPrint();
		//OutputFormat formater=OutputFormat.createCompactFormat();
		formater.setEncoding("utf-8");

		StringWriter out = new StringWriter();
		XMLWriter writer = new XMLWriter(out, formater);
		writer.write(doc);
		writer.close();
		// 注释：返回我们格式化后的结果
		return out.toString();
	}

	public static void main(String[] args) throws FileNotFoundException,
			DocumentException {

		SAXReader reader = new SAXReader();
		Document doc = reader.read("");
		// XPath xPath = new
		// DefaultXPath("/resources/product[@name='QQ']/account[@id='987654321']/password");
		// List<Element> list = xPath.selectNodes(doc.getRootElement());
		Element root = doc.getRootElement();
		// System.out.println(root.asXML());
		System.out.println(root.getName());
		// System.out.println(root.getData());
		System.out.println(root.getNodeTypeName());
		@SuppressWarnings("unchecked")
		List<Element> list = root.elements();
		for (Element element : list) {
			System.out.println(element.getName() + "=" + element.getTextTrim());
		}
	}

	public static String escape(String str) {
		return StringEscapeUtils.escapeXml(str);
	}
	
	public static String unescap(String str) {
		return StringEscapeUtils.unescapeXml(str);
	}
}
