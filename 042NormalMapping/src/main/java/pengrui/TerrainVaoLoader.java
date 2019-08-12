package pengrui;

import static pengrui.CommonConstant.MAX_HEIGHT;
import static pengrui.CommonConstant.MAX_PIXEL_COLOUR;
import static pengrui.CommonConstant.TERRAIN_ONE_LINE_SIZE;
import static pengrui.CommonConstant.TERRAIN_ONE_LINE_VERTEX_COUNT;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

public class TerrainVaoLoader {
	
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
		holder.vao = VAO.createVao(vaos,vbos,vertices,textureCoords,normals,indices);
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

		return VAO.createVao(vaos,vbos,vertices,textureCoords,normals,indices);
	}
}
