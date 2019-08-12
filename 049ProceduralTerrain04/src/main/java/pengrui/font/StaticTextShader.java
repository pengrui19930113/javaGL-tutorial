package pengrui.font;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;

import static pengrui.config.CommonConstant.*;

public class StaticTextShader implements Shaderable{

	static final String VERTEX_SHADER_FILE = "shaders/fontVertexShader";
	/**
	 * 将常量 用 uniform 代替，有主程序传递相关变量控制字体效果 可实现静态图片的动画效果
	 */
	static final String FRAGMENT_SHADER_FILE = "shaders/fontFragmentShader";
	
	public int programID;
	public int vertexShaderID;
	public int fragmentShaderID;
	
	public int location_colour;
	public int location_translation;
	public int location_width;
	public int location_edge;
	public int location_borderWidth;
	public int location_borderEdge;
	public int location_offset;
	public int location_outlineColour;
	
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
	
	public static StaticTextShader createShader(List<Shaderable> shaders) {
		StaticTextShader shader = new StaticTextShader();
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
		//bind
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_POSITION_INDEX, PROGRAM_ATTRIBUTE_POSITION_NAME);
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX, PROGRAM_ATTRIBUTE_TEXTURE_COORDS_NAME);
		GL20.glLinkProgram(shader.programID);
		//get uniform location
		shader.location_colour = GL20.glGetUniformLocation(shader.programID, "colour");
		shader.location_translation = GL20.glGetUniformLocation(shader.programID, "translation");
		shader.location_width = GL20.glGetUniformLocation(shader.programID, "width");
		shader.location_edge = GL20.glGetUniformLocation(shader.programID, "edge");
		shader.location_borderWidth = GL20.glGetUniformLocation(shader.programID, "borderWidth");
		shader.location_borderEdge = GL20.glGetUniformLocation(shader.programID, "borderEdge");
		shader.location_offset = GL20.glGetUniformLocation(shader.programID,"offset");
		shader.location_outlineColour = GL20.glGetUniformLocation(shader.programID,"outlineColour");
		return shader;
	}
}
