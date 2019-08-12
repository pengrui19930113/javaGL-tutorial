package pengrui;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class MatrixUtil {
	
	static final float FOV = 70.f;
	static final float NEAR = 0.1F;
	static final float FAR = 1000.f;
	
	public static void main(String[] args) {
		System.out.println(createProjectionMatrix());
	}
	
	
	public static Matrix4f createProjectionMatrix() {
		float aspectRatio = (float)MainGameLoop.WIDTH/(float)MainGameLoop.HEIGHT;
		float y_scale = (float)((1.f/Math.tan(Math.toRadians(FOV/2.F)))*aspectRatio);
		float x_scale = y_scale/aspectRatio;
		float frustum_length = FAR-NEAR;
		Matrix4f projectionMatrix = new Matrix4f();
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR+NEAR)/frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2*NEAR*FAR)/frustum_length);
		projectionMatrix.m33 = 0;
		
		return projectionMatrix;
	}
	
	 static final Vector3f X_NORMAL_VECTOR = new Vector3f(1,0,0)
			 ,Y_NORMAL_VECTOR = new Vector3f(0,1,0)
			 ,Z_NORMAL_VECTOR = new Vector3f(0,0,1)
			 ;
	static Matrix4f mat = new Matrix4f();
	static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	public static FloatBuffer caculateTransformMatrix(Entity entity) {
		float entityPosX=entity.entityPosX, entityPosY=entity.entityPosY, entityPosZ=entity.entityPosZ
		, entityRotX=entity.entityRotX, entityRotY=entity.entityRotY, entityRotZ=entity.entityRotZ
		, entityScaX=entity.entityScaX, entityScaY=entity.entityScaY, entityScaZ=entity.entityScaZ;
		mat.setIdentity();
		// 注意此处的 平移旋转缩放变换等价于 =  MT * MR * MS * P (MT:位移矩阵 ，MR:旋转矩阵，MS:缩放矩阵) // 可以查看 caculateTransformMatrix2
		Matrix4f.translate(new Vector3f(entityPosX, entityPosY, entityPosZ), mat, mat);//translate
		Matrix4f.rotate((float)Math.toRadians(entityRotX), X_NORMAL_VECTOR, mat, mat);// rotation
    	Matrix4f.rotate((float)Math.toRadians(entityRotY), Y_NORMAL_VECTOR, mat, mat);
    	Matrix4f.rotate((float)Math.toRadians(entityRotZ), Z_NORMAL_VECTOR, mat, mat);
    	Matrix4f.scale(new Vector3f(entityScaX,entityScaY,entityScaZ), mat, mat); //scale
    	
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
    	
    	return buffer;
	}
	/**
	 * 其实 player 可以继承 entity  但是为了代码的直观可读性 抄一遍吧
	 * @param player
	 * @return
	 */
	public static FloatBuffer caculateTransformMatrix(Player player) {
		mat.setIdentity();
		Matrix4f.translate(new Vector3f(player.xPos, player.yPos, player.zPos), mat, mat);//translate
		Matrix4f.rotate((float)Math.toRadians(player.xRot), X_NORMAL_VECTOR, mat, mat);// rotation
    	Matrix4f.rotate((float)Math.toRadians(player.yRot), Y_NORMAL_VECTOR, mat, mat);
    	Matrix4f.rotate((float)Math.toRadians(player.zRot), Z_NORMAL_VECTOR, mat, mat);
    	Matrix4f.scale(new Vector3f(player.xSca,player.xSca,player.xSca), mat, mat); //scale
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
    	return buffer;
	}
	public static FloatBuffer caculateTransformMatrix(Terrain terrain) {
		mat.setIdentity();
		Matrix4f.translate(new Vector3f(terrain.xPos, 0, terrain.zPos), mat, mat);//translate
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
    	return buffer;
	}
	
	/**
	 * 正常逻辑下先旋转再平移 和先平移再旋转的结果不一致
	 * @param entityPosX
	 * @param entityPosY
	 * @param entityPosZ
	 * @param entityRotX
	 * @param entityRotY
	 * @param entityRotZ
	 * @param entityScaX
	 * @param entityScaY
	 * @param entityScaZ
	 * @return
	 */
	public static FloatBuffer caculateTransformMatrix2(
			float entityPosX,float entityPosY,float entityPosZ
			,float entityRotX,float entityRotY,float entityRotZ
			,float entityScaX,float entityScaY,float entityScaZ
			) {
		mat.setIdentity();
		Matrix4f.translate(new Vector3f(entityPosX, entityPosY, entityPosZ), mat, mat);//translate
		
		Matrix4f tmp = new Matrix4f();
		Matrix4f.rotate((float)Math.toRadians(entityRotX), X_NORMAL_VECTOR, tmp, tmp);// rotation
		Matrix4f.mul(mat, tmp, mat);//先旋转 再平移
//		Matrix4f.mul(tmp, mat, mat);//先平移 再旋转
		
		buffer.clear();
		mat.store(buffer);
		buffer.flip();
		
		return buffer;
	}

	public static FloatBuffer caculateViewMatrix(Camera camera) {
		float cameraPosX = camera.cameraPosX, cameraPosY=camera.cameraPosY, cameraPosZ=camera.cameraPosZ
				, cameraRotX=camera.cameraRotX,cameraRotY=camera.cameraRotY,cameraRotZ=camera.cameraRotZ;
		mat.setIdentity();
		Matrix4f.rotate((float)Math.toRadians(-cameraRotX), X_NORMAL_VECTOR, mat, mat);// rotation
    	Matrix4f.rotate((float)Math.toRadians(-cameraRotY), Y_NORMAL_VECTOR, mat, mat);
    	Matrix4f.rotate((float)Math.toRadians(-cameraRotZ), Z_NORMAL_VECTOR, mat, mat);
    	Matrix4f.translate(new Vector3f(-cameraPosX, -cameraPosY, -cameraPosZ), mat, mat);//translate
    	
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
		return buffer;
	}


	public static FloatBuffer caculateTransformMatrix(GuiEntity gui) {
		mat.setIdentity();
    	Matrix4f.translate(new Vector2f(gui.xPos, gui.yPos), mat, mat);//translate
    	Matrix4f.scale(new Vector3f(gui.xSca, gui.ySca, 1.f), mat, mat);
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
		return buffer;
	}



}
