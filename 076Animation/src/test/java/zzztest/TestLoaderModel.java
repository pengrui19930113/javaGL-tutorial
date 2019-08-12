package zzztest;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import bridge.Loadable;
import data.JointData;
import data.KeyFrameData;
import data.MeshData;
import loader.ColladaLoader;

public class TestLoaderModel {
	//Chest
	static String[] datas = {
			"1 0 0 0 0 -0.06466547 -0.997907 0 0 0.997907 -0.06466556 3.210999 0 0 0 1"
			,"1 0 4.76837e-7 0 -7.13595e-8 0.9887388 0.1496516 1.482405 -4.71468e-7 -0.1496516 0.9887388 0 0 0 0 1"
			,"-1 -1.44018e-7 6.05564e-7 1.42109e-14 4.5605e-8 0.9532991 0.3020283 1.29516 -6.20781e-7 0.3020284 -0.9532989 2.98023e-8 0 0 0 1"
			,"1 2.99489e-8 -2.48448e-7 -1.42109e-14 -8.79698e-8 0.9715177 -0.2369666 0.3023291 2.34275e-7 0.2369666 0.9715177 0 0 0 0 1"
	};
	//Upper_Leg_L
	static String[] datas2 = {
			"1 0 0 0 0 -0.06466547 -0.997907 0 0 0.997907 -0.06466556 3.210999 0 0 0 1"
			,"-0.9999336 0.01149671 -8.33571e-4 0.5668193 -0.01149641 -0.9893998 0.1447591 -0.001348972 8.38965e-4 0.1447601 0.9894629 -0.1043749 0 0 0 1"
			,"-0.9991559 0.04105417 -0.001461088 -5.96046e-8 0.04052995 0.9793479 -0.1980785 1.282774 -0.006701034 -0.1979705 -0.980185 -1.49012e-8 0 0 0 1"
			,"-0.999561 0.008794559 -0.02829164 5.96046e-8 0.02962703 0.296683 -0.9545164 1.493334 -9.03878e-7 -0.9549353 -0.2968133 -1.49012e-8 0 0 0 1"
	};
	//Upper_Leg_R
	static String[] datas3 = {
			"1 0 0 0 0 -0.06466547 -0.997907 0 0 0.997907 -0.06466556 3.210999 0 0 0 1"
			,"-0.9999335 -0.01149671 -8.34063e-4 -0.5668193 0.01125496 -0.9893999 0.1447781 -0.001348972 -0.002489135 0.1447601 0.9894602 -0.1043749 0 0 0 1"
			,"-0.9991658 -0.04072399 0.003094593 0 -0.04052972 0.979348 -0.1980785 1.282774 0.005035869 -0.1980387 -0.9801812 2.98023e-8 0 0 0 1"
			,"-0.9995611 -0.008793429 0.02829201 -5.96046e-8 -0.02962703 0.2966829 -0.9545164 1.493333 -2.82292e-7 -0.9549352 -0.2968132 1.49012e-8 0 0 0 1"
	};
	
	static String[] targetData = datas3;
	static Matrix4f[] matrixs = new Matrix4f[targetData.length];
	public static void main(String[] args) {
		
		for(int i=0;i<targetData.length;i++) {
			String[] raw = targetData[i].split(" ");
			float[] floatData = new float[16];
			for(int j=0;j<16;j++) {
				floatData[j] = Float.parseFloat(raw[j]);
			}
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
			buf.put(floatData);
			buf.flip();
			Matrix4f tmp = new Matrix4f();
			tmp.load(buf);
			tmp.transpose();
			matrixs[i] = tmp;
		}
		
//		for(int i=0;i<matrixs.length;i++) {
//			System.out.println(matrixs[i]);
//		}
		Vector4f position = new Vector4f(0,0,0,1);
		for(int i=0;i<matrixs.length;i++) {
			Matrix4f.transform(matrixs[i], position, position);
//			Vector4f tmp = new Vector4f(position);
//			tmp.scale(1.f/tmp.w);
			System.out.println(position);
		}
		
	}
	
	static void testLoader() {
		Loadable loader = new ColladaLoader("xml/model.dae",3); 
		JointData jd = loader.getRootJointData();
		int jc = loader.getJointCount();
		MeshData md = loader.getMeshData();
		float at = loader.getAnimationTime();
		KeyFrameData[] kfds = loader.getKeyFrameDatas();
		
		System.out.println(jd.nameId);
		System.out.println(jc);
		System.out.println(md.indices.length);
		System.out.println(at);
		System.out.println(kfds.length);
	}
}
