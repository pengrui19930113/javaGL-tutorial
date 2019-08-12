package pengrui.particle3d;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import pengrui.common.VAO;

public class Particles3DGroup {
	private static final float PPS = 50;
	public final FloatBuffer buffer;
	
	public final int maxParticles; 
	public int particleCount;
	public float pps;
	//if you want, you can create generate new particle's position and velocity of member variable
	public VAO vao;
	public int vbo;
	public List<Particle3D> particles;
	
	private static Random random = new Random();
	public Particles3DGroup(Particle3DLoader.ParticleDataHolder holder,int maxParticles){
		this.maxParticles = maxParticles;
		this.pps = PPS;
		this.buffer = BufferUtils.createFloatBuffer(maxParticles*3);//position buffer
		this.vao = holder.vao;
		this.vbo = holder.vboid;
		this.particles = new ArrayList<Particle3D>();
	}
	public void update(long delta) {
		//check died particle and remove
		updateAndRemoveDiedPartilces(delta);
		//gen particle
		genParticles(delta);
		//put data to vbo
		store();
	}

	private void store() {
		this.particleCount = Math.min(particles.size(), maxParticles);
		float[] particleData = new float[particleCount*3];
		for(int i=0;i<particleCount;i++) {
			Particle3D p = particles.get(i);
			particleData[i*3] = p.xPos;
			particleData[i*3+1] = p.yPos;
			particleData[i*3+2] = p.zPos;
		}
		buffer.clear();
		buffer.put(particleData);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity()*4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void updateAndRemoveDiedPartilces(long delta) {
		Iterator<Particle3D> iterator = particles.iterator();
		while(iterator.hasNext()) {
			Particle3D p = iterator.next();
			if(!p.update(delta)) {
				iterator.remove();
			}
		}
	}

	private void genParticles(long delta) {
		float particlesToCreate = pps*delta/1000f;
		int count = (int) Math.floor(particlesToCreate);
		for(int i=0;i<count;i++) {
			Vector3f particleVelocity = genVelocity(0,1,0,45);
			particles.add(new Particle3D(particleVelocity.x,particleVelocity.y,particleVelocity.z));
		}
		float partialParticle = particlesToCreate%1;
		if(random.nextFloat() < partialParticle) {//根据随机数生成的数字决定这个 小数点后的是否生成
			Vector3f particleVelocity = genVelocity(0,1,0,45);
			particles.add(new Particle3D(particleVelocity.x,particleVelocity.y,particleVelocity.z));
		}
	}
	
	/**
	 * 
	 * @param coneDirvx 椎体 开口的方向的x分量
	 * @param coneDirvy 椎体 开口的方向的y分量
	 * @param coneDirvz 椎体 开口的方向的z分量
	 * @param angle		coneAngle 椎体母线和轴线的夹角
	 * @return
	 */
	private Vector3f genVelocity(float coneDirvx,float coneDirvy,float coneDirvz,float angle) {
		float cosAngle = (float) Math.cos(angle);
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float y = cosAngle + (random.nextFloat() * (1 - cosAngle));
		float rootOneMinusZSquared = (float) Math.sqrt(1 - y * y);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float z = (float) (rootOneMinusZSquared * Math.sin(theta));//此时 xyz 的平方和 == 1；
		Vector4f direction = new Vector4f(x, y, z, 1);
		
		if (coneDirvx != 0 || coneDirvz != 0 || (coneDirvy != 1 && coneDirvy != -1)) {//如果方向不是在空间坐标系上 与y轴同向  则需要旋转
			Vector3f coneDirection = new Vector3f(coneDirvy,coneDirvx,coneDirvz);
			Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(0, 1, 0), null);
			rotateAxis.normalise();
			float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, new Vector3f(0, 1, 0)));
			Matrix4f rotationMatrix = new Matrix4f();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			Matrix4f.transform(rotationMatrix, direction, direction);
		} else if (coneDirvy == -1) {
			direction.y *= -1;
		}
		return new Vector3f(direction);
	}
	
}
