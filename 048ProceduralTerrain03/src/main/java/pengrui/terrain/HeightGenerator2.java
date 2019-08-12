package pengrui.terrain;

import java.util.Random;

public class HeightGenerator2 implements IHeightGenerator {
	private static final float AMPLITUDE = 7F;
	
	private Random random = new Random();
	private int seed;
	
	public HeightGenerator2(){
		this.seed = random.nextInt(1000000000);
//		System.out.println(this.getNoise(4, 13));
//		System.out.println(this.getNoise(4, 13));// 获得的噪音一样
//		System.out.println(this.getNoise(5, 13));//获得的噪音与上面的区别很大
	}
	
	public float generateHeight(int x,int z){
		return this.getNoise(x, z)*AMPLITUDE;
	}
	
	public float getNoise(int x,int z){
		random.setSeed(x*49632+z*325176+seed);//相同的种子next会获得相同的数
		return random.nextFloat()*2f-1f;
	}
}
