package pengrui.terrain;

import java.util.Random;

public class HeightGenerator7 implements IHeightGenerator {
	private static final float AMPLITUDE = 14F;
	private static final int OCTAVES = 3;
	private static final float ROUGHNESS = 0.3F;
	private Random random = new Random();
	private int seed;
	
	public HeightGenerator7(){
		this.seed = random.nextInt(1000000000);
	}
	
	public float generateHeight(int x,int z){
		
		//分母越大越平滑 // 各个谐波相加的感觉  得到比较自然的地面
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES-1);
		for(int i=0;i<OCTAVES;++i){
			float freq = (float)(Math.pow(2, i)/d);
			float amp = (float) Math.pow(ROUGHNESS, i)*AMPLITUDE;
			total += this.getInterpolateNoise(x*freq, z*freq)*amp;
		}
		return total; 
	}
	
	private float getInterpolateNoise(float x,float z){
		int ix =(int)x;
		int iz = (int)z;
		float fracx = x-ix;
		float fracz = z-iz;
		float v1 = this.getSmoothNoise(ix, iz);
		float v2 = this.getSmoothNoise(ix+1, iz);
		float v3 = this.getSmoothNoise(ix, iz+1);
		float v4 = this.getSmoothNoise(ix+1, iz+1);
		
		float i1 = this.interpolate(v1, v2, fracx);
		float i2 = this.interpolate(v3, v4, fracx);
		return this.interpolate(i1, i2, fracz);
	}
	
	private float interpolate(float a,float b,float blend){
		double theta = blend*Math.PI;
		float f = (float )(1f-Math.cos(theta))*0.5f;
		return a*(1f-f)+b*f;
	}
	
	public float getNoise(int x,int z){
		random.setSeed(x*49632+z*325176+seed);//相同的种子next会获得相同的数
		return random.nextFloat()*2f-1f;
	}
	/**
	 * 
	 * 	   x-1,z+1	   x,z+1    x+1,z+1
	 * 	   x-1,z       x,z      x+1,z
	 * 	   x-1,z-1     x,z-1    x+1,z-1
	 * 
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	private float getSmoothNoise(int x,int z){
		float corners =
				(this.getNoise(x-1, z-1))
				+(this.getNoise(x+1, z-1))
				+(this.getNoise(x-1, z+1))
				+(this.getNoise(x+1, z+1))
				;
		corners /=16f;
		float sides = 
				(this.getNoise(x, z-1))
				+(this.getNoise(x-1, z))
				+(this.getNoise(x, z+1))
				+(this.getNoise(x+1, z))
				;
		sides/=8;
		float center = this.getNoise(x, z);
		center /=4;
		// 4*1/16+4*1/8+1/4 = 1; // 卷积核分布 为四角的权重 为1/16 四边的权重为 1/8 中心的权重为1/4
		return corners+sides+center;
	}
}
