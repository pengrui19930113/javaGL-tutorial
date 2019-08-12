package pengrui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

import static pengrui.CommonConstant.*;
/**
 *
 * Multiple Lighting
 */
public class MainGameLoop {
	public static final int WIDTH = 1024, HEIGHT = 768, WINDOW_X_POSITION = 0, WINDOW_Y_POSITION = 0, FPS = 60;
	static final String WINDOW_TITLE = "Hello Multiple Lighting";

	public static void main(String[] args) {
		initialize();
		long frameStartTime = System.currentTimeMillis();
		List<Integer> vbos = new LinkedList<Integer>();
		List<Integer> vaos = new LinkedList<Integer>();
		List<Shaderable> shaders = new LinkedList<Shaderable>();
		List<Integer> textures = new LinkedList<Integer>();
		
		EntityShader entityShader = EntityShader.createShader(shaders);
		List<Entity> entities = null ; //createEntities(vaos,vbos,textures);
		
		TerrainShader terrainShader = TerrainShader.createShader(shaders);
		Terrain terrain = new Terrain(
				VAO.createTerrainVao(vaos, vbos,"textures/heightmap.png")
				,TextureLoader.createTexture(textures, "textures/grassy.png")
				,TextureLoader.createTexture(textures, "textures/dirt.png")
				,TextureLoader.createTexture(textures, "textures/pinkFlowers.png")
				,TextureLoader.createTexture(textures, "textures/path.png")
				,TextureLoader.createTexture(textures, "textures/blendMap.png")
				);
		PlayerShader playerShader = PlayerShader.createShader(shaders);
		Player player = new Player(VAO.createObjVao(vaos, vbos,"models/stanfordBunny.obj")
				,TextureLoader.createTexture(textures, "textures/white.png"));
		
		Camera camera = new Camera(player);
		List<Light> lights = createLights();
		
		GuiEntityShader guiShader = null;// GuiEntityShader.createShader(shaders);
		List<GuiEntity> guis = null;//createGuis(vaos,vbos,textures);
	
		while (!Display.isCloseRequested()) {
			long oldTime = frameStartTime;
			frameStartTime = System.currentTimeMillis();
			long delta = frameStartTime - oldTime;
			
			event(camera, entities, lights, terrain, player);
			logic(camera, entities, lights, terrain, player,delta);
			draw(entityShader,entities,camera,lights,terrainShader,terrain,playerShader,player,guiShader,guis);
			//control FPS and update
			Display.sync(FPS);
			Display.update();
		}
		destroy(vaos, vbos, shaders, textures);
	}


	private static List<Light> createLights() {
		List<Light> list = new ArrayList<Light>(Light.MAX_LIGHTS);
		list.add(new Light(-2,0,-2,1,1,1));
		list.add(new Light(2,0,-2,0,0,1));
		list.add(new Light(2,0,2,0,1,0));
		list.add(new Light(-2,0,2,1,0,0));
		return list;
	}


	static void logic(Camera camera, List<Entity> entities, List<Light> lights, Terrain terrain, Player player, long delta) {
		ActionManager.action(camera, entities, lights, terrain, player,delta);
	}
	
	static void draw(EntityShader endityShader,List<Entity> entities,Camera camera
			,List<Light> lights, TerrainShader terrainShader, Terrain terrain, PlayerShader playerShader, Player player, GuiEntityShader guiShader, List<GuiEntity> guis) {
		DrawManager.draw(endityShader, entities, camera, lights, terrainShader, terrain, playerShader, player,guiShader,guis);
	}
	
	public static void event(Camera camera, List<Entity> entities, List<Light> lights, Terrain terrain, Player player) {
		EventManager.event(camera, entities, lights, terrain, player);
	}

	static void initialize() {
		System.getProperties().setProperty("org.lwjgl.librarypath", "D:\\.m2\\repository\\org\\lwjgl\\lwjgl\\lwjgl-platform\\2.9.3");
		Display.setLocation(WINDOW_X_POSITION, WINDOW_Y_POSITION);
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(),
					new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCompatibility(true));
			Display.setTitle(WINDOW_TITLE);
//			GL11.glEnable(GL11.GL_DEPTH_TEST);
//			GL11.glEnable(GL11.GL_CULL_FACE);
//			GL11.glEnable(GL11.GL_TEXTURE_2D);
//			GL11.glDisable(GL11.GL_LIGHTING);
//			GL11.glDisable(GL11.GL_TEXTURE);
//			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glClearColor(SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE, 1);// set background colour;
			
			int a = GL11.glGetInteger(GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS);
			System.out.println(a); //4096
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	static void destroy(List<Integer> vaos, List<Integer> vbos, List<Shaderable> shaders, List<Integer> textures) {
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (Shaderable shader : shaders) {
			GL20.glDetachShader(shader.getProgramID(), shader.getVertexShaderID());
			GL20.glDetachShader(shader.getProgramID(), shader.getFragmentShaderID());
			if(shader.getGeometryShaderID()>0) {
				GL20.glDetachShader(shader.getProgramID(), shader.getGeometryShaderID());
				GL20.glDeleteShader(shader.getGeometryShaderID());
			}
			GL20.glDeleteShader(shader.getVertexShaderID());
			GL20.glDeleteShader(shader.getFragmentShaderID());
			GL20.glDeleteProgram(shader.getProgramID());
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
		Display.destroy();
	}

	static List<GuiEntity> createGuis(List<Integer> vaos, List<Integer> vbos, List<Integer> textures) {
		GuiEntity.createVao(vaos,vbos);
		List<GuiEntity> list = new LinkedList<GuiEntity>();
		list.add(new GuiEntity(0.8f, -0.8f, 0.2f, 0.2f, TextureLoader.createTexture(textures, "textures/socuwan.png")));
		list.add(new GuiEntity(-0.9f, -0.9f, 0.1f, 0.1f, TextureLoader.createTexture(textures, "textures/thinmatrix.png")));
		return list;
	}
	
	static List<Entity> createEntities(List<Integer> vaos, List<Integer> vbos, List<Integer> textures){
		List<Entity> list = new LinkedList<Entity>();
		VAO shareVao = VAO.createObjVao(vaos, vbos,"models/fern.obj");
		int shareTexture = TextureLoader.createTexture(textures,"textures/fern.png");
		int textureNumberOfRow = 2;
		for(int i=0;i<6;i++) {
			Entity e = new Entity(shareVao,shareTexture,i%(textureNumberOfRow*textureNumberOfRow),textureNumberOfRow);
			e.hasTransparency = true;
			e.entityPosX = 0.8f*i;
			e.entityPosY = 0.5f;
			e.entityPosZ = -0.8f*i;
			list.add(e);
		}
		return list;
	}
	
}
