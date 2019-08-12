package shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class AnimatedModelShader {

	private static final String VERTEX_SHADER_FILE = "shader/animatedEntityVertex.glsl";
	private static final String FRAGMENT_SHADER_FILE = "shader/animatedEntityFragment.glsl";
	private static final int MAX_JOINTS = 50;
	private static final int DIFFUSE_TEX_UNIT = 0;
	private static final int NOT_FOUND = -1;
	
	public int vertexShader;
	public int fragmentShader;
	public int program;
	
	public int location_projectionViewMatrix;
	public int location_lightDirection;
	public int[] location_jointTransforms;
	public int location_diffuseMap;
	
	public AnimatedModelShader() {
		this.location_jointTransforms = new int[MAX_JOINTS];
		
		vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(vertexShader, getSource(VERTEX_SHADER_FILE));
		GL20.glCompileShader(vertexShader);
		checkShaderCompileStatus(vertexShader);
		fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(fragmentShader, getSource(FRAGMENT_SHADER_FILE));
		GL20.glCompileShader(fragmentShader);
		checkShaderCompileStatus(fragmentShader);
		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		GL20.glBindAttribLocation(program, 0, "in_position");
		GL20.glBindAttribLocation(program, 1, "in_textureCoords");
		GL20.glBindAttribLocation(program, 2, "in_normal");
		GL20.glBindAttribLocation(program, 3, "in_jointIndices");
		GL20.glBindAttribLocation(program, 4, "in_weights");
		GL20.glLinkProgram(program);
		GL20.glDetachShader(program, vertexShader);
		GL20.glDetachShader(program, fragmentShader);
		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteShader(fragmentShader);
		checkProgramCompileStatus();
		location_projectionViewMatrix = GL20.glGetUniformLocation(program, "projectionViewMatrix");
		checkStoreUniformLocation(location_projectionViewMatrix);
		location_lightDirection = GL20.glGetUniformLocation(program, "lightDirection");
		checkStoreUniformLocation(location_lightDirection);
		for(int i=0;i<location_jointTransforms.length;i++) {
			location_jointTransforms[i] = GL20.glGetUniformLocation(program, "jointTransforms["+i+"]");
			checkStoreUniformLocation(location_jointTransforms[i]);
		}
		location_diffuseMap = GL20.glGetUniformLocation(program, "diffuseMap");
		checkStoreUniformLocation(location_diffuseMap);
		
		GL20.glUseProgram(program);
		GL20.glUniform1i(location_diffuseMap, DIFFUSE_TEX_UNIT);
		GL20.glUseProgram(0);
	}
	
	private void checkStoreUniformLocation(int location) {
		if(NOT_FOUND == location) {
			throw new RuntimeException("uniform not found");
		}
	}
	private void checkProgramCompileStatus() {
		if(GL11.GL_FALSE == GL20.glGetProgrami(program, GL20.GL_LINK_STATUS)) {
			throw new RuntimeException(GL20.glGetProgramInfoLog(program, 512));
		}
		GL20.glValidateProgram(program);
	}
	
	private void checkShaderCompileStatus(int shaderId) {
		if(GL11.GL_FALSE == GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS)) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shaderId, 512));
		}
	}
	public void delete() {
		GL20.glUseProgram(0);
		GL20.glDeleteProgram(program);
	}
	
	private String getSource(String file) {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(file)));
		String line;
		try {
			while(null!=(line=reader.readLine())) {
				builder.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
}
