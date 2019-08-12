package pengrui.particle3d;

import org.lwjgl.util.vector.Vector3f;

public class Particle3D {
	private static final float GRAVITY = 0.5F,LIFE_LENGTH = 5000,SPEED = 3;
	
	public float xVel,yVel,zVel; //Velocity
	public float xPos,yPos,zPos;
	public float lifeLength;
	
	public Particle3D(float vx,float vy,float vz) {
		this(2,5,-2,vx,vy,vz,LIFE_LENGTH);
	}
	public Particle3D(float px,float py,float pz,float vx,float vy,float vz,float lifeLength) {
		this.xPos = px;
		this.yPos = py;
		this.zPos = pz;
		this.xVel = vx;
		this.yVel = vy;
		this.zVel = vz;
		this.lifeLength = lifeLength;
	}
	
	/**
	 * 
	 * @param delta
	 * @return 例子是否活着  true 是的
	 */
	public boolean update(float delta) {
		float secondDelta = delta/1000f;
		yVel -= GRAVITY*secondDelta;
		Vector3f change = new Vector3f(xVel,yVel,zVel);
		change.scale(secondDelta*SPEED);
		Vector3f dest = new Vector3f(xPos,yPos,zPos);
		Vector3f.add(dest, change, dest);
		this.xPos = dest.x;
		this.yPos = dest.y;
		this.zPos = dest.z;
		lifeLength -= delta;
		return lifeLength > 0;
	}
}
