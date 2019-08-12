package pengrui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * 
 * 渲染流水线流程
 * 本地空间-》世界空间-》相机空间-》
 * 齐次裁剪空间（标准裁剪空间）-》
 * 标准设备空间（opengl [-1,1][-1,1]）-》 视口空间
 *
 */
public class MousePicker {

	private final static int RECURSION_COUNT = 200;
	private final static float RAY_RANGE = 5;
	
	public Vector3f currentRay;
	public Vector3f currentTerrainPoint;
	public MousePicker() {
	}
	
	public void action(Camera camera,Terrain terrain) {
		currentRay = this.calculateMouseRay(camera);
		if(intersectionInRange(0,RAY_RANGE,currentRay,terrain,camera)) {
			currentTerrainPoint = binarySearch(0,0,RAY_RANGE,currentRay,terrain,camera);
		}else {
			currentTerrainPoint = null;
		}
		if(Mouse.isButtonDown(0)) {
			if(null != currentTerrainPoint) {
				System.out.println("terrain point:"+currentTerrainPoint);
			}
		}
	}
	
	/**
	 * 该算法在遇到有山峰挡住的会有异常计算行为
	 * 也就是一条线段与一条曲线有多个交点的时候该算法会有问题
	 * @param count
	 * @param start
	 * @param finish
	 * @param ray
	 * @return
	 */
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray,Terrain terrain,Camera camera) {
		float half = start+((finish-start)/2f);
		if(count >= RECURSION_COUNT){
			Vector3f endPoint = getPointOnRay(ray,half,camera);
			if(null != terrain){
				return endPoint;
			}else{
				return null;
			}
		}
		
		if(intersectionInRange(start,half,ray,terrain,camera)){
			return binarySearch(count+1,start,half,ray,terrain,camera);
		}else{
			return binarySearch(count+1,half,finish,ray,terrain,camera);
		}
	}

	private Vector3f getPointOnRay(Vector3f ray,float distance ,Camera camera){
		Vector3f start = new Vector3f(camera.cameraPosX,camera.cameraPosY,camera.cameraPosZ);
		Vector3f scaledRay = new Vector3f(ray.x*distance,ray.y*distance,ray.z*distance);
		return Vector3f.add(start,scaledRay,null);
	}
	
	private boolean isUnderGround(Vector3f testPoint,Terrain terrain) {
		float height = 0;
		if(null != terrain){
			height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ()); 
		}
		
		if(testPoint.y<height)
			return true;
		
		return false;
	}
	
	private boolean intersectionInRange(float start, float finish, Vector3f ray,Terrain terrain,Camera camera) {
		Vector3f startPoint = getPointOnRay(ray,start,camera);
		Vector3f endPoint = getPointOnRay(ray,finish,camera);
		if(!isUnderGround(startPoint,terrain)&&isUnderGround(endPoint,terrain)){
			return true;
		}
		return false;
	}

	private Vector3f calculateMouseRay(Camera camera) {
		float mousex = Mouse.getX();
		float mousey = Mouse.getY();
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mousex,mousey);
		//经过透视除法的 w = 1  经过透视投影变换近裁剪平面被投影到标准裁剪空间中Z = -1的平面上 所以此处 z = -1 
		Vector4f clipCoords = new Vector4f(normalizedCoords.x,normalizedCoords.y,-1f,1f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords,camera);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords, Camera camera) {
		Matrix4f viewMatrix = MatrixUtil.getViewMatrix(camera);
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x,rayWorld.y,rayWorld.z);
		return mouseRay;
	}
	/**
	 * 将标准裁剪的坐标转换到相机空间的坐标
	 * @param clipCoords
	 * @return
	 */
	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(MatrixUtil.getProjectionMatrix(), null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x,eyeCoords.y,-1f,0f);
	}

	/**
	 * opengl 标准设备坐标
	 * 	(-1,1)			(1,1)
	 * 		-----------------
	 * 		|				|
	 * 		|				|
	 * 		|				|
	 * 		|				|
	 * 		-----------------
	 * (-1,-1)			(1,-1)
	 * 
	 * 
	 * @param mousex
	 * @param mousey
	 * @return
	 */
	private Vector2f getNormalizedDeviceCoords(float mousex, float mousey) {
		// 将 窗口坐标[0,window.width][0,window.height]转换为[-1,1][-1,1]
		float x = 2f*mousex / Display.getWidth() - 1;
		//注意 此处由于各个左面系统的窗口原点不一致 可能
		//可能是左上角可能是左下角 lwjgl是在左下角
		//如果是在左上角为原点则 y = 2*(1-mousey/height)-1 !!! 或者!!!在最后返回的时候 return new Vector2f(x,-y); 也可以实现坐标变换
		float y = 2f*mousey / Display.getHeight() - 1;
		return new Vector2f(x,y);
	}
}
