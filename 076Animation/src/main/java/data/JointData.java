package data;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class JointData {
	public int index;
	public String nameId;
	public Matrix4f bindLocalTransform;
	public List<JointData> children;
	public JointData(int index,String nameId,Matrix4f localTransform) {
		this.index = index;
		this.nameId = nameId;
		this.bindLocalTransform = localTransform;
		this.children = new ArrayList<JointData>();
	}
	
	public void addChild(JointData child) {
		this.children.add(child);
	}
}
