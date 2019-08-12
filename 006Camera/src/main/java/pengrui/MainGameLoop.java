package pengrui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;
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
import org.lwjgl.util.vector.Matrix4f;

/**
 * Camera
 * 
 */
public class MainGameLoop {
	public static final int WIDTH = 1024, HEIGHT = 768, WINDOW_X_POSITION = 0, WINDOW_Y_POSITION = 0, FPS = 60;
	static final String WINDOW_TITLE = "Hello Camera";
	static float MOVE_SPEED = 0.1f;
	static float ROTATE_SPEED = 3f;
	public static void main(String[] args) {
		System.getProperties().setProperty("org.lwjgl.librarypath", "D:\\.m2\\repository\\org\\lwjgl\\lwjgl\\lwjgl-platform\\2.9.3");
		long frameStartTime = System.currentTimeMillis();
		Display.setLocation(WINDOW_X_POSITION, WINDOW_Y_POSITION);
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(),
					new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCompatibility(true));
			Display.setTitle(WINDOW_TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		List<Integer> vbos = new LinkedList<Integer>();
		List<Integer> vaos = new LinkedList<Integer>();
		List<Shader> shaders = new LinkedList<Shader>();
		List<Integer> textures = new LinkedList<Integer>();
		VAO vao = createVao(vaos, vbos);
		Shader shader = createShader(shaders);
		Matrix4f projectionMatrix = MatrixUtil.createProjectionMatrix();
		GL20.glUseProgram(shader.programID);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		projectionMatrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(shader.location_projectionMatrix,false,buffer);
		buffer = null;
		GL20.glUseProgram(shader.programID);
		int texture = TextureLoader.createTexture(textures);

		float entityPosX = 0, entityPosY = 0, entityPosZ = -5f;//-0.100001f;
		float entityRotX = 0, entityRotY = 0, entityRotZ = 0;
		float entityScaX = 1, entityScaY = 1, entityScaZ = 1;
		
		float cameraPosX = 0, cameraPosY = 0, cameraPosZ = 0;
		float cameraRotX = 0, cameraRotY = 0, cameraRotZ = 0;// pitch(-90,90) yaw[0,360) roll[0,360)

		GL11.glClearColor(0.2f, 0.3f, 0.4f, 1);// set background colour;
		while (!Display.isCloseRequested()) {
			long oldTime = frameStartTime;
			frameStartTime = System.currentTimeMillis();
			/**
			 * event
			 */
			if(Keyboard.isKeyDown(Keyboard.KEY_W)) cameraPosZ -=MOVE_SPEED;
			if(Keyboard.isKeyDown(Keyboard.KEY_S)) cameraPosZ +=MOVE_SPEED;
			if(Keyboard.isKeyDown(Keyboard.KEY_A)) cameraPosX -=MOVE_SPEED;
			if(Keyboard.isKeyDown(Keyboard.KEY_D)) cameraPosX +=MOVE_SPEED;
//			if(Keyboard.isKeyDown(Keyboard.KEY_O)) cameraPosY +=MOVE_SPEED;
//			if(Keyboard.isKeyDown(Keyboard.KEY_P)) cameraPosY -=MOVE_SPEED;
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) cameraRotY +=ROTATE_SPEED;
			if(Keyboard.isKeyDown(Keyboard.KEY_E)) cameraRotY -=ROTATE_SPEED;
//			if(Keyboard.isKeyDown(Keyboard.KEY_M)) cameraRotX -=ROTATE_SPEED;
//			if(Keyboard.isKeyDown(Keyboard.KEY_N)) cameraRotX +=ROTATE_SPEED;
//			if(Keyboard.isKeyDown(Keyboard.KEY_J)) cameraRotZ +=ROTATE_SPEED;
//			if(Keyboard.isKeyDown(Keyboard.KEY_K)) cameraRotZ -=ROTATE_SPEED;
			if(cameraRotX>89) cameraRotX = 89;	if(cameraRotX<-89) cameraRotX =-89f;//避免万向节死锁
			cameraPosY %= 360; cameraRotZ %= 360;
			
			/**
			 * logic
			 */
			entityRotX += (200.0*(frameStartTime-oldTime)/1000);
			entityRotX %= 360;
			
			/**
			 * draw
			 */
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			GL20.glUseProgram(shader.programID);

			GL30.glBindVertexArray(vao.vaoid);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);

			// load uniform matrix
			FloatBuffer transformBuffer = MatrixUtil.caculateTransformMatrix(
					entityPosX, entityPosY, entityPosZ,
					entityRotX, entityRotY, entityRotZ
					, entityScaX, entityScaY, entityScaZ);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, transformBuffer);
			
			FloatBuffer viewBuffer = MatrixUtil.caculateViewMatrix(
					cameraPosX, cameraPosY, cameraPosZ,
					cameraRotX, cameraRotY, cameraRotZ);
			GL20.glUniformMatrix4(shader.location_viewMatrix, false, viewBuffer);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);// indicate the texture unit
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			GL11.glDrawElements(GL11.GL_TRIANGLES, vao.vertexCount, GL11.GL_UNSIGNED_INT, 0);// use draw array
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL30.glBindVertexArray(0);

			GL20.glUseProgram(0);

			Display.sync(FPS);
			Display.update();
		}
		destroy(vaos, vbos, shaders, textures);
	}

	static final String VERTEX_SHADER_FILE = "shaders/vertexShader";
	static final String FRAGMENT_SHADER_FILE = "shaders/fragmentShader";
	static final int LOG_LENGTH = 512;

	static final int PROGRAM_ATTRIBUTE_POSITION_INDEX = 0;
	static final String PROGRAM_ATTRIBUTE_POSITION_NAME = "position";

	static final int PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX = 1;
	static final String PROGRAM_ATTRIBUTE_TEXTURE_COORDS_NAME = "textureCoords";

	private static Shader createShader(List<Shader> shaders) {
		Shader shader = new Shader();
		shaders.add(shader);
		// vertex shader
		shader.vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(shader.vertexShaderID,
				InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(VERTEX_SHADER_FILE)));
		GL20.glCompileShader(shader.vertexShaderID);
		if (GL11.GL_FALSE == GL20.glGetShaderi(shader.vertexShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.vertexShaderID, LOG_LENGTH));
		}
		// fragment shader
		shader.fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(shader.fragmentShaderID,
				InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(FRAGMENT_SHADER_FILE)));
		GL20.glCompileShader(shader.fragmentShaderID);
		if (GL11.GL_FALSE == GL20.glGetShaderi(shader.fragmentShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.fragmentShaderID, LOG_LENGTH));
		}
		// program
		shader.programID = GL20.glCreateProgram();
		GL20.glAttachShader(shader.programID, shader.vertexShaderID);
		GL20.glAttachShader(shader.programID, shader.fragmentShaderID);

		// 也可在shader中指定数据传送的位置 ，只不过 shader 的写法略有不同 该种写法 需要对应 使用 layout (location = 0) in
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_POSITION_INDEX, PROGRAM_ATTRIBUTE_POSITION_NAME);
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX,
				PROGRAM_ATTRIBUTE_TEXTURE_COORDS_NAME);

		GL20.glLinkProgram(shader.programID);
		shader.location_projectionMatrix = GL20.glGetUniformLocation(shader.programID, "projectionMatrix");
		shader.location_transformationMatrix = GL20.glGetUniformLocation(shader.programID, "transformationMatrix");
		shader.location_viewMatrix = GL20.glGetUniformLocation(shader.programID,"viewMatrix");
		return shader;
	}

	static final float[] TRIANGLE_VERTICES_POISITION_DATA = { 
			-.5f, .5f, .0f
			, -.5f, -.5f, .0f
			, .5f, -.5f, .0f
			, .5f, .5f, .0f 
	};
	static final int[] TRIANGLE_VERTICES_INDICES_DATA = { 
			0, 1, 2
			,0, 2, 3 
	};

	static final float[] TRIANGLE_VERTICES_TEXTURE_COORDS = { 
			0, 0
			, 1, 0
			, 1, 1
			, 0, 1 
	};// 注意纹理坐标是左手系 原点在图片左上角 向右是X 向下是Y

	static VAO createVao(List<Integer> vaos, List<Integer> vbos) {
		VAO vao = new VAO();
		// load data to vao
		int vaoid = GL30.glGenVertexArrays();
		vaos.add(vaoid);
		vao.vaoid = vaoid;
		GL30.glBindVertexArray(vaoid);
		// vertex array for position
		int vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(TRIANGLE_VERTICES_POISITION_DATA.length);
		buffer.put(TRIANGLE_VERTICES_POISITION_DATA);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_POSITION_INDEX, 3, GL11.GL_FLOAT, false, 0, 0);
		// vertex array for texture coordination
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		buffer = BufferUtils.createFloatBuffer(TRIANGLE_VERTICES_TEXTURE_COORDS.length);
		buffer.put(TRIANGLE_VERTICES_TEXTURE_COORDS);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX, 2, GL11.GL_FLOAT, false, 0, 0);

		// element array
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboid);
		IntBuffer buffer2 = BufferUtils.createIntBuffer(TRIANGLE_VERTICES_INDICES_DATA.length);
		buffer2.put(TRIANGLE_VERTICES_INDICES_DATA);
		buffer2.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer2, GL15.GL_STATIC_DRAW);
		vao.vertexCount = TRIANGLE_VERTICES_INDICES_DATA.length;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		return vao;
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
