package data;

import org.lwjgl.util.vector.Matrix4f;

public class JointTransformData {
	
	public String jointNameId;
	public Matrix4f jointLocalTransform;
	
	public JointTransformData(String jointNameId, Matrix4f jointLocalTransform) {
		this.jointNameId = jointNameId;
		this.jointLocalTransform = jointLocalTransform;
	}
}
