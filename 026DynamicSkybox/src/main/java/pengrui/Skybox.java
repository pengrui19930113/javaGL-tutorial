package pengrui;

import java.util.List;

public class Skybox {
	
	public VAO vao;
	public int dayTexture;;
	public int nightTexture;
	
	public float time = 0;
	public float rotation = 0;
	public int texture1;
	public int texture2;
	public float blendFactor;
	
	public Skybox(List<Integer>vaos,List<Integer>vbos,List<Integer>textures) {
		vao = VAO.createSkyboxVao(vaos, vbos, SKYBOX);
		dayTexture = TextureLoader.createCubeMap(textures, DAY_TEXTURE_FILES);
		nightTexture = TextureLoader.createCubeMap(textures, NIGHT_TEXTURE_FILES);
	}
	private static float SIZE = 5;

	static final float[] SKYBOX = {
			//RIGHT
			SIZE,SIZE,-SIZE
			,SIZE,-SIZE,-SIZE
			,SIZE,-SIZE,SIZE
			,SIZE,SIZE,-SIZE
			,SIZE,-SIZE,SIZE
			,SIZE,SIZE,SIZE 
			//LEFT
			,-SIZE,SIZE,SIZE
			,-SIZE,-SIZE,SIZE
			,-SIZE,-SIZE,-SIZE
			,-SIZE,SIZE,SIZE
			,-SIZE,-SIZE,-SIZE
			,-SIZE,SIZE,-SIZE
			//TOP
			,-SIZE,SIZE,SIZE
			,-SIZE,SIZE,-SIZE
			,SIZE,SIZE,-SIZE
			,-SIZE,SIZE,SIZE
			,SIZE,SIZE,-SIZE
			,SIZE,SIZE,SIZE
			//BOTTOM
			,-SIZE,-SIZE,-SIZE
			,-SIZE,-SIZE,SIZE
			,SIZE,-SIZE,SIZE
			,-SIZE,-SIZE,-SIZE
			,SIZE,-SIZE,SIZE
			,SIZE,-SIZE,-SIZE
			//BACK
			,SIZE,SIZE,SIZE
			,SIZE,-SIZE,SIZE
			,-SIZE,-SIZE,SIZE
			,SIZE,SIZE,SIZE
			,-SIZE,-SIZE,SIZE
			,-SIZE,SIZE,SIZE
			//FRONT
			,-SIZE,SIZE,-SIZE
			,-SIZE,-SIZE,-SIZE
			,SIZE,-SIZE,-SIZE
			,-SIZE,SIZE,-SIZE
			,SIZE,-SIZE,-SIZE
			,SIZE,SIZE,-SIZE
	};

	
	static String[] DAY_TEXTURE_FILES = {// 在天空盒内部看到的图片 左右翻转了， 可能是因为OPENGL的 算法是将图片从 立方体外侧进行贴图
			"dayRight"
			,"dayLeft"
			,"dayTop"
			,"dayBottom"
			,"dayBack"
			,"dayFront"
	};
	
	static String[] NIGHT_TEXTURE_FILES = {
			"nightRight"
			,"nightLeft"
			,"nightTop"
			,"nightBottom"
			,"nightBack"
			,"nightFront"
	};
}
