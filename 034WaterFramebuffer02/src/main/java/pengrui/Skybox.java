package pengrui;

import java.util.List;

public class Skybox {
	
	public VAO vao;
	public int texture;
	
	public Skybox(List<Integer>vaos,List<Integer>vbos,List<Integer>textures) {
		vao = VAO.createSkyboxVao(vaos, vbos, SKYBOX);
		texture = TextureLoader.createCubeMap(textures, TEXTURE_FILES);
	}
	private static float SIZE = 50;

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
	static String[] TEXTURE_FILES = {// 在天空盒内部看到的图片 左右翻转了， 可能是因为OPENGL的 算法是将图片从 立方体外侧进行贴图
			"right"
			,"left"
			,"top"
			,"bottom"
			,"back"
			,"front"
	};
}
