package pengrui.flare;

import org.lwjgl.util.vector.Vector3f;

import pengrui.common.Camera;

public class Sun {
	public static final float SUN_DIS = 50;//SUN_DISTANCE
	public float xDir = 0.55f,yDir = -0.34f,zDir = 1;
	public float scale = 55;
	public int texture;
	
	
	public Sun(int texture) {
		this.texture = texture;
	}
	public Vector3f getWorldPosition(Camera camera) {
		Vector3f sunPos = new Vector3f(xDir,yDir,zDir);
		sunPos.normalise();
		sunPos.negate();
		sunPos.scale(SUN_DIS);
		return Vector3f.add(new Vector3f(camera.xPos,camera.yPos,camera.zPos), sunPos, null);
	}
}
