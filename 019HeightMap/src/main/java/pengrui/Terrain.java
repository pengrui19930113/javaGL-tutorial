package pengrui;

import static pengrui.CommonConstant.*;
public class Terrain {
	public VAO vao;
	public float xPos ,zPos;
	public int texture;
	public int rTexture;
	public int gTexture;
	public int bTexture;
	public int blendMap;
	
	public Terrain(VAO v,int tex,int r,int g ,int b,int map) {
		vao = v;
		texture = tex;
		xPos = -TERRAIN_ONE_LINE_SIZE/2; 
		zPos = -TERRAIN_ONE_LINE_SIZE/2;
		
		rTexture = r;
		gTexture = g;
		bTexture = b;
		blendMap = map;
	}
}
