package zzztest;

import xml.XmlNode;
import xml.XmlParser;

public class TestLoaderXml {
	public static void main(String args[]) {
		
		XmlNode root = new XmlParser().loadXmlFile("model/model.dae");
		System.out.println(root);
	}
}
