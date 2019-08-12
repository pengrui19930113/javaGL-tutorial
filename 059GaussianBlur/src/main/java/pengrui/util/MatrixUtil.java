package pengrui.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import pengrui.common.Camera;
import pengrui.entity.Entity;
import pengrui.gui.GuiEntity;
import pengrui.normalMapping.NormalMappingEntity;
import pengrui.player.Player;
import pengrui.terrain.Terrain;
import static pengrui.config.CommonConstant.*;
public class MatrixUtil {
	
	public static final float FOV = 70.f;
	public static final float NEAR = 0.1F;
	public static final float FAR = 1000.f;
	
	public static void main(String[] args) {
		System.out.println(createProjectionMatrix());
		Camera camera = new Camera(null);
		calculateSkyboxViewMatrix(camera);
	}
	
	
	public static Matrix4f createProjectionMatrix() {
		float aspectRatio = (float)WIDTH/(float)HEIGHT;
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
	

	static Matrix4f mat = new Matrix4f();
	static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	public static Matrix4f calculateTransformMatrix(Entity entity) {
		float entityPosX=entity.xPos, entityPosY=entity.yPos, entityPosZ=entity.zPos
				, entityRotX=entity.xRot, entityRotY=entity.yRot, entityRotZ=entity.zRot
				, entityScaX=entity.xSca, entityScaY=entity.ySca, entityScaZ=entity.zSca;
				mat.setIdentity();
				// 注意此处的 平移旋转缩放变换等价于 =  MT * MR * MS * P (MT:位移矩阵 ，MR:旋转矩阵，MS:缩放矩阵) // 可以查看 calculateTransformMatrix2
				Matrix4f.translate(new Vector3f(entityPosX, entityPosY, entityPosZ), mat, mat);//translate
				Matrix4f.rotate((float)Math.toRadians(entityRotX), X_NORMAL_VECTOR, mat, mat);// rotation
		    	Matrix4f.rotate((float)Math.toRadians(entityRotY), Y_NORMAL_VECTOR, mat, mat);
		    	Matrix4f.rotate((float)Math.toRadians(entityRotZ), Z_NORMAL_VECTOR, mat, mat);
		    	Matrix4f.scale(new Vector3f(entityScaX,entityScaY,entityScaZ), mat, mat); //scale
		return mat;
	}
	
	public static FloatBuffer calculateTransformMatrixBuffer(Entity entity) {
		float entityPosX=entity.xPos, entityPosY=entity.yPos, entityPosZ=entity.zPos
		, entityRotX=entity.xRot, entityRotY=entity.yRot, entityRotZ=entity.zRot
		, entityScaX=entity.xSca, entityScaY=entity.ySca, entityScaZ=entity.zSca;
		mat.setIdentity();
		// 注意此处的 平移旋转缩放变换等价于 =  MT * MR * MS * P (MT:位移矩阵 ，MR:旋转矩阵，MS:缩放矩阵) // 可以查看 calculateTransformMatrix2
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
	public static FloatBuffer calculateTransformMatrix(Player player) {
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
	public static FloatBuffer calculateTransformMatrix(Terrain terrain) {
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
	public static FloatBuffer calculateTransformMatrix2(
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

	public static FloatBuffer calculateViewMatrix(Camera camera) {
		float cameraPosX = camera.xPos, cameraPosY=camera.yPos, cameraPosZ=camera.zPos
				, cameraRotX=camera.xRot,cameraRotY=camera.yRot,cameraRotZ=camera.zRot;
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


	public static FloatBuffer calculateTransformMatrix(GuiEntity gui) {
		mat.setIdentity();
    	Matrix4f.translate(new Vector2f(gui.xPos, gui.yPos), mat, mat);//translate
    	Matrix4f.scale(new Vector3f(gui.xSca, gui.ySca, 1.f), mat, mat);
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
		return buffer;
	}


	public static FloatBuffer calculateSkyboxViewMatrix(Camera camera) {
		mat.setIdentity();
		Matrix4f.rotate((float)Math.toRadians(-camera.xRot), X_NORMAL_VECTOR, mat, mat);// rotation
    	Matrix4f.rotate((float)Math.toRadians(-camera.yRot), Y_NORMAL_VECTOR, mat, mat);
    	Matrix4f.rotate((float)Math.toRadians(-camera.zRot), Z_NORMAL_VECTOR, mat, mat);
//    	Matrix4f.translate(new Vector3f(-camera.cameraPosX, -camera.cameraPosY, -camera.cameraPosZ), mat, mat);//translate
//    	mat.m30 = 0;
//    	mat.m31 = 0;
//    	mat.m32 = 0;
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
		return buffer;
	}


	public static FloatBuffer calculateTransformMatrix(NormalMappingEntity entity) {
		float entityPosX=entity.xPos, entityPosY=entity.yPos, entityPosZ=entity.zPos
		, entityRotX=entity.xRot, entityRotY=entity.yRot, entityRotZ=entity.zRot
		, entityScaX=entity.xSca, entityScaY=entity.ySca, entityScaZ=entity.zSca;
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


	public static Matrix4f calculateCameraRotation(Camera camera) {
		Matrix4f rotation = new Matrix4f();
		rotation.rotate((float)Math.toRadians(- camera.xRot), X_NORMAL_VECTOR);
		rotation.rotate((float)Math.toRadians(-camera.yRot), Y_NORMAL_VECTOR);
		return rotation;
	}

}
