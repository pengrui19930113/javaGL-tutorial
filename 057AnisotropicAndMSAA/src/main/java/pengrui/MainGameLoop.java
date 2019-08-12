package pengrui;

import static pengrui.config.CommonConstant.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

import pengrui.common.Camera;
import pengrui.common.Light;
import pengrui.common.ObjVaoLoader;
import pengrui.common.Shaderable;
import pengrui.common.TextureLoader;
import pengrui.common.VAO;
import pengrui.entity.Entity;
import pengrui.entity.EntityShader;
import pengrui.font.Font;
import pengrui.font.FontEffect;
import pengrui.font.StaticText;
import pengrui.font.StaticTextShader;
import pengrui.gui.GuiEntity;
import pengrui.gui.GuiEntityShader;
import pengrui.manager.ActionManager;
import pengrui.manager.DrawManager;
import pengrui.manager.EventManager;
import pengrui.normalMapping.NormalMappingEntity;
import pengrui.normalMapping.NormalMappingEntityShader;
import pengrui.normalMapping.NormalMappingEntityVaoLoader;
import pengrui.player.Player;
import pengrui.player.PlayerShader;
import pengrui.skybox.Skybox;
import pengrui.skybox.SkyboxShader;
import pengrui.terrain.IHeightGenerator;
import pengrui.terrain.Terrain;
import pengrui.terrain.TerrainShader;
import pengrui.terrain.TerrainVaoLoader;
/**
 *
 * Anisotropic And MSAA
 */
public class MainGameLoop {
	
	static final String WINDOW_TITLE = "Hello Anisotropic And MSAA";

	public static void main(String[] args) {
		initialize();
		
		long frameStartTime = System.currentTimeMillis();
		
		List<Integer> vbos = new LinkedList<Integer>();
		List<Integer> vaos = new LinkedList<Integer>();
		List<Shaderable> shaders = new LinkedList<Shaderable>();
		List<Integer> textures = new LinkedList<Integer>();
		
		TerrainShader terrainShader = TerrainShader.createShader(shaders);
		Terrain terrain = createTerrain(vaos,vbos,textures);
	
		EntityShader entityShader = EntityShader.createShader(shaders);
		List<Entity> entities = createEntities(vaos,vbos,textures,terrain);
	
		SkyboxShader skyboxShader = SkyboxShader.createShader(shaders);
		Skybox skybox = new Skybox(vaos,vbos,textures);
		
		PlayerShader playerShader = PlayerShader.createShader(shaders);
		Player player = new Player(ObjVaoLoader.createObjVao(vaos, vbos,"models/stanfordBunny.obj")
				,TextureLoader.createTexture(textures, "textures/white.png"));
	
		Camera camera = new Camera(player);
		Light light = new Light();
		
		NormalMappingEntityShader normalMappingEntityShader = NormalMappingEntityShader.createShader(shaders);
		List<NormalMappingEntity> normalEntities = null; //createNormalMappingEntities(vaos,vbos,textures);
		
		StaticTextShader textShader = StaticTextShader.createShader(shaders);
		FontEffect fontEffect = new FontEffect();
		List<StaticText> texts = Collections.emptyList();// createTexts(textures);
	
		GuiEntityShader guiShader = GuiEntityShader.createShader(shaders);
		List<GuiEntity> guis = createGuis(vaos,vbos,textures);

		while (!Display.isCloseRequested()) {
			long oldTime = frameStartTime;
			frameStartTime = System.currentTimeMillis();
			long delta = frameStartTime - oldTime;
			
			EventManager.event(camera, entities, light, terrain, player, skybox);
			ActionManager.action(camera, entities, light, terrain, player,skybox,normalEntities,texts,fontEffect,delta);
			DrawManager.draw(entityShader, entities, camera, light
					, terrainShader, terrain, playerShader, player
					,guiShader,guis,skyboxShader,skybox
					,normalMappingEntityShader,normalEntities
					,textShader,texts,fontEffect
					);
			//control FPS and update
			Display.sync(FPS);
			Display.update();
		}
		destroy(vaos, vbos, shaders, textures,texts);
	}

	static void printShaderMessage(List<Shaderable> shaders) {
		for(Shaderable shader:shaders) 
			System.out.println(shader.getClass().getSimpleName()+":"+GL20.glGetProgramInfoLog(shader.getProgramID(), LOG_LENGTH));
		//Validation warning! - Sampler value texSampler has not been set.Validation successful.
	}
	
	static void initialize() {
		System.getProperties().setProperty("org.lwjgl.librarypath", "D:\\.m2\\repository\\org\\lwjgl\\lwjgl\\lwjgl-platform\\2.9.3");
		Display.setLocation(WINDOW_X_POSITION, WINDOW_Y_POSITION);
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withSamples(8),
					new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCompatibility(true));
			Display.setTitle(WINDOW_TITLE);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
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
	
	static void destroy(List<Integer> vaos, List<Integer> vbos, List<Shaderable> shaders, List<Integer> textures, List<StaticText> texts) {
		
		for(StaticText text:texts) {
			text.destroy();
		}
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
//		list.add(new GuiEntity(0.8f, -0.8f, 0.2f, 0.2f, TextureLoader.createTexture(textures, "textures/socuwan.png")));
//		list.add(new GuiEntity(-0.9f, -0.9f, 0.1f, 0.1f, TextureLoader.createTexture(textures, "textures/thinmatrix.png")));
		return list;
	}
	
	static List<Entity> createEntities(List<Integer> vaos, List<Integer> vbos, List<Integer> textures, Terrain terrain){
//		Random random = new Random();
//		final float RANGE = TERRAIN_ONE_LINE_SIZE;
		List<Entity> list = new LinkedList<Entity>();
		VAO shareVao = ObjVaoLoader.createObjVao(vaos, vbos,"models/box.obj");
		int shareTexture = TextureLoader.createTexture(textures,"textures/box.png");
		
		for(int i=0;i<1;i++) {
			Entity e = new Entity(shareVao,shareTexture);
//			float x = RANGE*(random.nextFloat()-0.5f);
//			float z = RANGE*(random.nextFloat()-0.5f);
//			float y = terrain.getHeightOfTerrain(x, z);
			e.xPos = 0;
			e.yPos = 5;
			e.zPos = 0;
			list.add(e);
		}
		return list;
	}

	static List<NormalMappingEntity> createNormalMappingEntities(List<Integer> vaos, List<Integer> vbos,
			List<Integer> textures) {
		List<NormalMappingEntity> list = new LinkedList<NormalMappingEntity>();
		NormalMappingEntity entity = new NormalMappingEntity(
				NormalMappingEntityVaoLoader.createNormalMappingEntityVao(vaos, vbos, "models/barrel.obj")
				,TextureLoader.createTexture(textures, "textures/barrel.png", -2.5f)
				,TextureLoader.createTexture(textures, "textures/barrelNormal.png", -2.5f)
				);
		list.add(entity);
		return list;
	}
	
	static List<StaticText> createTexts(List<Integer> textures) {
		List<StaticText> list = new LinkedList<StaticText>();
		Font font = new Font("font/candara.fnt", TextureLoader.createTexture(textures, "font/candara.png"));
//		list.add(new StaticText(-1f,-1f, "0OZabcdefghijklmnopqrstuvwxyz 0OZabcdefghijklmnopqrstuvwxyz 0OZabcdefghijkl", 512, font));
//		list.add(new StaticText(0.3f, 0.75f, "0OZabcdefghijklmnopqrstuvwxyz", 1024, font));
		StaticText text = new StaticText(0f, 0f, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz·~!@#$%^&*()_-+=[]{};:'\"\\|,.<>/ ?", 256, font);
//		text.red = 0.9f;
//		text.green = 0.3f;
//		text.blue = 0.6f;
		//将文本放在窗口左上角
		text.xPos = -1;
		text.yPos -= text.numberOfLines*text.ndcLineHight*text.fontSize; //将文本上部的纵坐标设置为0
		text.yPos += 1;//将文本上部的纵坐标设置为1
		list.add(text);
		return list;
	}

	private static Terrain createTerrain(List<Integer> vaos, List<Integer> vbos, List<Integer> textures) {
		Terrain terrain = null;
		terrain = new Terrain(
				TerrainVaoLoader.createTerrainVao(vaos, vbos,IHeightGenerator.getGenerator())
				,TextureLoader.createTexture(textures, "textures/grassy.png")
				,TextureLoader.createTexture(textures, "textures/dirt.png")
				,TextureLoader.createTexture(textures, "textures/pinkFlowers.png")
				,TextureLoader.createTexture(textures, "textures/path.png")
				,TextureLoader.createTexture(textures, "textures/blendMap.png")
				);
		return terrain;
	}

}
