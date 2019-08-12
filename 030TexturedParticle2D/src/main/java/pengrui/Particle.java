package pengrui;

import java.util.List;
import static pengrui.CommonConstant.*;
public class Particle {
	public static VAO vao;
	
	public float xPos,yPos,zPos;// position
	public float vx,vy,vz;// velocity,speed
	public float gravityEffect;//受重力影响的程度  通常取 1
	public float lifeLength;
	public float rotation;
	public float scale;
	
	public float elapsedTime = 0;
	
	public int texture;
	public int numberOfRows;
	
	public Particle(
			float x,float y,float z
			,float vx,float vy,float vz
			,float gravityEffect,float lifeLength
			,float rot,float sca
			) {
		this.xPos = x;
		this.yPos = y;
		this.zPos = z;
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rot;
		this.scale = sca;
	}
	
	public Particle(
			float x,float y,float z
			,float vx,float vy,float vz
			,float gravityEffect,float lifeLength
			) {
		this(x, y, z, vx, vy, vz, gravityEffect, lifeLength, 0, 0.2f);
	}
	
	public boolean action(long delta) {
		float deltaSeconds = delta/1000f;
		vy +=PARTICLE_GRAVITY*gravityEffect*deltaSeconds;
		
		xPos += deltaSeconds*vx;
		yPos += deltaSeconds*vy;
		zPos += deltaSeconds*vz;
		
		elapsedTime += deltaSeconds;
		return elapsedTime<lifeLength;
	}
	
	static final float[] VERTICES = {
			-0.5f,0.5f
			,-0.5f,-0.5f
			,0.5f,0.5f
			,0.5f,-0.5f
	};
	
	public static void loadParticleVao(List<Integer> vaos, List<Integer> vbos) {
		vao = VAO.createParticleVao(vaos, vbos, VERTICES);
	}
}
