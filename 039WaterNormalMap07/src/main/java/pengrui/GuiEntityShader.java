package pengrui;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static pengrui.CommonConstant.*;

public class GuiEntityShader implements Shaderable{
	
	public int vertexShaderID;
	public int fragmentShaderID;
	public int programID;
	
	public int location_transformationMatrix;
	
	private static final String VERTEX_SHADER_FILE = "shaders/guiEntityVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/guiEntityFragmentShader";

	
	public static GuiEntityShader createShader(List<Shaderable> shaders) {
		GuiEntityShader shader = new GuiEntityShader();
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
		shader.location_transformationMatrix = GL20.glGetUniformLocation(shader.programID, "transformationMatrix");
		
		//once uniform
//		GL20.glUseProgram(shader.programID);
//		GL20.glUseProgram(0);
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
