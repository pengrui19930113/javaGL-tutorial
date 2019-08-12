package pengrui.terrain;

import static pengrui.config.CommonConstant.*;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import pengrui.common.VAO;
public class Terrain {
	public VAO vao;
	public float xPos ,zPos;
	public int texture;
	public int rTexture;
	public int gTexture;
	public int bTexture;
	public int blendMap;
	public float[][] heights;
	
	public Terrain(TerrainHolder holder,int tex,int r,int g ,int b,int map) {
		vao = holder.vao;
		heights = holder.heights;
		texture = tex;
		xPos = -TERRAIN_ONE_LINE_SIZE/2; 
		zPos = -TERRAIN_ONE_LINE_SIZE/2;
		
		rTexture = r;
		gTexture = g;
		bTexture = b;
		blendMap = map;
	}

	public float getHeightOfTerrain(float worldx, float worldz) {
		float terrainx = worldx - this.xPos; // 从世界坐标变换到地图的模型坐标
		float terrainz = worldz - this.zPos;
		float SIZE = TERRAIN_ONE_LINE_SIZE;
		float gridSquareSize = SIZE/((float)heights.length-1);//计算每个网格的大小
		
		int gridx = (int) Math.floor(terrainx/gridSquareSize);//计算当前坐标属于 的网格坐标
		int gridz = (int) Math.floor(terrainz/gridSquareSize);

		if(gridx>=heights.length-1 || gridz >=heights.length-1
				||gridx<0||gridz<0
				){
			return 0;
		}
		float xcoord = (terrainx%gridSquareSize)/gridSquareSize; //映射到 x,z 属于[0,1] 的单位正方形内
		float zcoord = (terrainz%gridSquareSize)/gridSquareSize;
		
		// barycentric coordinates on triangles
		float answer;
		if(xcoord<= (1-zcoord)){ // 判断所属单位正方形中2个三角形中的哪一个
			answer = barryCentric(
					new Vector3f(0,heights[gridx][gridz],0)
					, new Vector3f(1,heights[gridx+1][gridz],0)
					, new Vector3f(0,heights[gridx][gridz+1],1)
					, new Vector2f(xcoord,zcoord)
					);// 计算 重心的高度
		}else{
			answer = barryCentric(
					new Vector3f(1,heights[gridx+1][gridz],0)
					, new Vector3f(0,heights[gridx+1][gridz+1],0)
					, new Vector3f(0,heights[gridx][gridz+1],1)
					, new Vector2f(xcoord,zcoord)
					);
		}
		
		return answer;
	}
	
	/**
	 * 原理
	 * https://en.wikipedia.org/wiki/Barycentric_coordinate_system
	 * 
	 *  空间三角形 ABC 所在平面有一点P，其中 A(ax,ay,az) B(bx,by,bz) C(cx,cy,cz) P(px,py,pz);
	 *  向量AP AB AC满足等式：
	 *  AP = λ1 AB + λ2 AC		其中λ1 λ2 为常数
	 *  相应的坐标满足
	 *  px-ax = λ1(bx-ax) + λ2(cx-ax)
	 *  py-ay = λ1(by-ay) + λ2(cy-ay)
	 *  pz-az = λ1(bz-az) + λ2(cz-az)
	 *  
	 *  若已知  A B C的坐标 及其 P 中的任意两个坐标(px,py,pz 中选2个) 则可求取 λ1 λ2 和剩余一个P的坐标 
	 *  
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param pos
	 * @return
	 */
	public static float barryCentric(Vector3f p1,Vector3f p2,Vector3f p3 ,Vector2f pos){
		float det = (p2.z - p3.z)*(p1.x-p3.x)+(p3.x-p2.x)*(p1.z-p3.z);
		float l1 = ((p2.z-p3.z)*(pos.x-p3.x)+(p3.x-p2.x)*(pos.y-p3.z))/det;
		float l2 = ((p3.z-p1.z)*(pos.x-p3.x)+(p1.x-p3.x)*(pos.y-p3.z))/det;
		float l3 = 1.f-l1-l2;
		return l1*p1.y+l2*p2.y+l3*p3.y;
	}
	
	public static class TerrainHolder{
		public VAO vao;
		public float[][] heights; 
	}
}
