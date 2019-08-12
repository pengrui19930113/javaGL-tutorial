package pengrui.particle3d;

import java.util.ArrayList;
import java.util.List;

public class Particle3DSystem {
	
	public static final float PPS = 50;
	public static final int MAX_PARTICLES = 1000;
	public List<Particles3DGroup> groups;
	
	public Particle3DSystem(List<Integer>vaos,List<Integer>vbos) {
		groups = new ArrayList<Particles3DGroup>();
		Particle3DLoader.ParticleDataHolder holder = Particle3DLoader.createVaoAndVbo(vaos, vbos,MAX_PARTICLES);
		groups.add(new Particles3DGroup(holder,MAX_PARTICLES));
	}
	
	public void update(long delta) {
		for(Particles3DGroup group:groups) {
			group.update(delta);
		}
	}
}
