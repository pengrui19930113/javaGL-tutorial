package pengrui;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import static pengrui.CommonConstant.*;

public class EntityShader implements Shaderable{
	
	public int vertexShaderID;
	public int fragmentShaderID;
	public int programID;
	
	public int location_transformationMatrix;
	public int location_viewMatrix;
	public int location_projectionMatrix;
	public int location_lightPosition;
	public int location_lightColour;
	public int location_shineDamper; //聚光读
	public int location_reflectivity;//反射比
	public int location_useFakeLighting;
	public int location_skyColour;
	
	private static final String VERTEX_SHADER_FILE = "shaders/entityVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/entityFragmentShader";

	
	public static EntityShader createShader(List<Shaderable> shaders) {
		EntityShader shader = new EntityShader();
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
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX,PROGRAM_ATTRIBUTE_TEXTURE_COORDS_NAME);
		GL20.glBindAttribLocation(shader.programID, PROGRAM_ATTRIBUTE_NORMALS_INDEX, PROGRAM_ATTRIBUTE_NORMALS_NAME);
		
		GL20.glLinkProgram(shader.programID);
		shader.location_projectionMatrix = GL20.glGetUniformLocation(shader.programID, "projectionMatrix");
		shader.location_transformationMatrix = GL20.glGetUniformLocation(shader.programID, "transformationMatrix");
		shader.location_viewMatrix = GL20.glGetUniformLocation(shader.programID,"viewMatrix");
		shader.location_lightPosition = GL20.glGetUniformLocation(shader.programID, "lightPosition");
		shader.location_lightColour = GL20.glGetUniformLocation(shader.programID,"lightColour");
		shader.location_shineDamper = GL20.glGetUniformLocation(shader.programID, "shineDamper");
		shader.location_reflectivity = GL20.glGetUniformLocation(shader.programID,"reflectivity");
		shader.location_useFakeLighting = GL20.glGetUniformLocation(shader.programID,"useFakeLighting");
		shader.location_skyColour = GL20.glGetUniformLocation(shader.programID, "skyColour");
		
		//once uniform
		Matrix4f projectionMatrix = MatrixUtil.createProjectionMatrix();
		GL20.glUseProgram(shader.programID);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		projectionMatrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(shader.location_projectionMatrix,false,buffer);
		buffer = null;
		GL20.glUniform3f(shader.location_skyColour, SKY_COLOR_RED, SKY_COLOR_GREEN, SKY_COLOR_BLUE);
		GL20.glUseProgram(shader.programID);
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
