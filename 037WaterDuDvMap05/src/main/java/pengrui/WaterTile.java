package pengrui;

import java.util.List;

public class WaterTile {
	
	public static VAO vao;
	public static float SIZE = 10;
	public static int dudvTexture;
	public float x,z,height;
	public float moveFactor;
	
	public WaterTile(float x, float z, float height) {
		this.x = x;
		this.z = z;
		this.height = height;
	}


	private static void loadVao(List<Integer>vaos,List<Integer>vbos) {
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


	public static void init(List<Integer> vaos, List<Integer> vbos, List<Integer> textures) {
		loadVao(vaos,vbos);
		dudvTexture = TextureLoader.createTexture(textures, "textures/waters/waterDUDV.png");
	}
}
