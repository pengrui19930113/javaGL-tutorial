package pengrui.reflection;

import static pengrui.config.CommonConstant.LOG_LENGTH;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;
import pengrui.util.MatrixUtil;

public class ReflectionEntityShader implements Shaderable{

	private static final String VERTEX_SHADER_FILE = "shaders/reflectionEntityVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/reflectionEntityFragmentShader";
	
	public int programID;
	public int vertexShaderID;
	public int fragmentShaderID;
	
	public int location_transformationMatrix;
	public int location_projectionMatrix;
	public int location_viewMatrix;
	public int location_modelTexture;
	public int location_cameraPosition;
	public int location_enviroMap;
	
	public static ReflectionEntityShader createShader(List<Shaderable> shaders) {
		ReflectionEntityShader shader = new ReflectionEntityShader();
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
		GL20.glBindAttribLocation(shader.programID, 0, "position");
		GL20.glBindAttribLocation(shader.programID, 1,"textureCoords");
		GL20.glBindAttribLocation(shader.programID, 2, "normal");
		
		GL20.glLinkProgram(shader.programID);
		GL20.glValidateProgram(shader.programID);
		if(GL11.GL_FALSE == GL20.glGetProgrami(shader.programID, GL20.GL_LINK_STATUS)) {
			throw new RuntimeException(GL20.glGetProgramInfoLog(shader.programID, LOG_LENGTH));
		}
		shader.location_transformationMatrix = GL20.glGetUniformLocation(shader.programID, "transformationMatrix");
		shader.location_projectionMatrix = GL20.glGetUniformLocation(shader.programID, "projectionMatrix");
		shader.location_viewMatrix = GL20.glGetUniformLocation(shader.programID, "viewMatrix");
		shader.location_modelTexture = GL20.glGetUniformLocation(shader.programID, "modelTexture");
		shader.location_cameraPosition = GL20.glGetUniformLocation(shader.programID,"cameraPosition");
		shader.location_enviroMap = GL20.glGetUniformLocation(shader.programID, "enviroMap");
		// once uniform
		GL20.glUseProgram(shader.programID);
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		MatrixUtil.createProjectionMatrix().store(buf);
		buf.flip();
		GL20.glUniformMatrix4(shader.location_projectionMatrix, false, buf);
		// connect texture unit
		GL20.glUniform1i(shader.location_modelTexture, 0);
		GL20.glUniform1i(shader.location_enviroMap, 1);
		GL20.glUseProgram(0);
		return shader;
	}

	@Override
	public int getProgramID() {
		return programID;
	}

	@Override
	public int getFragmentShaderID() {
		return fragmentShaderID;
	}

	@Override
	public int getVertexShaderID() {
		return vertexShaderID;
	}
	
}
