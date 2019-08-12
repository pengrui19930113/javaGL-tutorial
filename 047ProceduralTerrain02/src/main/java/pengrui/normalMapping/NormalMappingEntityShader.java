package pengrui.normalMapping;

import static pengrui.config.CommonConstant.*;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;
import pengrui.util.MatrixUtil;
public class NormalMappingEntityShader implements Shaderable{

	private static final String VERTEX_SHADER_FILE = "shaders/normalMappingEntityVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/normalMappingEntityFragmentShader";
	
	public int vertexShaderID;
	public int fragmentShaderID;
	public int programID;
	
	public int location_transformationMatrix;
	public int location_viewMatrix;
	public int location_projectionMatrix;
	public int location_shineDamper;
	public int location_reflectivity;
	public int location_lightPosition;
	public int location_lightColour;
	public int location_modelTexture;
	public int location_normalTexture;
	
	public static NormalMappingEntityShader createShader(List<Shaderable>shaders) {
		NormalMappingEntityShader shader = new NormalMappingEntityShader();
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
		GL20.glAttachShader(shader.programID,shader.vertexShaderID);
		GL20.glAttachShader(shader.programID, shader.fragmentShaderID);
		// bind attribute index
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_POSITION_INDEX, PROGRAM_ATTRIBUTE_POSITION_NAME);
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX, PROGRAM_ATTRIBUTE_TEXTURE_COORDS_NAME);
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_NORMALS_INDEX, PROGRAM_ATTRIBUTE_NORMALS_NAME);
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_TANGENT_INDEX, PROGRAM_ATTRIBUTE_TANGENT_NAME);
		GL20.glLinkProgram(shader.programID);
		// get uniform location
		shader.location_transformationMatrix = GL20.glGetUniformLocation(shader.programID,"transformationMatrix");
		shader.location_viewMatrix = GL20.glGetUniformLocation(shader.programID,"viewMatrix");
		shader.location_projectionMatrix = GL20.glGetUniformLocation(shader.programID, "projectionMatrix");
		shader.location_shineDamper = GL20.glGetUniformLocation(shader.programID, "shineDamper");
		shader.location_reflectivity = GL20.glGetUniformLocation(shader.programID, "reflectivity");
		shader.location_lightPosition = GL20.glGetUniformLocation(shader.programID, "lightPosition");
		shader.location_lightColour = GL20.glGetUniformLocation(shader.programID, "lightColour");
		shader.location_modelTexture = GL20.glGetUniformLocation(shader.programID, "modelTexture");
		shader.location_normalTexture = GL20.glGetUniformLocation(shader.programID, "normalTexture");
		// once uniform
		GL20.glUseProgram(shader.programID);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		MatrixUtil.createProjectionMatrix().store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(shader.location_projectionMatrix, false, buffer);
		GL20.glUniform1i(shader.location_modelTexture, 0);
		GL20.glUniform1i(shader.location_normalTexture, 1);
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
