package pengrui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Camera {
	
	static float CAMERA_MOVE_SPEED = 10;
	static float CAMERA_ROTATE_SPEED = 0.1f;
	
	public float xPos,yPos = 2,zPos = 5;
	
	public Vector3f right = new Vector3f(1,0,0);
	public Vector3f up = new Vector3f(0,1,0);
	private final Matrix4f viewMatrix;
	public Camera() {
		viewMatrix = new Matrix4f();
		updateViewMatrix();
	}	
	
	private void updateViewMatrix() {
		right.normalise();
		up.normalise();
		Vector3f back = Vector3f.cross(right, up, null);
		viewMatrix.m00 = right.x;
		viewMatrix.m10 = right.y;
		viewMatrix.m20 = right.z;
		viewMatrix.m30 = 0;
		
		viewMatrix.m01 = up.x;
		viewMatrix.m11 = up.y;
		viewMatrix.m21 = up.z;
		viewMatrix.m31 = 0;
		
		viewMatrix.m02 = back.x;
		viewMatrix.m12 = back.y;
		viewMatrix.m22 = back.z;
		viewMatrix.m32 = 0;
		
		viewMatrix.m03 = xPos;
		viewMatrix.m13 = yPos;
		viewMatrix.m23 = zPos;
		viewMatrix.m33 = 1;
		
		viewMatrix.transpose();
		viewMatrix.invert();
	}
	
	private void move(Vector3f vec) {
		xPos += vec.x;
		yPos += vec.y;
		zPos += vec.z;
	}
	
	public void update(long delta) {
		float deltaSeconds = delta/1000.f;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			Vector3f front = (Vector3f)Vector3f.cross(right, up, null).scale(-CAMERA_MOVE_SPEED*deltaSeconds);
			move(front);
		} 
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			Vector3f back = (Vector3f)Vector3f.cross(right, up, null).scale(CAMERA_MOVE_SPEED*deltaSeconds);
			move(back);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			Vector3f left = (Vector3f) new Vector3f(right).scale(-CAMERA_MOVE_SPEED*deltaSeconds);
			move(left);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			Vector3f right = (Vector3f) new Vector3f(this.right).scale(CAMERA_MOVE_SPEED*deltaSeconds);
			move(right);
		}
		
		if(Mouse.isButtonDown(0)) {
			float dx = -Mouse.getDX();//dx <0 表示 窗口坐标系向左的单位时间偏移   在坐标变换中这个应该是逆时针
			Matrix4f rotation = Matrix4f.rotate(deltaSeconds*CAMERA_ROTATE_SPEED*dx, new Vector3f(0,1,0) ,new Matrix4f(), null);
			Vector4f v4 = Matrix4f.transform(rotation, new Vector4f(right.x,right.y,right.z,0), null);
			right.x = v4.x;
			right.y = v4.y;
			right.z = v4.z;
//			v4 = Matrix4f.transform(rotation, new Vector4f(up.x,up.y,up.z,0), null);
//			up.x = v4.x;
//			up.y = v4.y;
//			up.z = v4.z;
		}
		if(Mouse.isButtonDown(1)) {
			float dy = Mouse.getDY();//dy >0 向上	逆时针
			Matrix4f rotation = Matrix4f.rotate(deltaSeconds*CAMERA_ROTATE_SPEED*dy, new Vector3f(1,0,0) ,new Matrix4f(), null);
			Vector4f v4 = Matrix4f.transform(rotation, new Vector4f(up.x,up.y,up.z,0), null);
			up.x = v4.x;
			up.y = v4.y;
			up.z = v4.z;
//			v4 = Matrix4f.transform(rotation, new Vector4f(right.x,right.y,right.z,0), null);
//			right.x = v4.x;
//			right.y = v4.y;
//			right.z = v4.z;
		}
		updateViewMatrix();
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
}
