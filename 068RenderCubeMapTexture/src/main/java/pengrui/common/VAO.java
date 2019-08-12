package pengrui.common;

import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_NORMALS_INDEX;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_INDEX;
import static pengrui.config.CommonConstant.PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
public class VAO {
	
	public int vaoid;
	public int vertexCount;
	public static VAO createVao(List<Integer> vaos, List<Integer> vbos,float[] vertices,float[] texCoords,float[] normals,int[] indices) {
		VAO vao = new VAO();
		// load data to vao
		int vaoid = GL30.glGenVertexArrays();
		vaos.add(vaoid);
		vao.vaoid = vaoid;
		GL30.glBindVertexArray(vaoid);
		// vertex array for position
		int vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_POSITION_INDEX, 3, GL11.GL_FLOAT, false, 0, 0);
		// vertex array for texture coordination
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		buffer = BufferUtils.createFloatBuffer(texCoords.length);
		buffer.put(texCoords);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX, 2, GL11.GL_FLOAT, false, 0, 0);
		// normals
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		buffer = BufferUtils.createFloatBuffer(normals.length);
		buffer.put(normals);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_NORMALS_INDEX, 3, GL11.GL_FLOAT, false, 0, 0);

		// element array
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboid);
		IntBuffer buffer2 = BufferUtils.createIntBuffer(indices.length);
		buffer2.put(indices);
		buffer2.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer2, GL15.GL_STATIC_DRAW);
		vao.vertexCount = indices.length;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		return vao;
	}
}
