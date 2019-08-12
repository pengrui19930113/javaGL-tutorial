package pengrui.font;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import pengrui.common.VAO;

public class StaticTextLoader {
	
	public static VAO loadVao(float[] verticesArray, float[] textureCoordsArray,StaticText text) {
		VAO vao = new VAO();
		vao.vertexCount = verticesArray.length/2;
		vao.vaoid = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao.vaoid);
		
		int vboid = GL15.glGenBuffers();
		text.selfManagedVbos[StaticText.VERTICES_INDEX] = vboid;
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(verticesArray.length);
		buffer.put(verticesArray);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0,0);
		
		vboid = GL15.glGenBuffers();
		text.selfManagedVbos[StaticText.TEXTURE_COODINATION_INDEX] = vboid;
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		buffer = BufferUtils.createFloatBuffer(textureCoordsArray.length);
		buffer.put(textureCoordsArray);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
		GL30.glBindVertexArray(0);
		return vao;
	}
	
}
