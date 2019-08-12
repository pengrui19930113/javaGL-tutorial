package pengrui.skybox;

import java.util.List;

import pengrui.common.TextureLoader;
import pengrui.common.Textureable;
import pengrui.common.VAO;

public class Skybox implements Textureable{
	
	public VAO vao;
	public int texture;
	
	public Skybox(List<Integer>vaos,List<Integer>vbos,List<Integer>textures) {
		vao = SkyboxVaoLoader.createSkyboxVao(vaos, vbos, SKYBOX);
		texture = TextureLoader.createCubeMap(textures, ENVIRO_MAP_INSIDE);
	}
	private static float SIZE = 100;

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
	static final String[] ENVIRO_MAP_SNOW = {"cposx", "cnegx", "cposy", "cnegy", "cposz", "cnegz"};
	static final String[] ENVIRO_MAP_LAKE = {"posx", "negx", "posy", "negy", "posz", "negz"};
	static final String[] ENVIRO_MAP_INSIDE = {"lposx", "lnegx", "lposy", "lnegy", "lposz", "lnegz"};

	@Override
	public int getTexture() {
		return texture;
	}
}
