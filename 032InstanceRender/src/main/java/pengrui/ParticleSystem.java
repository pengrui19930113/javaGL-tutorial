package pengrui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class ParticleSystem {

	public Map<Integer,List<Particle>> particles;
	public float pps;//particle per second;
	public float speed;//particle emit speed;
	public float speedNoises = 0;
	public boolean useSpeedNoises = false;
	
	public float gravity;//particle gravity effect/factor
	
	public float lifeLength;//particle life length;
	public float lifeLengthNoises = 0;
	public boolean useLifeLengthNoises = false;
	
	public int texture;
	public int numberOfRows;
	
	public Random random;
	
	
	private static final int MAX_INSTANCES = 10000;
	public static final int INSTANCE_DATA_LENGTH = (4 + 4 + 4 + 4 // modelviewMatrix 16
															// float
			+ 2 // offset1 2 float
			+ 2 // offset2 2 float
			+ 1 // blendFactor 1 float
	);
	public int instanceVaraibleVbo;
	public FloatBuffer dataBuffer;
	
	public ParticleSystem(List<Integer> vaos,List<Integer> vbos,float pps,float speed,float gravity,float lifeLength,int texture,int numberOfRows) {
		this(vaos,vbos,pps, speed, 0,false, gravity, lifeLength, 0,false,texture,numberOfRows);
	}
	private static final float  PARTICLE_GRAVITY_EFFECTED = 1F;
	public ParticleSystem(List<Integer> vaos,List<Integer> vbos,float pps,float speed,float lifeLength,int texture,int numberOfRows) {
		this(vaos,vbos,pps, speed, 0,false, PARTICLE_GRAVITY_EFFECTED, lifeLength, 0,false,texture,numberOfRows);
	}
	
	public ParticleSystem(
			List<Integer> vaos
			,List<Integer> vbos
			,float pps
			,float speed,float speedNoises ,boolean useSpeedNoises
			,float gravity
			,float lifeLength,float lifeLengthNoises,boolean useLifeLengthNoises,int texture,int numberOfRows) {
		Particle.loadParticleVao(vaos,vbos);
		this.texture = texture;
		this.numberOfRows = numberOfRows;
		this.pps = pps;
		this.speed = speed;
		this.speedNoises = speedNoises;
		this.gravity = gravity;
		this.lifeLength = lifeLength;
		this.lifeLengthNoises = lifeLengthNoises;
		particles = new HashMap<Integer,List<Particle>>();
		random = new Random();
		int bufferSize = MAX_INSTANCES*INSTANCE_DATA_LENGTH;
		dataBuffer = BufferUtils.createFloatBuffer(bufferSize);
		instanceVaraibleVbo = createEmptyVbo(vbos,bufferSize);
		addInstanceAttribute();
	}
	
	private void addInstanceAttribute() {
		GL30.glBindVertexArray(Particle.vao.vaoid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceVaraibleVbo);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, INSTANCE_DATA_LENGTH*4,0*4);
		GL33.glVertexAttribDivisor(1, 1);
		GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, INSTANCE_DATA_LENGTH*4,4*4);
		GL33.glVertexAttribDivisor(2, 1);
		GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, INSTANCE_DATA_LENGTH*4,8*4);
		GL33.glVertexAttribDivisor(3, 1);
		GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, INSTANCE_DATA_LENGTH*4,12*4);
		GL33.glVertexAttribDivisor(4, 1);
		GL20.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, INSTANCE_DATA_LENGTH*4,16*4);
		GL33.glVertexAttribDivisor(5, 1);
		GL20.glVertexAttribPointer(6, 1, GL11.GL_FLOAT, false, INSTANCE_DATA_LENGTH*4,20*4);
		GL33.glVertexAttribDivisor(6, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	private int createEmptyVbo(List<Integer> vbos,int bufferSize) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferSize*4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public void action(Camera camera, long delta) {
		Iterator<Map.Entry<Integer,List<Particle>>> mapIter = particles.entrySet().iterator();
		while(mapIter.hasNext()) {
			List<Particle> list = mapIter.next().getValue();
			Iterator<Particle> partIter = list.iterator();
			while(partIter.hasNext()) {
				Particle p = partIter.next();
				boolean alive = p.action(camera,delta);
				if(!alive) {
					partIter.remove();
					if(list.isEmpty()) {
						mapIter.remove();
					}
				}
			}
			sortHighTolow(list);//将距离比较近的粒子放在最后渲染
		}
	}
	
	static void sortHighTolow(List<Particle> list) {
		for(int i=1;i<list.size();i++) {
			if(list.get(i).distance>list.get(i-1).distance) {
				sortUpHighToLow(list,i);
			}
		}
	}
	private static void sortUpHighToLow(List<Particle> list, int i) {
		Particle item = list.get(i);
		int attemptPos = i-1;
		while(attemptPos != 0 &&list.get(attemptPos-1).distance<item.distance){
			attemptPos--;
		}
		list.remove(i);
		list.add(attemptPos, item);
	}

	public void addParticle(Particle p) {
		if(null == p) return;
		List<Particle> partList = particles.get(p.texture);
		if(null == partList) {
			partList = new ArrayList<Particle>();
		}
		particles.put(p.texture, partList);
		partList.add(p);
	}
	
	public void genParticles(float x,float y,float z,float delta) {
		float particlesToCreate = pps*delta/1000.f;
		int count = (int)Math.floor(particlesToCreate);
		for(int i=0;i<count;i++) {
			emitParticle(x,y,z);
		}
		float partialParticle = particlesToCreate%1;
		if(random.nextFloat()<partialParticle){
			emitParticle(x,y,z);
		}
	}
	
	private void emitParticle(float x,float y,float z) {
		this.emitParticle(x, y, z, useSpeedNoises, useLifeLengthNoises);
	}
	
	private void emitParticle(float x,float y,float z,boolean useSpeedNoises,boolean useLifeLengthNoises) {
		float dirx = this.random(-1, 1);
		float diry = 1.f;//this.random(-1, 1);
		float dirz = this.random(-1, 1);
		float length = (float) Math.sqrt(dirx*dirx+diry*diry+dirz*dirz);
		
		float speed = this.speed;
		if(useSpeedNoises) {
			speed += random(-speedNoises,speedNoises);
		}
		float vx = speed*dirx/length;
		float vy = speed*diry/length;
		float vz = speed*dirz/length;
		float gravity = this.gravity;
		float lifeLength = this.lifeLength;
		if(useLifeLengthNoises) {
			lifeLength += random(-lifeLengthNoises,lifeLengthNoises);
		}
		Particle p = new Particle(x, y, z, vx, vy, vz, gravity, lifeLength);
		p.texture = texture;
		p.numberOfRows = numberOfRows;
		addParticle(p);
	}
	
	private float random(float min,float max) {
		float length = max-min;
		return min+(random.nextFloat()*length);
	}
}
