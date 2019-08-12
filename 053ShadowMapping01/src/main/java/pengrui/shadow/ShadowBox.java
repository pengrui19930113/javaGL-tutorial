package pengrui.shadow;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import pengrui.common.Camera;
import pengrui.common.Light;
import pengrui.util.MatrixUtil;

import static pengrui.config.CommonConstant.*;


public class ShadowBox {
	
	public static final float SHADOW_DISTANCE = 30;
	/**
	 * lightViewMatrix 中的 shadowBox的边界
	 */
	public float xMin,xMax,yMin,yMax,zMin,zMax;
	
	public final float farHeight,farWidth,nearHeight,nearWidth;
	
	public final Matrix4f orthoProjectionMatrix,lightViewMatrix,pvMatrix;
	
	public Vector3f virtualLightPosition;
	
	public ShadowBox(Light light) {
		farHeight = (float) (2*Math.tan(Math.toRadians(MatrixUtil.FOV/2.0))*SHADOW_DISTANCE);
		nearHeight = (float) (2*Math.tan(Math.toRadians(MatrixUtil.FOV/2.0))*MatrixUtil.NEAR);;
		farWidth = farHeight*WIDTH/HEIGHT;
		nearWidth = nearHeight*WIDTH/HEIGHT;
		orthoProjectionMatrix = new Matrix4f();
		lightViewMatrix= new Matrix4f();
		pvMatrix = new Matrix4f();
		this.updateLightViewMatrix(light,new Vector3f(0,0,-SHADOW_DISTANCE/2));
	}
	
	private void updateLightViewMatrix(Light light,Vector3f center) {
		Vector3f back = (Vector3f)new Vector3f(light.xPos,light.yPos,light.zPos).normalise(); //Z
		Vector3f up = new Vector3f(
				Matrix4f.transform(
						Matrix4f.rotate(
								-(float) Math.atan(light.yPos/Math.sqrt(light.xPos*light.xPos+light.zPos*light.zPos))
								, new Vector3f(1,0,0)
								, new Matrix4f()
								, null
						)
				, new Vector4f(0,1,0,0)
				, null
				)
		);
		Vector3f right = Vector3f.cross(up, back, null);//X
		virtualLightPosition = Vector3f.add(center,(Vector3f)new Vector3f(light.xPos,light.yPos,light.zPos).normalise()
				.scale(SHADOW_DISTANCE/2)
				,null);
		
//		System.out.println(back.length());
//		System.out.println(up.length());
//		System.out.println(right.length());
		lightViewMatrix.m00 = right.x;
		lightViewMatrix.m10 = right.y;
		lightViewMatrix.m20 = right.z;
		lightViewMatrix.m30 = 0;
		lightViewMatrix.m01 = up.x;
		lightViewMatrix.m11 = up.y;
		lightViewMatrix.m21 = up.z;
		lightViewMatrix.m31 = 0;
		lightViewMatrix.m02 = back.x;
		lightViewMatrix.m12 = back.y;
		lightViewMatrix.m22 = back.z;
		lightViewMatrix.m32 = 0;
		lightViewMatrix.m03 = virtualLightPosition.x;
		lightViewMatrix.m13 = virtualLightPosition.y;
		lightViewMatrix.m23 = virtualLightPosition.z;
		lightViewMatrix.m33 = 1;
		lightViewMatrix.transpose();
		lightViewMatrix.invert();

	}
	
	private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction,
			float width) {
		width /= 2;
		Vector3f point = Vector3f.add(startPoint,
				new Vector3f(direction.x * width, direction.y * width, direction.z * width), null);
		Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
		Matrix4f.transform(lightViewMatrix, point4f, point4f);
		return point4f;
	}
	
	static final Vector4f FORWARD = new Vector4f(0,0,-1,0);
	static final Vector4f UP = new Vector4f(0,1,0,0);
	static final float OFFSET = 10;
	public void update(Camera camera,Light light,long delta) {
		Matrix4f cameraRotation = MatrixUtil.calculateCameraRotation(camera);
		Vector3f forwardVector = new Vector3f(Matrix4f.transform(cameraRotation, FORWARD, null));

		Vector3f toFar = (Vector3f)new Vector3f(forwardVector).scale(ShadowBox.SHADOW_DISTANCE);
		Vector3f toNear = (Vector3f)new Vector3f(toFar).scale(MatrixUtil.NEAR);
		Vector3f centerNear = Vector3f.add(toNear, new Vector3f(camera.xPos,camera.yPos,camera.zPos), null);
		Vector3f centerFar = Vector3f.add(toFar, new Vector3f(camera.xPos,camera.yPos,camera.zPos), null);
		if(Keyboard.isKeyDown(Keyboard.KEY_P) && System.currentTimeMillis()-lastTime >1000) {
			lastTime = System.currentTimeMillis();
//			System.out.println(String.format("x[%s,%s] y[%s,%s] z[%s,%s]", xMin,xMax,yMin,yMax,zMin,zMax));
//			for(Vector4f v4:points) {
//				System.out.println(v4);
//			}
//			System.out.println("visual position:"+virtualLightPosition);
//			System.out.println("camera position:"+camera);
			System.out.println(forwardVector);
			System.out.println(toFar);
			System.out.println();
		}
//		cameraRotation	forwardVector	centerNear	centerFar
		Vector3f upVector = new Vector3f(Matrix4f.transform(cameraRotation, UP, null));//相机空间的一个子集  因为使用的远平面 不是 FAR 而是SHADOW_DISTANCE 
		Vector3f rightVector = Vector3f.cross(forwardVector, upVector, null);// 光线的视觉空间的 right 基
//		Vector3f downVector = null; 
		Vector3f leftVector = (Vector3f)new Vector3f(rightVector).scale(-1);
		
		Vector3f farTop = Vector3f.add(centerFar, (Vector3f)new Vector3f(upVector).scale(farHeight), null);
		Vector3f farBottom = Vector3f.add(centerFar, (Vector3f)new Vector3f(-upVector.x,-upVector.y,-upVector.z).scale(farHeight), null);
		Vector3f nearTop = Vector3f.add(centerNear, (Vector3f)new Vector3f(upVector).scale(nearHeight), null);
		Vector3f nearBottom = Vector3f.add(centerNear, (Vector3f)new Vector3f(-upVector.x,-upVector.y,-upVector.z).scale(nearHeight), null);
		
		//计算 阴影视锥体8个顶点在 光源空间的坐标
		Vector4f[] points = new Vector4f[8];
		points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
		points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
		points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
		points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
		points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
		points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
		points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
		points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);

		boolean first = true;
		//1 计算 shadow box  xyzMinMax
		for (Vector4f point : points) {
			if (first) {
				xMin = point.x;
				xMax = point.x;
				yMin = point.y;
				yMax = point.y;
				zMin = point.z;
				zMax = point.z;
				first = false;
				continue;
			}
			if (point.x > xMax) {
				xMax = point.x;
			} else if (point.x < xMin) {
				xMin = point.x;
			}
			if (point.y > yMax) {
				yMax = point.y;
			} else if (point.y < yMin) {
				yMin = point.y;
			}
			if (point.z > zMax) {
				zMax = point.z;
			} else if (point.z < zMin) {
				zMin = point.z;
			}
		}
		zMax += OFFSET;

		//2 更新正交投影
		float boxWidth = xMax - xMin;
		float boxHeight = yMax - yMin;
		float boxLength = zMax - zMin;
		orthoProjectionMatrix.setIdentity();
		orthoProjectionMatrix.m00 = 2f/boxWidth;
		orthoProjectionMatrix.m11 = 2f/boxHeight;
		orthoProjectionMatrix.m22 = -2f/boxLength;//正交投影1.png
		orthoProjectionMatrix.m33 = 1;
		//3 更新光源空间的视图矩阵
		this.updateLightViewMatrix(light,getCenter());
		// 更新 projection view matrix
		Matrix4f.mul(orthoProjectionMatrix, lightViewMatrix, pvMatrix);
	}
	long lastTime = System.currentTimeMillis();
	
	public Matrix4f getToShadowMapSpaceMatrix() {
		return Matrix4f.mul(offset,  Matrix4f.mul(orthoProjectionMatrix, lightViewMatrix, null), null);
	}
	private Matrix4f offset = createOffset();
	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}
	
	public Vector3f getCenter() {
		float x = (xMin+xMax)/2;
		float y = (yMin+yMax)/2;
		float z = (zMin+zMax)/2;
		Vector4f cen = new Vector4f(x, y, z, 1);
		Matrix4f invertedLight = new Matrix4f();
		Matrix4f.invert(lightViewMatrix, invertedLight);
		return new Vector3f(Matrix4f.transform(invertedLight, cen, null));
	}
}
