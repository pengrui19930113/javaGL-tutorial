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
		Camera camera = new Camera(null);
		calcuateSkyboxViewMatrixBuffer(camera);
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
	public static FloatBuffer calcuateTransformMatrixBuffer(Entity entity) {
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
	public static FloatBuffer calcuateTransformMatrixBuffer(Player player) {
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
	public static FloatBuffer calcuateTransformMatrixBuffer(Terrain terrain) {
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
	public static FloatBuffer calcuateTransformMatrixBuffer2(
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

	public static Matrix4f getViewMatrix(Camera camera) {
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		Matrix4f.rotate((float)Math.toRadians(-camera.cameraRotX), X_NORMAL_VECTOR, mat, mat);// rotation
    	Matrix4f.rotate((float)Math.toRadians(-camera.cameraRotY), Y_NORMAL_VECTOR, mat, mat);
    	Matrix4f.rotate((float)Math.toRadians(-camera.cameraRotZ), Z_NORMAL_VECTOR, mat, mat);
    	Matrix4f.translate(new Vector3f(-camera.cameraPosX, -camera.cameraPosY, -camera.cameraPosZ), mat, mat);//translate
    	return mat;
	}
	
	public static FloatBuffer calcuateViewMatrixBuffer(Camera camera) {
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


	public static FloatBuffer calcuateTransformMatrixBuffer(GuiEntity gui) {
		mat.setIdentity();
    	Matrix4f.translate(new Vector2f(gui.xPos, gui.yPos), mat, mat);//translate
    	Matrix4f.scale(new Vector3f(gui.xSca, gui.ySca, 1.f), mat, mat);
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
		return buffer;
	}


	public static FloatBuffer calcuateSkyboxViewMatrixBuffer(Camera camera) {
		mat.setIdentity();
		Matrix4f.rotate((float)Math.toRadians(-camera.cameraRotX), X_NORMAL_VECTOR, mat, mat);// rotation
    	Matrix4f.rotate((float)Math.toRadians(-camera.cameraRotY), Y_NORMAL_VECTOR, mat, mat);
    	Matrix4f.rotate((float)Math.toRadians(-camera.cameraRotZ), Z_NORMAL_VECTOR, mat, mat);
//    	Matrix4f.translate(new Vector3f(-camera.cameraPosX, -camera.cameraPosY, -camera.cameraPosZ), mat, mat);//translate
//    	mat.m30 = 0;
//    	mat.m31 = 0;
//    	mat.m32 = 0;
    	buffer.clear();
    	mat.store(buffer);
    	buffer.flip();
		return buffer;
	}


	public static Matrix4f calculateModelViewMatrix(Particle particle, Matrix4f viewMatrix) {
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(new Vector3f(particle.xPos, particle.yPos ,particle.zPos), modelMatrix, modelMatrix);
//		modelMatrix.m00 = viewMatrix.m00;
//		modelMatrix.m01 = viewMatrix.m10;		
//		modelMatrix.m02 = viewMatrix.m20;
//		modelMatrix.m10 = viewMatrix.m01;
//		modelMatrix.m11 = viewMatrix.m11;
//		modelMatrix.m12 = viewMatrix.m21;
//		modelMatrix.m20 = viewMatrix.m02;
//		modelMatrix.m21 = viewMatrix.m12;
//		modelMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float)Math.toRadians(particle.rotation), Z_NORMAL_VECTOR, modelMatrix, modelMatrix);
		Matrix4f.scale(new Vector3f(particle.scale,particle.scale,particle.scale), modelMatrix, modelMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
		return modelViewMatrix;
	}
	
	/**
	 数学上的属于 旋转矩阵 为正交矩阵  也就是 逆矩阵等于转制矩阵
	 二维平面旋转矩阵 设旋转角为  arctan 3/5  也就是直角三角形 勾三股四弦五
	 旋转矩阵如下 逆矩阵为其转置 
	矩阵
		0.8	-0.6	0
		0.6	0.8	0
		0	0	1
		逆矩阵
		0.8	0.6	0
		-0.6	0.8	0
		0	0	1
	 */
	public static Matrix4f calculateModelViewMatrix2(Particle particle, Matrix4f viewMatrix) {
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(new Vector3f(particle.xPos, particle.yPos ,particle.zPos), modelMatrix, modelMatrix);
		//为了让正方形始终面对着相机
		modelMatrix.m00 = viewMatrix.m00;//由于旋转矩阵都是正交矩阵，所以该处也可以求解 左上角3x3 的矩阵的逆矩阵进行填充但是效率不如转置  两个矩阵相乘之后变为单位矩阵(3x3部分 因为互为逆矩阵)
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float)Math.toRadians(particle.rotation), Z_NORMAL_VECTOR, modelMatrix, modelMatrix);
		Matrix4f.scale(new Vector3f(particle.scale,particle.scale,particle.scale), modelMatrix, modelMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
		return modelViewMatrix;
	}

	public static FloatBuffer calculateModelViewMatrixBuffer(Particle particle, Matrix4f viewMatrix) {
    	buffer.clear();
//    	Matrix4f mvm = calculateModelViewMatrix(particle,viewMatrix);
    	Matrix4f mvm = calculateModelViewMatrix2(particle,viewMatrix);
    	mvm.store(buffer);
    	buffer.flip();
    	return buffer;
	}

}
