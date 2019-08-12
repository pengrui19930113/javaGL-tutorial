package pengrui;

import java.util.List;

public class WaterTile {
	
	public static VAO vao;
	public float x,z,height;
	
	public static float SIZE = 5;
	public WaterTile(float x, float z, float height) {
		this.x = x;
		this.z = z;
		this.height = height;
	}


	public static void loadVao(List<Integer>vaos,List<Integer>vbos) {
		float[] VERTICES = {
				-1,-1
				,-1,1
				,1,-1
				,1,-1
				,-1,1
				,1,1
		};
		vao = VAO.createWaterVao(vaos, vbos,VERTICES);
	}
}
