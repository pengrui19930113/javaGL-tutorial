package animation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

/**
 * 关节节点
 * @author 14899
 *
 */
public class Joint {
	
	public int index;
	public String name;
	public List<Joint> children;
	public final Matrix4f localBindTransform;//骨骼空间变换矩阵
	public Matrix4f inverseBindTransform;//变换到模型空间参考点的逆矩阵(旋转节点在模型空间的原点)
	public Matrix4f animatedTransform;
	
	public Joint(int index,String name,Matrix4f localBindTransform) {
		this.index = index;
		this.name = name;
		this.localBindTransform = localBindTransform;
		this.children = new ArrayList<Joint>();
		this.inverseBindTransform = new Matrix4f();
		this.animatedTransform = new Matrix4f();
	}
	public void addChild(Joint child) {
		this.children.add(child);
	}
	
	public Joint calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, localBindTransform, null);
		Matrix4f.invert(bindTransform, inverseBindTransform);
		for (Joint child : children) {
			child.calcInverseBindTransform(bindTransform);
		}
		return this;
	}
}
