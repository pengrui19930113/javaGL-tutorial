package pengrui.terrain;

import java.util.Random;

public class HeightGenerator3 implements IHeightGenerator {
	private static final float AMPLITUDE = 7F;
	
	private Random random = new Random();
	private int seed;
	
	public HeightGenerator3(){
		this.seed = random.nextInt(1000000000);
//		System.out.println(this.getNoise(4, 13));
//		System.out.println(this.getNoise(4, 13));// 获得的噪音一样
//		System.out.println(this.getNoise(5, 13));//获得的噪音与上面的区别很大
	}
	
	public float generateHeight(int x,int z){
		return this.getSmoothNoise(x, z)*AMPLITUDE;
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
