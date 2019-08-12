package animation;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

/**
 * 关键帧
 * @author 14899
 *
 */
public class KeyFrame {
	
	/**
	 * 一段动画中的关键帧  的帧时间  例如 一段动画由  0	0.2	0.5 0.6 共4帧  则 time = 其中一个值  一个 Animation 中包含4个帧
	 */
	public final float time;
	/**
	 * 骨骼空间 该关键帧中所有关节的位置及其旋转信息
	 * 
	 * key: 	该关节的 nameId 
	 * value:	旋转和位移信息(骨骼空间)
	 */
	private final Map<String,JointTransform> pose;
	
	public KeyFrame(float time, Map<String, JointTransform> pose) {
		this.time = time;
		this.pose = pose;
	}
	
	/**
	 * 关键帧之间插值求取 骨骼空间所有关节的变换矩阵
	 * @param previousFrame
	 * @param nextFrame
	 * @param progression
	 * @return
	 */
	public static Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.pose.keySet()) {
			JointTransform previousTransform = previousFrame.pose.get(jointName);
			JointTransform nextTransform = nextFrame.pose.get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}

}
