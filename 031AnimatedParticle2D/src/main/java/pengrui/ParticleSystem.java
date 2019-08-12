package pengrui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	public ParticleSystem(float pps,float speed,float gravity,float lifeLength,int texture,int numberOfRows) {
		this(pps, speed, 0,false, gravity, lifeLength, 0,false,texture,numberOfRows);
	}
	private static final float  PARTICLE_GRAVITY_EFFECTED = 1F;
	public ParticleSystem(float pps,float speed,float lifeLength,int texture,int numberOfRows) {
		this(pps, speed, 0,false, PARTICLE_GRAVITY_EFFECTED, lifeLength, 0,false,texture,numberOfRows);
	}
	
	public ParticleSystem(
			float pps
			,float speed,float speedNoises ,boolean useSpeedNoises
			,float gravity
			,float lifeLength,float lifeLengthNoises,boolean useLifeLengthNoises,int texture,int numberOfRows) {
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
			sortHighTolow(list);//TODO
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
