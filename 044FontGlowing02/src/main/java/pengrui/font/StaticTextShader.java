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
	 * 普通的片段着色器，通过 alpha通道的衰减可以看到字体边缘的颜色越来越淡
	 */
	static final String FRAGMENT_SHADER_FILE = "shaders/fontFragmentShader";
	/**
	 * 此时 纹理中的alpha通道相当于距离字体周线的距离 通过相关计算重新生成最终显示的alpha
	 * 该做着色器达到的效果是柔化字体的边缘
	 */
	static final String FRAGMENT_SHADER_FILE2 = "shaders/fontFragmentShader2";
	/**
	 * 添加边框 注意边框的定义查看图片
	 */
	static final String FRAGMENT_SHADER_FILE3 = "shaders/fontFragmentShader3";
	/**
	 * 添加了偏移 可以实现阴影效果
	 */
	static final String FRAGMENT_SHADER_FILE4 = "shaders/fontFragmentShader4";
	/**
	 * 将常量 用 uniform 代替，有主程序传递相关变量控制字体效果 可实现静态图片的动画效果
	 */
	static final String FRAGMENT_SHADER_FILE5 = "shaders/fontFragmentShader5";
	
	private static String targetVertexShaderFile = VERTEX_SHADER_FILE;
	private static String targetFragmentShaderFile = FRAGMENT_SHADER_FILE3;
	public int programID;
	public int vertexShaderID;
	public int fragmentShaderID;
	
	public int location_colour;
	public int location_translation;
	
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
		GL20.glShaderSource(shader.vertexShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(targetVertexShaderFile)));
		GL20.glCompileShader(shader.vertexShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.vertexShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.vertexShaderID, LOG_LENGTH));
		}
		shader.fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(shader.fragmentShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(targetFragmentShaderFile)));
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
		return shader;
	}
}
