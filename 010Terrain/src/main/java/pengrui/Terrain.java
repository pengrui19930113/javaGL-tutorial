package pengrui;

import static pengrui.CommonConstant.*;
public class Terrain {
	public VAO vao;
	public float xPos ,zPos;
	public int texture;

	public Terrain(VAO v,int tex) {
		vao = v;
		texture = tex;
		xPos = -TERRAIN_ONE_LINE_SIZE/2; 
		zPos = -TERRAIN_ONE_LINE_SIZE/2;
	}
}
