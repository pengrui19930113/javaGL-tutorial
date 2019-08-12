package pengrui;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {

	public List<Particle> particles;
	
	public float pps;//particle per second;
	public float speed;//particle emit speed;
	public float speedNoises = 0;
	public boolean useSpeedNoises = false;
	
	public float gravity;//particle gravity effect/factor
	
	public float lifeLength;//particle life length;
	public float lifeLengthNoises = 0;
	public boolean useLifeLengthNoises = false;
	
	public Random random;
	public ParticleSystem(List<Integer>vaos,List<Integer>vbos,List<Integer>textures,float pps,float speed,float gravity,float lifeLength) {
		this(vaos,vbos,textures,pps, speed, 0,false, gravity, lifeLength, 0,false);
	}
	private static final float  PARTICLE_GRAVITY_EFFECTED = 1F;
	public ParticleSystem(List<Integer>vaos,List<Integer>vbos,List<Integer>textures,float pps,float speed,float lifeLength) {
		this(vaos,vbos,textures,pps, speed, 0,false, PARTICLE_GRAVITY_EFFECTED, lifeLength, 0,false);
	}
	
	public ParticleSystem(
			List<Integer>vaos,List<Integer>vbos,List<Integer>textures
			,float pps
			,float speed,float speedNoises ,boolean useSpeedNoises
			,float gravity
			,float lifeLength,float lifeLengthNoises,boolean useLifeLengthNoises) {
		
		Particle.loadParticleVao(vaos,vbos,textures);
		this.pps = pps;
		this.speed = speed;
		this.speedNoises = speedNoises;
		this.gravity = gravity;
		this.lifeLength = lifeLength;
		this.lifeLengthNoises = lifeLengthNoises;
		particles = new LinkedList<Particle>();
		random = new Random();
	}
	
	private List<Object> willBeRemovedElement = new LinkedList<Object>();
	public void action(long delta) {
		//方法1
		willBeRemovedElement.clear();
		for(Particle p:particles) { 
			boolean isAlive = p.action(delta);
			if(!isAlive) {
				//particles.remove(p);// for each底层由Iterator实现 移除也必须由iterator进行移除，但是此处无法获取iterator 否则可能抛出ConcurrentModificationException异常
				willBeRemovedElement.add(p);
			}
		}
		for(Object o:willBeRemovedElement) {
			particles.remove(o);
		}
		//方法2		
//		Iterator<Particle> iterator = particles.iterator();
//		while(iterator.hasNext()) {
//			Particle p = iterator.next();
//			boolean alive = p.action(delta);
//			if(!alive) {
//				iterator.remove();
//			}
//		}
	}
	
	private void addParticle(Particle p) {
		particles.add(p);
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
		addParticle(new Particle(x, y, z, vx, vy, vz, gravity, lifeLength));
	}
	
	private float random(float min,float max) {
		float length = max-min;
		return min+(random.nextFloat()*length);
	}
}
