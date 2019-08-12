package xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 14899
 *
 */
public class XmlNode {
	
	private static final List<XmlNode> EMPTY_LIST;
	
	static {
		EMPTY_LIST = new ArrayList<XmlNode>(0);
	}
	
	private String name;//target name;
	private Map<String,String> attributes;
	private String data;
	private Map<String,List<XmlNode>> children;
	
	public XmlNode(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String getData() {
		return data;
	}
	public XmlNode setData(String data) {
		this.data = data;
		return this;
	}
	public XmlNode addAttribute(String att,String value) {
		if(null == attributes)
			attributes = new HashMap<String,String>();
		
		attributes.put(att, value);
		return this;
	}
	public String getAttribute(String att) {
		if(null != attributes)
			return attributes.get(att);
		
		return null;
	}
	
	public XmlNode addChild(XmlNode child) {
		if(null == children)
			children = new HashMap<String,List<XmlNode>>();
		
		List<XmlNode> list = children.get(child.name);
		if(null == list) {
			list = new LinkedList<XmlNode>();
			children.put(child.name, list);
		}
		list.add(child);
		return this;
	}
	public List<XmlNode> getChildren(String name){
		if(null != children) {
			List<XmlNode> children = this.children.get(name);
			if(null != children) 
				return children;
		}
		return EMPTY_LIST;
	}
	public XmlNode getChild(String name) {
		List<XmlNode> children = this.getChildren(name);
		if(children == EMPTY_LIST)
			return null;
		else
			return children.get(0);
	}
	public XmlNode getChildWithAttribute(String childName,String attr,String value) {
		List<XmlNode> children = this.getChildren(childName);
		if(null == children || children.isEmpty())
			return null;
		
		for(XmlNode child:children) {
			String val = child.getAttribute(attr);
			if(value.equals(val))
				return child;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("NAME:%s,DATA:%s", name,data);
	}

}
