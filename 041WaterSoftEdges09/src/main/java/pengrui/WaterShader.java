package pengrui;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import static pengrui.CommonConstant.*;
public class WaterShader implements Shaderable{

	private static final String VERTEX_SHADER_FILE = "shaders/waterVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/waterFragmentShader";
	
	public int programID;
	public int vertexShaderID;
	public int fragmentShaderID;
	
	public int location_projectionMatrix;
	public int location_viewMatrix;
	public int location_modelMatrix;
	public int location_reflectionTexture;
	public int location_refractionTexture;
	public int location_dudvMap;
	public int location_moveFactor;
	public int location_cameraPosition;
	public int location_normalMap;
	public int location_lightColour;
	public int location_lightPosition;
	public int location_depthMap;
	
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
	
	
	public static WaterShader createShader(List<Shaderable>shaders) {
		WaterShader shader = new WaterShader();
		shaders.add(shader);
		shader.vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(shader.vertexShaderID, InputStreamUtil.readStringFromInputStream(ClassLoader.getSystemResourceAsStream(VERTEX_SHADER_FILE)));
		GL20.glCompileShader(shader.vertexShaderID);
		if(GL11.GL_FALSE == GL20.glGetShaderi(shader.vertexShaderID,GL20.GL_COMPILE_STATUS)) {
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
		//bind attribute location
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_POSITION_INDEX, PROGRAM_ATTRIBUTE_POSITION_NAME);
		GL20.glLinkProgram(shader.programID);
		//get uniform location
		shader.location_modelMatrix = GL20.glGetUniformLocation(shader.programID, "modelMatrix");
		shader.location_viewMatrix = GL20.glGetUniformLocation(shader.programID, "viewMatrix");
		shader.location_projectionMatrix = GL20.glGetUniformLocation(shader.programID, "projectionMatrix");
		shader.location_reflectionTexture = GL20.glGetUniformLocation(shader.programID,"reflectionTexture");
		shader.location_refractionTexture = GL20.glGetUniformLocation(shader.programID, "refractionTexture");
		shader.location_dudvMap = GL20.glGetUniformLocation(shader.programID, "dudvMap");
		shader.location_moveFactor = GL20.glGetUniformLocation(shader.programID, "moveFactor");
		shader.location_cameraPosition = GL20.glGetUniformLocation(shader.programID, "cameraPosition");
		shader.location_normalMap = GL20.glGetUniformLocation(shader.programID, "normalMap");
		shader.location_lightColour = GL20.glGetUniformLocation(shader.programID,"lightColour");
		shader.location_lightPosition = GL20.glGetUniformLocation(shader.programID, "lightPosition");
		shader.location_depthMap = GL20.glGetUniformLocation(shader.programID, "depthMap");
		//once uniform variable assigned
		GL20.glUseProgram(shader.programID);
		GL20.glUniform1i(shader.location_reflectionTexture, 0);//0 associate glActiveTexture(GL_TEXTURE0)
		GL20.glUniform1i(shader.location_refractionTexture, 1);//1 associate glActiveTexture(GL_TEXTURE1)
		GL20.glUniform1i(shader.location_dudvMap, 2);////2 associate glActiveTexture(GL_TEXTURE1)
		GL20.glUniform1i(shader.location_normalMap, 3);
		GL20.glUniform1i(shader.location_depthMap, 4);
		
		Matrix4f projectionMatrix = MatrixUtil.createProjectionMatrix();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		projectionMatrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(shader.location_projectionMatrix, false, buffer);
		GL20.glUseProgram(0);
		return shader;
	}
}