package pengrui;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 * Terrain
 */
public class MainGameLoop {
	public static final int WIDTH = 1024, HEIGHT = 768, WINDOW_X_POSITION = 0, WINDOW_Y_POSITION = 0, FPS = 60;
	static final String WINDOW_TITLE = "Hello Terrain";
	static float MOVE_SPEED = 0.1f;
	static float ROTATE_SPEED = 3f;
	public static void main(String[] args) {
		initialize();
		long frameStartTime = System.currentTimeMillis();
		List<Integer> vbos = new LinkedList<Integer>();
		List<Integer> vaos = new LinkedList<Integer>();
		List<Shaderable> shaders = new LinkedList<Shaderable>();
		List<Integer> textures = new LinkedList<Integer>();
		
		EntityShader entityShader = EntityShader.createShader(shaders);
		Entity entity = new Entity(VAO.createObjVao(vaos, vbos,"models/box.obj"),TextureLoader.createTexture(textures,"textures/box.png"));
		
		TerrainShader terrainShader = TerrainShader.createShader(shaders);
		Terrain terrain = new Terrain(VAO.createTerrainVao(vaos, vbos),TextureLoader.createTexture(textures, "textures/awesomeface.png"));
		
		Camera camera = new Camera();
		Light light = new Light();
		GL11.glClearColor(0.2f, 0.3f, 0.4f, 1);// set background colour;
		while (!Display.isCloseRequested()) {
			long oldTime = frameStartTime;
			frameStartTime = System.currentTimeMillis();
			long delta = frameStartTime - oldTime;
			event(camera,entity,light);
			
			logic(camera,entity,delta);
			
			draw(entityShader,entity,camera,light,terrainShader,terrain);
			//control FPS and update
			Display.sync(FPS);
			Display.update();
		}
		destroy(vaos, vbos, shaders, textures);
	}

	static void logic(Camera camera, Entity entity,long delta) {
//		entity.entityRotX += (20.0*(delta)/1000);
//		entity.entityRotX %= 360;
//		entity.entityRotY += (10.0*(delta)/1000);
//		entity.entityRotY %= 360;
//		entity.entityRotZ += (15.0*(delta)/1000);
//		entity.entityRotZ %= 360;
	}
	
	static void draw(EntityShader endityShader,Entity entity,Camera camera,Light light, TerrainShader terrainShader, Terrain terrain) {
		
		DrawManager.drawBegin();
		DrawManager.drawTerrain(terrainShader, camera, light, terrain);
		DrawManager.drawEntity(endityShader, camera, light, entity);
		DrawManager.drawEnd();
	}
	
	static void event(Camera camera, Entity entity, Light light) {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) camera.cameraPosZ -=MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) camera.cameraPosZ +=MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) camera.cameraPosX -=MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) camera.cameraPosX +=MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) camera.cameraRotY +=ROTATE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_E)) camera.cameraRotY -=ROTATE_SPEED;
		if(camera.cameraRotX>89) camera.cameraRotX = 89;	if(camera.cameraRotX<-89) camera.cameraRotX =-89f;//避免万向节死锁
		camera.cameraPosY %= 360; camera.cameraRotZ %= 360;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_O)) camera.cameraPosY += MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_P)) camera.cameraPosY -= MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_N)) camera.cameraRotX += ROTATE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_M)) camera.cameraRotX -= ROTATE_SPEED;
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
			GL11.glClearColor(0.2f, 0.3f, 0.2f, 1);
//			GL11.glEnable(GL11.GL_TEXTURE_2D);
//			GL11.glDisable(GL11.GL_LIGHTING);
//			GL11.glDisable(GL11.GL_TEXTURE);
//			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
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
}
