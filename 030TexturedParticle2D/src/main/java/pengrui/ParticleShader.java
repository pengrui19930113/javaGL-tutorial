package pengrui;

import static pengrui.CommonConstant.LOG_LENGTH;
import static pengrui.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_INDEX;
import static pengrui.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_NAME;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
public class ParticleShader implements Shaderable{
	private static final String VERTEX_SHADER_FILE = "shaders/particleVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/particleFragmentShader";
	
	public int programID;
	public int vertexShaderID;
	public int fragmentShaderID;
	
	public int location_projectionMatrix;
	public int location_modelViewMatrix;
	
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

	public static ParticleShader createParticlesShader(List<Shaderable> shaders) {
		ParticleShader shader = new ParticleShader();
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
		GL20.glAttachShader(shader.programID,shader.fragmentShaderID);
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_POSITION_INDEX, PROGRAM_ATTRIBUTE_POSITION_NAME);
		GL20.glLinkProgram(shader.programID);
		shader.location_modelViewMatrix = GL20.glGetUniformLocation(shader.programID, "modelViewMatrix");
		shader.location_projectionMatrix = GL20.glGetUniformLocation(shader.programID, "projectionMatrix");
		//
		GL20.glUseProgram(shader.programID);
		Matrix4f projectionMatrix = MatrixUtil.createProjectionMatrix();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		projectionMatrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(shader.location_projectionMatrix, false, buffer);
		GL20.glUseProgram(0);
		return shader;
	}

}
