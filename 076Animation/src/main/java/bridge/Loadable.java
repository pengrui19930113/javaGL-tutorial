package bridge;

import data.JointData;
import data.KeyFrameData;
import data.MeshData;

public interface Loadable {
	JointData getRootJointData();
	int getJointCount();
	MeshData getMeshData();
	float getAnimationTime();
	KeyFrameData[] getKeyFrameDatas();
	
//	default void sayHello() {
//		System.out.println("hello");
//	}
}
