package pengrui.terrain;

import static pengrui.config.CommonConstant.LOG_LENGTH;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_NORMALS_INDEX;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_NORMALS_NAME;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_INDEX;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_NAME;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_TEXTURE_COORDS_NAME;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import pengrui.common.Shaderable;
import pengrui.util.InputStreamUtil;
import pengrui.util.MatrixUtil;

public class TerrainShader implements Shaderable{
	public int vertexShaderID;
	public int fragmentShaderID;
	public int programID;
	
	public int location_transformationMatrix;
	public int location_viewMatrix;
	public int location_projectionMatrix;
	public int location_lightPosition;
	public int location_lightColour;
	public int location_backgroundTexture;// multi texture ...
	public int location_rTexture;
	public int location_gTexture;
	public int location_bTexture;
	public int location_blendMap;
	
	private static final String VERTEX_SHADER_FILE = "shaders/terrainVertexShader";
	private static final String FRAGMENT_SHADER_FILE = "shaders/terrainFragmentShader";
	
	public static TerrainShader createShader(List<Shaderable> shaders) {
		TerrainShader shader = new TerrainShader();
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
		shader.location_backgroundTexture = GL20.glGetUniformLocation(shader.programID, "backgroundTexture");
		shader.location_rTexture = GL20.glGetUniformLocation(shader.programID, "rTexture");
		shader.location_gTexture = GL20.glGetUniformLocation(shader.programID, "gTexture");
		shader.location_bTexture = GL20.glGetUniformLocation(shader.programID, "bTexture");
		shader.location_blendMap = GL20.glGetUniformLocation(shader.programID, "blendMap");
		//once uniform
		Matrix4f projectionMatrix = MatrixUtil.createProjectionMatrix();
		GL20.glUseProgram(shader.programID);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		projectionMatrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(shader.location_projectionMatrix,false,buffer);
		// indicate texture unit
		GL20.glUniform1i(shader.location_backgroundTexture, 0);
		GL20.glUniform1i(shader.location_rTexture, 1);
		GL20.glUniform1i(shader.location_gTexture, 2);
		GL20.glUniform1i(shader.location_bTexture, 3);
		GL20.glUniform1i(shader.location_blendMap, 4);
		
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
