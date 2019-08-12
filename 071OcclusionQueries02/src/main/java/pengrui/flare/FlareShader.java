package pengrui.flare;

import static pengrui.config.CommonConstant.LOG_LENGTH;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;

public class FlareShader implements Shaderable{

	private static final String VERTEX_SHADER_FILE = "shaders/flareVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/flareFragmentShader";

	public int programID;
	public int vertexShaderID;
	public int fragmentShaderID;
	
	public int location_brightness;
	public int location_flareTexture;
	public int location_transform;
	
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
	
	public static FlareShader createShader(List<Shaderable> shaders) {
		FlareShader shader = new FlareShader();
		shaders.add(shader);
		shader.vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(shader.vertexShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(VERTEX_SHADER_FILE)));
		GL20.glCompileShader(shader.vertexShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.vertexShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.vertexShaderID, LOG_LENGTH));
		}
		shader.fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(shader.fragmentShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(FRAGMENT_SHADER_FILE)));
		GL20.glCompileShader(shader.fragmentShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.fragmentShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.fragmentShaderID, LOG_LENGTH));
		}
		shader.programID = GL20.glCreateProgram();
		GL20.glAttachShader(shader.programID, shader.vertexShaderID);
		GL20.glAttachShader(shader.programID, shader.fragmentShaderID);
		
		GL20.glBindAttribLocation(shader.programID, 0, "in_position");
		GL20.glLinkProgram(shader.programID);
		GL20.glValidateProgram(shader.programID);
		if(GL11.GL_FALSE == GL20.glGetProgrami(shader.programID, GL20.GL_LINK_STATUS)) {
			throw new RuntimeException(GL20.glGetProgramInfoLog(shader.programID, LOG_LENGTH));
		}
		shader.location_brightness = GL20.glGetUniformLocation(shader.programID, "brightness");
		shader.location_flareTexture = GL20.glGetUniformLocation(shader.programID,"flareTexture");
		shader.location_transform = GL20.glGetUniformLocation(shader.programID, "transform");
		
		GL20.glUseProgram(shader.programID);
		GL20.glUniform1i(shader.location_flareTexture, 0);
		GL20.glUseProgram(0);
		return shader;
	}

}
