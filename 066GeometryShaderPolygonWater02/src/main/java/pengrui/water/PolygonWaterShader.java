package pengrui.water;

import static pengrui.config.CommonConstant.LOG_LENGTH;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;

public class PolygonWaterShader implements Shaderable{
	private static final String VERTEX_SHADER_FILE = "shaders/polygonWaterVertexShader";
	private static final String GEOMETRY_SHADER_FILE = "shaders/polygonWaterGeometryShader";
	private static final String FRAGMETN_SHADER_FILE = "shaders/polygonWaterFragmentShader";
	
	public int programID;
	public int vertexShaderID;
	public int geometryShaderID;
	public int fragmentShaderID;
	
	public int location_projectionViewMatrix;
	public int location_time;
	public int location_cameraPosition;
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
	@Override
	public int getGeometryShaderID() {
		return geometryShaderID;
	}
	
	public static PolygonWaterShader createShader(List<Shaderable> shaders) {
		PolygonWaterShader shader = new PolygonWaterShader();
		shaders.add(shader);
		
		shader.vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(shader.vertexShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(VERTEX_SHADER_FILE)));
		GL20.glCompileShader(shader.vertexShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.vertexShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.vertexShaderID, LOG_LENGTH));
		}
		
		shader.geometryShaderID = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);
		GL20.glShaderSource(shader.geometryShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(GEOMETRY_SHADER_FILE)));
		GL20.glCompileShader(shader.geometryShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.geometryShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.geometryShaderID, LOG_LENGTH));
		}
		
		shader.fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(shader.fragmentShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(FRAGMETN_SHADER_FILE)));
		GL20.glCompileShader(shader.fragmentShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.fragmentShaderID, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shader.fragmentShaderID, LOG_LENGTH));
		}
		
		shader.programID = GL20.glCreateProgram();
		GL20.glAttachShader(shader.programID, shader.vertexShaderID);
		GL20.glAttachShader(shader.programID, shader.geometryShaderID);
		GL20.glAttachShader(shader.programID, shader.fragmentShaderID);
		//bind
		GL20.glBindAttribLocation(shader.programID, 0, "position");
		//link
		GL20.glLinkProgram(shader.programID);
		GL20.glValidateProgram(shader.programID);
		if(GL11.GL_FALSE == GL20.glGetProgrami(shader.programID, GL20.GL_LINK_STATUS)) {
			throw new RuntimeException(GL20.glGetProgramInfoLog(shader.programID, LOG_LENGTH));
		}
		// get uniform location
		shader.location_projectionViewMatrix = GL20.glGetUniformLocation(shader.programID, "projectionViewMatrix");
		shader.location_time = GL20.glGetUniformLocation(shader.programID, "time");
		shader.location_cameraPosition = GL20.glGetUniformLocation(shader.programID, "cameraPosition");
		//once uniform
		return shader;
	}
}
