package animation;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import bridge.Loadable;
import data.JointTransformData;

public class Animation {

	public final float length;// 秒为单位
	public final KeyFrame[] keyframes;
	
	public Animation(Loadable loader) {
		this.length = loader.getAnimationTime();
		this.keyframes = new KeyFrame[loader.getKeyFrameDatas().length];
		
		for (int i = 0; i < keyframes.length; i++) {
			Map<String, JointTransform> map = new HashMap<String, JointTransform>();
			for (JointTransformData jointData : loader.getKeyFrameDatas()[i].jointTransforms) {
				Matrix4f mat = jointData.jointLocalTransform;
				Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
				Quaternion rotation = Quaternion.fromMatrix(mat);
				JointTransform jointTransform = new JointTransform(translation, rotation);
				map.put(jointData.jointNameId, jointTransform);
			}
			keyframes[i] =  new KeyFrame(loader.getKeyFrameDatas()[i].time, map);
			
//			this.keyframes[i] = createKeyFrame(loader.getKeyFrameDatas()[i]);
		}
	}

//	private static KeyFrame createKeyFrame(KeyFrameData data) {
//		Map<String, JointTransform> map = new HashMap<String, JointTransform>();
//		for (JointTransformData jointData : data.jointTransforms) {
//			JointTransform jointTransform = createTransform(jointData);
//			map.put(jointData.jointNameId, jointTransform);
//		}
//		return new KeyFrame(data.time, map);
//	}
//	
//	private static JointTransform createTransform(JointTransformData data) {
//		Matrix4f mat = data.jointLocalTransform;
//		Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
//		Quaternion rotation = Quaternion.fromMatrix(mat);
//		return new JointTransform(translation, rotation);
//	}
}
