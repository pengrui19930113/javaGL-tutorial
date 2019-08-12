package model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import data.MeshData;

public class MeshVaoCreator {
	
	public int vaoid;
	public int indexCount;
	public int[] vbos = new int[6];
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_INT = 4;
	public MeshVaoCreator createVao(MeshData meshData) {
		vaoid = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoid);
		
		int[] indices = meshData.indices;
		indexCount = indices.length;
		vbos[5] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbos[5]);
		IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
		buffer.put(indices);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
		createAttribute(0,meshData.vertices,3);
		createAttribute(1,meshData.textureCoords,2);
		createAttribute(2,meshData.normals,3);
		createIntAttribute(3,meshData.jointIds,3);
		createAttribute(4,meshData.vertexWeights,3);
		
		GL30.glBindVertexArray(0);
		return this;
	}
	
	private void createIntAttribute(int attribute, int[] data, int attrSize) {
		vbos[attribute] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[attribute]);
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();	
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		//注意这里是 IPinter 
		GL30.glVertexAttribIPointer(attribute, attrSize, GL11.GL_INT,  attrSize * BYTES_PER_INT, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void createAttribute(int attribute,float[] data,int attrSize) {
		vbos[attribute] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[attribute]);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();	
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, attrSize * BYTES_PER_FLOAT, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
}
