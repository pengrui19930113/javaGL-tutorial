package data;

import java.util.ArrayList;
import java.util.List;

public class KeyFrameData {
	public float time;// keyFrame time; 关键帧时间点
	public List<JointTransformData> jointTransforms;// 该关键帧时间点 所有关节的变换矩阵
	
	public KeyFrameData(float time) {
		this.time = time;
		this.jointTransforms = new ArrayList<JointTransformData>();
	}
	
	public void addJointTransform(JointTransformData transform) {
		jointTransforms.add(transform);
	}
}
