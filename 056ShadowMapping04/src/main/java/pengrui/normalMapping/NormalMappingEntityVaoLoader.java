package pengrui.normalMapping;

import static pengrui.config.CommonConstant.*;

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

import pengrui.common.VAO;

public class NormalMappingEntityVaoLoader {

	public static VAO createNormalMappingEntityVao(List<Integer>vaos,List<Integer>vbos,String file) {
		return createNormalMappingEntityVao(vaos,vbos,ClassLoader.getSystemResourceAsStream(file));
	}
	public static VAO createNormalMappingEntityVao(List<Integer>vaos,List<Integer>vbos,InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		List<VertexNM> vertices = new ArrayList<VertexNM>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			String line;
			while(null !=(line = reader.readLine())) {
				if(line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					vertices.add(new VertexNM(vertices.size(),new Vector3f(Float.valueOf(currentLine[1]),Float.valueOf(currentLine[2]),Float.valueOf(currentLine[3]))));
				}else if(line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					textures.add(new Vector2f(Float.valueOf(currentLine[1]),Float.valueOf(currentLine[2])));
				}else if(line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					normals.add(new Vector3f(Float.valueOf(currentLine[1]),Float.valueOf(currentLine[2]),Float.valueOf(currentLine[3])));
				}else if(line.startsWith("f ")) {
					break;
				}
			}
			do {
				if(line.startsWith("f ")) {
					String[] currentLine = line.split(" ");
					String[] vertex1 = currentLine[1].split("/");
					String[] vertex2 = currentLine[2].split("/");
					String[] vertex3 = currentLine[3].split("/");
					VertexNM v0 = processVertex(vertex1,vertices,indices);
					VertexNM v1 = processVertex(vertex2,vertices,indices);
					VertexNM v2 = processVertex(vertex3,vertices,indices);
					calculateTangents(v0,v1,v2,textures);
				}
			}while(null != (line = reader.readLine()));
			reader.close();
		}catch(IOException e) {
			System.out.println("loader normal mapping obj failure.");
		}
		for (VertexNM vertex : vertices) {
			//calculate average of tangents
			if(!vertex.tangents.isEmpty()) {
				for(Vector3f tangent:vertex.tangents) {
					Vector3f.add(vertex.averagedTangent, tangent, vertex.averagedTangent);
				}
				vertex.averagedTangent.normalise();
			}
			// remove unused vertices
			if (!(-1!=vertex.textureIndex && -1!=vertex.normalIndex)) {//没初始化过节点
				//打断点 debug 发现并没有进入该分支
				vertex.textureIndex = 0;
				vertex.normalIndex = 0;
			}
		}
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float[] tangentsArray = new float[vertices.size() * 3];
//		float furthest = 
		convertDataToArrays(vertices,textures,normals,verticesArray,texturesArray,normalsArray,tangentsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		
		return createVao(vaos,vbos,verticesArray,texturesArray,normalsArray,tangentsArray,indicesArray);
	}
	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}
	private static float convertDataToArrays(List<VertexNM> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray, float[] tangentsArray) {
		float furthestPoint = 0;
		for(int i=0;i<vertices.size();i++) {
			VertexNM currentVertex = vertices.get(i);
			if(currentVertex.positionLength >furthestPoint) {
				furthestPoint = currentVertex.positionLength;
			}
			Vector3f position = currentVertex.position;
			Vector2f textureCoord = textures.get(currentVertex.textureIndex);
			Vector3f normalVector = normals.get(currentVertex.normalIndex);
			Vector3f tangent = currentVertex.averagedTangent;
			verticesArray[i*3] = position.x;
			verticesArray[i*3+1] = position.y;
			verticesArray[i*3+2] = position.z;
			texturesArray[i*2] = textureCoord.x;
			texturesArray[i*2+1] = 1-textureCoord.y; // 注意纹理是左手系
			normalsArray[i*3] = normalVector.x;
			normalsArray[i*3+1] = normalVector.y;
			normalsArray[i*3+2] = normalVector.z;
			tangentsArray[i*3] = tangent.x;
			tangentsArray[i*3+1] = tangent.y;
			tangentsArray[i*3+2] = tangent.z;
		}
		return furthestPoint;
	}
	/**
	 * https://learnopengl-cn.github.io/05%20Advanced%20Lighting/04%20Normal%20Mapping/
	 * 
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param textures
	 */
	private static void calculateTangents(VertexNM v0, VertexNM v1, VertexNM v2,
			List<Vector2f> textures) {
		Vector3f v01 = Vector3f.sub(v1.position,v0.position,null);// v01.x与 tangent平行	v01.y 与 bitangent平行
		Vector3f v02 = Vector3f.sub(v2.position,v0.position,null);// v02.x与 tangent平行	v02.y 与 bitangent平行
		
		Vector2f uv0 = textures.get(v0.textureIndex);
		Vector2f uv1 = textures.get(v1.textureIndex);
		Vector2f uv2 = textures.get(v2.textureIndex);
		Vector2f deltaUV1 = Vector2f.sub(uv1, uv0, null);  
		Vector2f deltaUV2 = Vector2f.sub(uv2, uv0, null); 
		
		float r = 1.f/(deltaUV1.x*deltaUV2.y-deltaUV1.y*deltaUV2.x);
		v01.scale(deltaUV2.y);
		v02.scale(deltaUV1.y);
		Vector3f tangent = Vector3f.sub(v01, v02, null);
		tangent.scale(r);
		
		v0.tangents.add(tangent);
		v1.tangents.add(tangent);
		v2.tangents.add(tangent);
	}
	
	private static VertexNM dealWithAlreadyProcessedVertex(VertexNM previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<VertexNM> vertices) {//遇见顶点重复 但是纹理或法线不重复的时候寻找next 中是否包含 如果包含则返回 否则创建
		//recursive header
		if(newTextureIndex == previousVertex.textureIndex &&  newNormalIndex == previousVertex.normalIndex) {
			indices.add(previousVertex.listIndex);
			return previousVertex;
		}else {
			VertexNM next = previousVertex.next;
			if(null != next) {
				return dealWithAlreadyProcessedVertex(next,newTextureIndex,newNormalIndex,indices,vertices);
			}else {//recursive call
				VertexNM tail = new VertexNM(vertices.size() // 对于新添加的顶点设置List的索引为新的长度
						,previousVertex.position);
				tail.tangents = previousVertex.tangents;
				tail.textureIndex = newTextureIndex;
				tail.normalIndex = newNormalIndex;
				previousVertex.next = tail;// 将新创建的节点 放置到链表上
				vertices.add(tail);// tail 在 vertices的位置 位域 tail.listIndex
				indices.add(tail.listIndex);
				return tail;
			}
		}
	}
	private static VertexNM processVertex(String[] vertex,List<VertexNM> vertices,List<Integer> indices) {
		int vertexIndex = Integer.valueOf(vertex[0])-1; //由于 obj 文件中的索引是从1开始的  但是 List 是从0开始 所以减少1个偏移量
		int textureIndex = Integer.valueOf(vertex[1])-1;
		int normalIndex = Integer.valueOf(vertex[2])-1;
		VertexNM currentVertex = vertices.get(vertexIndex);
		if(!(-1 != currentVertex.normalIndex && -1 != currentVertex.textureIndex)) {//如果
			currentVertex.textureIndex = textureIndex;
			currentVertex.normalIndex = normalIndex;
			indices.add(vertexIndex);
			return currentVertex;
		}else {
			return dealWithAlreadyProcessedVertex(currentVertex,textureIndex,normalIndex,indices,vertices);
		}
	}
	
	public static VAO createVao(List<Integer> vaos, List<Integer> vbos,float[] vertices,float[] texCoords,float[] normals,float[] tangents ,int[] indices) {
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
		// tangents
		vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		buffer = BufferUtils.createFloatBuffer(tangents.length);
		buffer.put(tangents);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(PROGRAM_ATTRIBUTE_TANGENT_INDEX, 3, GL11.GL_FLOAT, false, 0,0);
		
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
	
	private static class VertexNM{
		/**
		 * obj 文件中的 v  顶点坐标 (vertex)
		 */
		public Vector3f position;
		/**
		 * position.length
		 */
		public float positionLength;
		/**
		 * obj 文件中的 vt 索引 (vertex texture index)
		 */
		public int textureIndex = -1;
		/**
		 * 
		 */
		public int normalIndex = -1;
		/**
		 * 在obj 文件中  f 中对应的三角形面的顶点中 可能存在顶点索引相同 但是纹理索引和法线索引不同的三角形顶点
		 * 该成员变量维护了一个单向链表，用来记录同顶点不同纹理或法线的三角形顶点
		 */
		public VertexNM next;//sameVertexButNotSameTextureOrNormalChain;
		/**
		 * List<VertexNM> 中的 索引  由0开始
		 */
		public int listIndex;
		/**
		 * 由于一个顶点可能被多个面使用到，那么顶点可能存在法线不一致的情况，导致计算出来的切线空间的基不一样
		 * 逻辑上可以先计算出公共顶点的法向量取平均 然后再来计算相应的切线空间基的一个分量 （tangent 也叫正切基）
		 */
		public List<Vector3f> tangents = new ArrayList<Vector3f>();
		/**
		 * 最终会计算出tangent的均值
		 */
		public Vector3f averagedTangent = new Vector3f(0,0,0);
		
		public VertexNM(int index,Vector3f position) {
			this.listIndex = index;
			this.position = position;
			this.positionLength = position.length();
		}
	}
	
}
