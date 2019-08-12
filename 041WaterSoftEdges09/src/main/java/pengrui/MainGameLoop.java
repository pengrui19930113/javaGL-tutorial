package pengrui;

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
 * Water Soft Edges
 */
public class MainGameLoop {
	public static final int WIDTH = 1024, HEIGHT = 768, WINDOW_X_POSITION = 0, WINDOW_Y_POSITION = 0, FPS = 60;
	static final String WINDOW_TITLE = "Hello Water Soft Edges";

	public static void main(String[] args) {
		initialize();
		long frameStartTime = System.currentTimeMillis();
		List<Integer> vbos = new LinkedList<Integer>();
		List<Integer> vaos = new LinkedList<Integer>();
		List<Shaderable> shaders = new LinkedList<Shaderable>();
		List<Integer> textures = new LinkedList<Integer>();
		List<Integer> framebuffers = new LinkedList<Integer>();
		List<Integer> renderBuffers = new LinkedList<Integer>();
		
		SkyboxShader skyboxShader = SkyboxShader.createShader(shaders);
		Skybox skybox = new Skybox(vaos,vbos,textures);
		
		EntityShader entityShader = EntityShader.createShader(shaders);
		List<Entity> entities = null;//createEntities(vaos,vbos,textures);
		
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
		Light light = new Light();
		
		WaterShader waterShader = WaterShader.createShader(shaders);
		WaterTile.init(vaos, vbos,textures);
		WaterTile water = new WaterTile(0,0,0);
		WaterFramebuffers waterFramebuffers = new WaterFramebuffers(textures, framebuffers, renderBuffers);
		
		GuiEntityShader guiShader = GuiEntityShader.createShader(shaders);
		List<GuiEntity> guis = createGuis(vaos,vbos,textures,waterFramebuffers);
	
		while (!Display.isCloseRequested()) {
			long oldTime = frameStartTime;
			frameStartTime = System.currentTimeMillis();
			long delta = frameStartTime - oldTime;
			
			EventManager.event(camera, entities, light, terrain, player, skybox);
			ActionManager.action(camera, entities, light, terrain, player,skybox,water,delta);
			DrawManager.draw(entityShader,entities,camera,light,terrainShader,terrain,playerShader,player,guiShader,guis,skyboxShader,skybox,waterShader,water,waterFramebuffers);
			//control FPS and update
			Display.sync(FPS);
			Display.update();
		}
		destroy(vaos, vbos, shaders, textures,framebuffers,renderBuffers);
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
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	static void destroy(List<Integer> vaos, List<Integer> vbos, List<Shaderable> shaders, List<Integer> textures,List<Integer> framebuffers,List<Integer> renderBuffers) {
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
		for(int renderbuffer:renderBuffers) {
			GL30.glDeleteRenderbuffers(renderbuffer);
		}
		for(int framebuffer:framebuffers) {
			GL30.glDeleteFramebuffers(framebuffer);
		}
		Display.destroy();
	}

	static List<GuiEntity> createGuis(List<Integer> vaos, List<Integer> vbos, List<Integer> textures, WaterFramebuffers waterFramebuffers) {
		GuiEntity.createVao(vaos,vbos);
		List<GuiEntity> list = new LinkedList<GuiEntity>();
		list.add(new GuiEntity(0.8f, -0.8f, 0.2f, 0.2f, waterFramebuffers.reflectionTexture));
		list.add(new GuiEntity(-0.8f, -0.8f, 0.2f, 0.2f, waterFramebuffers.refractionTexture));
		return list;
	}
	
	static List<Entity> createEntities(List<Integer> vaos, List<Integer> vbos, List<Integer> textures){
		List<Entity> list = new LinkedList<Entity>();
		VAO shareVao = VAO.createObjVao(vaos, vbos,"models/box.obj");
		int shareTexture = TextureLoader.createTexture(textures,"textures/box.png");
		for(int i=0;i<6;i++) {
			Entity e = new Entity(shareVao,shareTexture);
			e.entityPosX = 0.8f*i;
			e.entityPosY = 1f;
			e.entityPosZ = -0.8f*i;
			list.add(e);
		}
		return list;
	}
	
}
