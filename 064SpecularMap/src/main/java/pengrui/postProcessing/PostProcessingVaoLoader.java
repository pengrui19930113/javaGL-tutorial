package pengrui.postProcessing;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import pengrui.common.VAO;

public class PostProcessingVaoLoader {
	
	private static final float[] POSITIONS = {//triangle strip
			-1,1
			,-1,-1
			,1,1
			,1,-1
	};
	public static VAO createPostProcessingVao(List<Integer> vaos, List<Integer> vbos) {
		VAO vao = new VAO();
		int vaoid = GL30.glGenVertexArrays();
		vaos.add(vaoid);
		vao.vaoid = vaoid;
		GL30.glBindVertexArray(vaoid);
		int vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(POSITIONS.length);
		buffer.put(POSITIONS);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		vao.vertexCount = POSITIONS.length/2;
		GL30.glBindVertexArray(0);
		return vao;
	}
}
