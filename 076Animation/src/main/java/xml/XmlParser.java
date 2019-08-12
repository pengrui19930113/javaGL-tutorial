package xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该解析器只能解析：
 *  1	<s>标签</s> <s>属性</s> <s>数据</s> 在一行
 * 	2	标签名于属性 属性与属性之间的间隔只有一个空格
 * 的xml文件
 * dae 文件是这种规范
 * @author 14899
 *
 */
public class XmlParser {
	private static final Pattern DATA = Pattern.compile(">(.+?)<");
	private static final Pattern START_TAG = Pattern.compile("<(.+?)>");
	private static final Pattern ATTR_NAME = Pattern.compile("(.+?)=");
	private static final Pattern ATTR_VAL = Pattern.compile("\"(.+?)\"");
	private static final Pattern CLOSE = Pattern.compile("(</|/>)");
	
	public XmlNode loadXmlFile(String filePath) {
		if(null == filePath) throw new NullPointerException();
		BufferedReader reader = null;
		XmlNode root = null;
		try {
			reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filePath)));
			reader.readLine();
			root = loadNode(reader);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} finally {
			if(null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return root;
	}

	private XmlNode loadNode(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		if(null == line) return null;		//学习的源码中没有做该检擦 因为正常的dae 不会为null 
		line = line.trim();
		if(line.startsWith("</")) // 如果是单标签 直接忽略
			return null;
		// match start tag
		Matcher match = START_TAG.matcher(line);
		match.find();
		String startTagParts[] = match.group(1).split(" ");
		XmlNode node = new XmlNode(startTagParts[0]);// 学习的代码中 还做了一个replace的动作 		XmlNode node = new XmlNode(startTagParts[0].replace("/", ""));
		// add  attributes
		for(int i=1;i< startTagParts.length;i++) {
			if(startTagParts[i].contains("=")) {
				String currentLine = startTagParts[i];
				Matcher nameMatch = ATTR_NAME.matcher(currentLine);
				nameMatch.find();
				Matcher valMatch = ATTR_VAL.matcher(currentLine);
				valMatch.find();
				node.addAttribute(nameMatch.group(1), valMatch.group(1));
			}
		}
		// add data
		Matcher dataMatcher = DATA.matcher(line);
		if(dataMatcher.find()) {
			node.setData(dataMatcher.group(1));
		}
		// detect close
		if(CLOSE.matcher(line).find()) {
			return node;
		}
		// add children
		XmlNode child;
		while(null!=(child = loadNode(reader))) {
			node.addChild(child);
		}
		return node;
	}
}
