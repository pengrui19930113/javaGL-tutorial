package pengrui.particle3d;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import pengrui.common.VAO;

public class Particle3DLoader {

	public static ParticleDataHolder createVaoAndVbo(List<Integer>vaos,List<Integer>vbos, int maxParticles) {
		ParticleDataHolder holder = new ParticleDataHolder();
		holder.vao = new VAO();
		int vaoid = GL30.glGenVertexArrays();
		vaos.add(vaoid);
		holder.vao.vaoid  = vaoid;
		GL30.glBindVertexArray(vaoid);
		int vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		holder.vboid = vboid;
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
//		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, maxParticles*4, GL15.GL_DYNAMIC_DRAW);//一个float 4个字节
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, maxParticles*4, GL15.GL_STREAM_DRAW);//一个float 4个字节
		//3d particle position
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0,0);//position
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
		GL30.glBindVertexArray(0);
		return holder;
	}
	
	public static class ParticleDataHolder{
		public VAO vao;
		public int vboid;
	}
}
