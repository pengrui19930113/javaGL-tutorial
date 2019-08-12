package pengrui;

import static pengrui.CommonConstant.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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
	
	public static VAO createObjVao(List<Integer>vaos,List<Integer> vbos,String file) {
		return createObjVao(vaos,vbos,ClassLoader.getSystemResourceAsStream(file));
	}
	
	public static Terrain.TerrainHolder createTerrainVao(List<Integer>vaos,List<Integer> vbos,String heightMapPath) {
		BufferedImage image = null;
		float heights[][] = null;
		try {
			image = ImageIO.read(ClassLoader.getSystemResourceAsStream(heightMapPath));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("height map load failure");
		}
		int VERTEX_COUNT = image.getHeight();
		float SIZE = TERRAIN_ONE_LINE_SIZE;
		
		int count = VERTEX_COUNT*VERTEX_COUNT;
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		
		float[] vertices = new float[count*3];
		float[] normals = new float[count*3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		
		for(int row = 0;row < VERTEX_COUNT; row++) {
			for(int column = 0;column <VERTEX_COUNT; column++) {
				// 顶点坐标y始终是0
				vertices[vertexPointer*3] = column*SIZE / ((float)VERTEX_COUNT-1);
				float height = getHeight(column,row,image);
				heights[column][row] = height;
				vertices[vertexPointer*3+1] = height;
				vertices[vertexPointer*3+2] = row*SIZE/ ((float)VERTEX_COUNT-1);
				// 法向量始终 与世界坐标系y轴同向
				Vector3f normal = calculateNormal(column,row,image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				
				textureCoords[vertexPointer*2] = ((float)column) / ((float)VERTEX_COUNT-1);
				textureCoords[vertexPointer*2+1] = ((float)row)/ ((float)VERTEX_COUNT-1);
				
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		
		Terrain.TerrainHolder holder = new Terrain.TerrainHolder();
		holder.vao = createVao(vaos,vbos,vertices,textureCoords,normals,indices);
		holder.heights = heights;
		return holder;
	}
	/**
	 * 差分法近似求法向量， 避免用 cross product 来提高性能
	 * 
	 * 
	 * 举个例子 ，在一段连续平滑的曲面上假设点p领域范围内相邻的四个方向上有4个点分别为 pup pdown pleft pright
	 * 高度分别为
	 *
 *   					pup	(9)
	 *   pleft(4)		p			pright(6)
 *   					pdown(1)
	 *   
	 *   已知p周围4个点的高度，可以通过下列算法近似求解p点的法向量
	 *   
	 *   n = normalise(pright-pleft,2,pdown-pup);
	 * 		
	 * 	有点像求偏导数的感觉
	 * @param x
	 * @param z
	 * @param image
	 * @return
	 */
	private static Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x-1,z,image);
		float heightR = getHeight(x+1,z,image);
		float heightD = getHeight(x,z-1,image);
		float heightU = getHeight(x,z+1,image);
		Vector3f normal = new Vector3f(heightR-heightL,2f,heightD-heightU);
		normal.normalise();
		return normal;
	}


	/**
	 * image.getRGB(x,z) 返回的是整数
	 * 
	 *  11111111R(8)G(8)B(8)
	 * 	0xFF000000 //-16777216 			256^3 = 16777216
	 * 	0xFFFFFFFF // -1
	 * @param x
	 * @param z
	 * @param image
	 * @return
	 */
	private static float getHeight(int x, int z, BufferedImage image) {
		if(x<0 || x>=image.getWidth() || z<0|| z>=image.getHeight()){
			return 0;
		}
		float height = image.getRGB(x,z); //值域范围 [-MAX_PIXEL_COLOUR , 0) 因为高位的alpha是八位1，所以值域是这个
		height += MAX_PIXEL_COLOUR/2F;// [-MAX_PIXEL_COLOUR/2 , MAX_PIXEL_COLOUR/2)
		height /= MAX_PIXEL_COLOUR/2F; //[-1,1)
		height *= MAX_HEIGHT;//[-MAX_HEIGHT,MAX_HEIGHT)
		return height;
	}

	public static VAO createTerrainVao(List<Integer>vaos,List<Integer> vbos) {
		return createTerrainVao(vaos,vbos,TERRAIN_ONE_LINE_VERTEX_COUNT,TERRAIN_ONE_LINE_SIZE);
	}
	
	/**
	 * 一条边的SIZE 假设为10 VERTEX_COUNT 假设为3(3个点构成两条线段) 也就是 Vx0(0,0,0) Vx1(5,0,0) Vx2(10,0,0)
	 * 
	 * 构成的方格块是由 6个index array 指定的
	 * @param vaos
	 * @param vbos
	 * @param VERTEX_COUNT
	 * @param SIZE
	 * @return
	 */
	public static VAO createTerrainVao(List<Integer>vaos,List<Integer> vbos,int VERTEX_COUNT ,float SIZE) {//得到一个由很多小三角形组成的 正方形网格
		int count = VERTEX_COUNT*VERTEX_COUNT;
		float[] vertices = new float[count*3];
		float[] normals = new float[count*3];
		float[] textureCoords = new float[count*2];
		
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];//假设VERTEX_COUNT = 4 则有 3*3个正方形 所以有 6*3*3 个顶点索引
		int vertexPointer = 0;
		for(int row = 0;row < VERTEX_COUNT;row++) { //一行共 VERTEX_COUNT 从 0 到 VERTEX_COUNT-1 
			for(int column = 0;column < VERTEX_COUNT;column++) {
				// 顶点坐标y始终是0
				vertices[vertexPointer*3] = column*SIZE / ((float)VERTEX_COUNT-1);
				vertices[vertexPointer*3+1] = 0;
				vertices[vertexPointer*3+2] = row*SIZE/ ((float)VERTEX_COUNT-1);
				// 法向量始终 与世界坐标系y轴同向
				normals[vertexPointer*3] = 0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] = 0;
				
				textureCoords[vertexPointer*2] = ((float)column) / ((float)VERTEX_COUNT-1);
				textureCoords[vertexPointer*2+1] = ((float)row)/ ((float)VERTEX_COUNT-1);
				
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gridz = 0;gridz < VERTEX_COUNT-1 ; gridz++) {
			for(int gridx = 0;gridx < VERTEX_COUNT-1;gridx++) {
				int topLeft = (gridz * VERTEX_COUNT) + gridx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gridz+1)*VERTEX_COUNT) + gridx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		return createVao(vaos,vbos,vertices,textureCoords,normals,indices);
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
		return createVao(vaos,vbos,verticesArray,textureArray,normalsArray,indicesArray);
	}
	
	private static void processVertex(
			String[] vertexData
			,List<Integer> indices
			,List<Vector2f> textureCoords
			,List<Vector3f> normals
			,float[] textureArray
			,float[] normalsArray
			){
		processVertex(vertexData,indices,textureCoords,normals,textureArray,normalsArray,true);
	}
	private static void processVertex(
			String[] vertexData
			,List<Integer> indices
			,List<Vector2f> textureCoords
			,List<Vector3f> normals
			,float[] textureArray
			,float[] normalsArray
			,boolean transTexture//是否转换纹理坐标
			){
		int currVertPointer = Integer.valueOf(vertexData[0])-1;
		indices.add(currVertPointer);
		Vector2f currTex = textureCoords.get(Integer.valueOf(vertexData[1])-1);
		textureArray[currVertPointer*2] = currTex.x;
		if(transTexture) {
			textureArray[currVertPointer*2+1] = 1-currTex.y;// 因为纹理坐标的原点在纹理的左上角
		}else {
			textureArray[currVertPointer*2+1] = currTex.y;// 因为纹理坐标的原点在纹理的左上角
		}
		Vector3f currNorm = normals.get(Integer.valueOf(vertexData[2])-1);
		normalsArray[currVertPointer*3] = currNorm.x;
		normalsArray[currVertPointer*3+1] = currNorm.y;
		normalsArray[currVertPointer*3+2] = currNorm.z;
	}
	
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
	
	public static VAO createGuiEntityVao(List<Integer> vaos, List<Integer> vbos,float[] vertices) {
		VAO vao = new VAO();
		int vaoid = GL30.glGenVertexArrays();
		vaos.add(vaoid);
		vao.vaoid = vaoid;
		GL30.glBindVertexArray(vaoid);
		
		int vboid = GL15.glGenBuffers();
		vbos.add(vboid);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		vao.vertexCount = vertices.length/2;
		GL30.glBindVertexArray(0);
		return vao;
	}
}
