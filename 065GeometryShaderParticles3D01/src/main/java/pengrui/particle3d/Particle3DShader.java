package pengrui.particle3d;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;

import static pengrui.config.CommonConstant.*;

public class Particle3DShader implements Shaderable{

	private static final String VERTEX_SHADER_FILE = "shaders/particle3dVertexShader";
	private static final String GEOMETRY_SHADER_FILE = "shaders/particle3dGeometryShader";
	private static final String FRAGMETN_SHADER_FILE = "shaders/particle3dFragmentShader";
	
	public int programID;
	public int vertexShaderID;
	public int geometryShaderID;
	public int fragmentShaderID;
	
	public int location_projectionViewMatrix;
	public static Particle3DShader createShader(List<Shaderable> shaders) {
		Particle3DShader shader = new Particle3DShader();
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
		//get uniform location
		shader.location_projectionViewMatrix = GL20.glGetUniformLocation(shader.programID, "projectionViewMatrix");
		//once uniform ,connect texture unit projection matrix ...etc
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
	
	@Override
	public int getGeometryShaderID() {
		return geometryShaderID;
	}
}
