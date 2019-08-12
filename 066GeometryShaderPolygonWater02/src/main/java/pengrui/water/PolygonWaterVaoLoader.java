package pengrui.water;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import pengrui.common.VAO;

public class PolygonWaterVaoLoader {
//	public static final int VERTEX_COUNT = 100;
	public static final int VERTEX_COUNT = 51;
//	public static final float SIZE = 0.11f;
	public static final float SIZE = 0.1024f;
	
	public static VAO createPolygonWaterVao(List<Integer> vaos,List<Integer>vbos) {
		VAO vao = new VAO();
		float[] position = generatePositions();// 浮点数组 是 x1,z1 x2,z2 ...
		int[] indices = generateIndices();
		int vaoid = GL30.glGenVertexArrays();
		vaos.add(vaoid);
		vao.vaoid = vaoid;
		GL30.glBindVertexArray(vaoid);
		
		int vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboid);
		IntBuffer ibuf = BufferUtils.createIntBuffer(indices.length);
		ibuf.put(indices);
		ibuf.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ibuf, GL15.GL_STATIC_DRAW);	
		
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		FloatBuffer fbuf = BufferUtils.createFloatBuffer(position.length);
		fbuf.put(position);
		fbuf.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fbuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		vao.vertexCount = indices.length;
		return vao;
	}
	
	private static float[] generatePositions(){
		float[] positions = new float[VERTEX_COUNT * VERTEX_COUNT * 2];
		int pointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				positions[pointer++] = j * SIZE;
				positions[pointer++] = i * SIZE;
			}
		}
		return positions;
	}
	
	private static int[] generateIndices(){
		int pointer = 0;
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return indices;
	}
}
