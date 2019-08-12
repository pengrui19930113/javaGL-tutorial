package pengrui.shadow;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;

import static pengrui.config.CommonConstant.*;

public class ShadowShader implements Shaderable{

	private static final String VERTEX_SHADER_FILE = "shaders/shadowVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/shadowFragmentShader";
	
	public int vertexShaderID;
	public int fragmentShaderID;
	public int programID;
	
	public int location_mvpMatrix;
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
	
	public static ShadowShader createShader(List<Shaderable> shaders) {
		ShadowShader shader = new ShadowShader();
		shaders.add(shader);
		shader.vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(shader.vertexShaderID,InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(VERTEX_SHADER_FILE)));
		GL20.glCompileShader(shader.vertexShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.vertexShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.vertexShaderID, LOG_LENGTH));
		}
		shader.fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(shader.fragmentShaderID,InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(FRAGMENT_SHADER_FILE)));
		GL20.glCompileShader(shader.fragmentShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.fragmentShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.vertexShaderID, LOG_LENGTH));
		}
		shader.programID = GL20.glCreateProgram();
		GL20.glAttachShader(shader.programID, shader.vertexShaderID);
		GL20.glAttachShader(shader.programID, shader.fragmentShaderID);
		GL20.glBindAttribLocation(shader.programID, 0, "in_position"); // 顶点着色器 的输入 根据  location 来识别
		GL20.glLinkProgram(shader.programID);
		GL20.glValidateProgram(shader.programID);
		if(GL11.GL_FALSE == GL20.glGetProgrami(shader.programID, GL20.GL_LINK_STATUS)) {
			throw new RuntimeException(GL20.glGetProgramInfoLog(shader.programID, LOG_LENGTH));
		}
		shader.location_mvpMatrix = GL20.glGetUniformLocation(shader.programID, "mvpMatrix");
		return shader;
	}
	
}
