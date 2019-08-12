package pengrui.postProcessing;

import static pengrui.config.CommonConstant.LOG_LENGTH;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_INDEX;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_NAME;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;

public class HorizontalBlurShader implements Shaderable{

	private static final String VERTEX_SHADER_FILE = "shaders/gaussianHorizontalBlurVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/gaussianBlurFragmentShader";
	
	public int programID;
	public int fragmentShaderID;
	public int vertexShaderID;
	
	public int location_targetWidth;
	
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
	
	public static HorizontalBlurShader createShader(List<Shaderable> shaders) {
		HorizontalBlurShader shader = new HorizontalBlurShader();
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
		
		GL20.glLinkProgram(shader.programID);
		GL20.glValidateProgram(shader.programID);
		if(GL11.GL_FALSE == GL20.glGetProgrami(shader.programID, GL20.GL_LINK_STATUS)) {
			throw new RuntimeException(GL20.glGetProgramInfoLog(shader.programID, LOG_LENGTH));
		}
		shader.location_targetWidth = GL20.glGetUniformLocation(shader.programID, "targetWidth");
		//once uniform
//		GL20.glUseProgram(shader.programID);
//		
//		GL20.glUseProgram(0);
		return shader;
	}

	public void loadTargetWidth(int width) {
		GL20.glUseProgram(programID);
		GL20.glUniform1f(location_targetWidth, width);
		GL20.glUseProgram(0);
	}
}
