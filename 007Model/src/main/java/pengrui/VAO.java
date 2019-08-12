package pengrui;

import static pengrui.CommonConstant.PROGRAM_ATTRIBUTE_POSITION_INDEX;
import static pengrui.CommonConstant.PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
public class VAO {
	
	public int vaoid;
	public int vertexCount;
	
	static final float[] TRIANGLE_VERTICES_POISITION_DATA = { 
			-.5f, .5f, .0f
			, -.5f, -.5f, .0f
			, .5f, -.5f, .0f
			, .5f, .5f, .0f 
	};
	static final int[] TRIANGLE_VERTICES_INDICES_DATA = { 
			0, 1, 2
			,0, 2, 3 
	};

	static final float[] TRIANGLE_VERTICES_TEXTURE_COORDS = { 
			0, 0
			, 1, 0
			, 1, 1
			, 0, 1 
	};// 注意纹理坐标是左手系 原点在图片左上角 向右是X 向下是Y

	public static VAO createVao(List<Integer> vaos, List<Integer> vbos) {
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
		FloatBuffer buffer = BufferUtils.createFloatBuffer(TRIANGLE_VERTICES_POISITION_DATA.length);
		buffer.put(TRIANGLE_VERTICES_POISITION_DATA);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_POSITION_INDEX, 3, GL11.GL_FLOAT, false, 0, 0);
		// vertex array for texture coordination
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		buffer = BufferUtils.createFloatBuffer(TRIANGLE_VERTICES_TEXTURE_COORDS.length);
		buffer.put(TRIANGLE_VERTICES_TEXTURE_COORDS);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX, 2, GL11.GL_FLOAT, false, 0, 0);

		// element array
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboid);
		IntBuffer buffer2 = BufferUtils.createIntBuffer(TRIANGLE_VERTICES_INDICES_DATA.length);
		buffer2.put(TRIANGLE_VERTICES_INDICES_DATA);
		buffer2.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer2, GL15.GL_STATIC_DRAW);
		vao.vertexCount = TRIANGLE_VERTICES_INDICES_DATA.length;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		return vao;
	}
	
	public static VAO createObjVao(List<Integer>vaos,List<Integer> vbos,InputStream is) {
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textureCoords = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();// 现阶段没用到 //no use
		List<Integer> indices = new ArrayList<Integer>();
		int[]indicesArray = null;
		float[]verticesArray = null;
		float[]normalsArray = null;// 现阶段没用到 //no use
		float[]textureArray = null;
		
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try { // v vt vn f
			while(null!=(line = br.readLine())) {
				
				if(line.startsWith("#") || line.trim().length()<1) 
					continue;
				
				if(line.startsWith("v ")) {
					String[] strs = line.split(" ");
					vertices.add(new Vector3f(Float.parseFloat(strs[1]),Float.parseFloat(strs[2]),Float.parseFloat(strs[3])));
				}else if(line.startsWith("vt ")) {
					String[] strs = line.split(" ");
					textureCoords.add(new Vector2f(Float.parseFloat(strs[1]),Float.parseFloat(strs[2])));
				}else if(line.startsWith("vn ")) {
					String[] strs = line.split(" ");
					normals.add(new Vector3f(Float.parseFloat(strs[1]),Float.parseFloat(strs[2]),Float.parseFloat(strs[3])));
				}else if(line.startsWith("f ")) {
					textureArray = new float[vertices.size()*2];
					normalsArray= new float[vertices.size()*3];
					break;
				}
			}
			
			do {
				if(line.startsWith("#") || line.trim().length()<1) 
					continue;
				//放在得内存数组索引首地址为0 但是 三角形面使用得各个索引起始地址是1
				String[] strs = line.split(" ");//	f v1/t1/n1 v2/t2/n2 v3/t3/n3
				String[] vertex1 = strs[1].split("/");
				String[] vertex2 = strs[2].split("/");
				String[] vertex3 = strs[3].split("/");
				processVertex(vertex1,indices,textureCoords,normals,textureArray,normalsArray);
				processVertex(vertex2,indices,textureCoords,normals,textureArray,normalsArray);
				processVertex(vertex3,indices,textureCoords,normals,textureArray,normalsArray);
			}while(null!=(line = br.readLine()));
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("read obj file exception");
		}
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		int vertPointer = 0;
		for(Vector3f vert:vertices){
			verticesArray[vertPointer++]= vert.x;
			verticesArray[vertPointer++]= vert.y;
			verticesArray[vertPointer++]= vert.z;
		}
		
		for(int i=0;i<indices.size();++i)
			indicesArray[i] = indices.get(i);
		return createVao(vaos,vbos,verticesArray,textureArray,indicesArray);
	}
	
	private static void processVertex(
			String[] vertexData
			,List<Integer> indices
			,List<Vector2f> textureCoords
			,List<Vector3f> normals
			,float[] textureArray
			,float[] normalsArray
			){
		int currVertPointer = Integer.valueOf(vertexData[0])-1;
		indices.add(currVertPointer);
		Vector2f currTex = textureCoords.get(Integer.valueOf(vertexData[1])-1);
		textureArray[currVertPointer*2] = currTex.x;
		textureArray[currVertPointer*2+1] = 1-currTex.y;// 因为纹理坐标的原点在纹理的左上角
		Vector3f currNorm = normals.get(Integer.valueOf(vertexData[2])-1);
		normalsArray[currVertPointer*3] = currNorm.x;
		normalsArray[currVertPointer*3+1] = currNorm.y;
		normalsArray[currVertPointer*3+2] = currNorm.z;
	}
	
	public static VAO createVao(List<Integer> vaos, List<Integer> vbos,float[] vertices,float[] texCoords,int[] indices) {
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
