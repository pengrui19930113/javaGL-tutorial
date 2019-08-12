package pengrui;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

/**
 * DiffuseLighting
 * 
 */
public class MainGameLoop {
	public static final int WIDTH = 1024, HEIGHT = 768, WINDOW_X_POSITION = 0, WINDOW_Y_POSITION = 0, FPS = 60;
	static final String WINDOW_TITLE = "Hello DiffuseLighting";
	static float MOVE_SPEED = 0.1f;
	static float ROTATE_SPEED = 3f;
	public static void main(String[] args) {
		initialize();
		long frameStartTime = System.currentTimeMillis();
		List<Integer> vbos = new LinkedList<Integer>();
		List<Integer> vaos = new LinkedList<Integer>();
		List<Shader> shaders = new LinkedList<Shader>();
		List<Integer> textures = new LinkedList<Integer>();
		VAO vao = VAO.createObjVao(vaos, vbos,ClassLoader.getSystemResourceAsStream("models/box.obj"));
		Shader shader = Shader.createShader(shaders);
		int texture = TextureLoader.createTexture(textures);

		Entity entity = new Entity();
		Camera camera = new Camera();
		Light light = new Light();
		GL11.glClearColor(0.2f, 0.3f, 0.4f, 1);// set background colour;
		while (!Display.isCloseRequested()) {
			long oldTime = frameStartTime;
			frameStartTime = System.currentTimeMillis();
			long delta = frameStartTime - oldTime;
			event(camera,entity,light);
			
			logic(camera,entity,delta);
			
			draw(shader,vao,entity,camera,texture,light);
			//control FPS and update
			Display.sync(FPS);
			Display.update();
		}
		destroy(vaos, vbos, shaders, textures);
	}

	static void logic(Camera camera, Entity entity,long delta) {
		entity.entityRotX += (20.0*(delta)/1000);
		entity.entityRotX %= 360;
		entity.entityRotY += (10.0*(delta)/1000);
		entity.entityRotY %= 360;
		entity.entityRotZ += (15.0*(delta)/1000);
		entity.entityRotZ %= 360;
	}
	
	static void draw(Shader shader,VAO vao,Entity entity,Camera camera,int texture, Light light) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
//		GL11.glCullFace(GL11.GL_FRONT);
		GL11.glCullFace(GL11.GL_BACK);
//		GL11.glFrontFace(GL11.GL_CW);
		GL11.glFrontFace(GL11.GL_CCW);//默认 逆时针
		
		//indicate shader program
		GL20.glUseProgram(shader.programID);
		//bind vao
		GL30.glBindVertexArray(vao.vaoid);
		//enable vertex attribute
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		//load uniform matrix
		FloatBuffer transformBuffer = MatrixUtil.caculateTransformMatrix(entity);
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, transformBuffer);
		FloatBuffer viewBuffer = MatrixUtil.caculateViewMatrix(camera);
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, viewBuffer);
		GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
		GL20.glUniform3f(shader.location_lightColour,light.red,light.green,light.blue);
		//bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);// indicate the texture unit
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		//draw
		GL11.glDrawElements(GL11.GL_TRIANGLES, vao.vertexCount, GL11.GL_UNSIGNED_INT, 0);// use draw array
		//disable vertex attribute
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		//unbind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		//unbind vao
		GL30.glBindVertexArray(0);
		//unuse shader program
		GL20.glUseProgram(0);

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
		
		if(Keyboard.isKeyDown(Keyboard.KEY_O)) light.xPos -= MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_P)) light.xPos += MOVE_SPEED;
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
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	static void destroy(List<Integer> vaos, List<Integer> vbos, List<Shader> shaders, List<Integer> textures) {
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (Shader shader : shaders) {
			GL20.glDetachShader(shader.programID, shader.vertexShaderID);
			GL20.glDetachShader(shader.programID, shader.fragmentShaderID);
			GL20.glDeleteShader(shader.vertexShaderID);
			GL20.glDeleteShader(shader.fragmentShaderID);
			GL20.glDeleteProgram(shader.programID);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
		Display.destroy();
	}
}
