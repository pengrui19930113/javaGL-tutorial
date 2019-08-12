package pengrui;

import java.util.List;
import java.util.Random;

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
	public boolean additive = true;
	public float distance;
	public float texOffset1x,texOffset1y,texOffset2x,texOffset2y;
	public float blend;
	
	static Random random = new Random();
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
	
	public boolean action(Camera camera, long delta) {
		float deltaSeconds = delta/1000f;
		vy +=PARTICLE_GRAVITY*gravityEffect*deltaSeconds;
		xPos += deltaSeconds*vx;
		yPos += deltaSeconds*vy;
		zPos += deltaSeconds*vz;
		float xDif = xPos-camera.cameraPosX;
		float yDif = yPos-camera.cameraPosY;
		float zDif = yPos-camera.cameraPosZ;
		distance = (float) Math.sqrt(xDif*xDif+yDif*yDif+zDif*zDif);
		this.updateTextureCoordsInfo();
//		rotation += random.nextFloat()*deltaSeconds*100;
//		rotation %=360f;
		elapsedTime += deltaSeconds;
		return elapsedTime<lifeLength;
	}
	
	private void updateTextureCoordsInfo() {
		float lifeFactor = elapsedTime/lifeLength;//例子生存周期占比
		int stageCount = numberOfRows*numberOfRows;//一共有多少帧
		float atlasProgression = lifeFactor*stageCount;//当前处于帧占比
		int index1 = (int)Math.floor(atlasProgression);
		int index2 = index1 <(stageCount-1)?(index1+1):index1;
		blend = atlasProgression%1;// 两帧的混合占比
		texOffset1x = (float)(index1%numberOfRows)/numberOfRows;//求取坐标偏移
		texOffset1y = (float)(index1/numberOfRows)/numberOfRows;
		texOffset2x = (float)(index2%numberOfRows)/numberOfRows;
		texOffset2y = (float)(index2/numberOfRows)/numberOfRows;
		
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
