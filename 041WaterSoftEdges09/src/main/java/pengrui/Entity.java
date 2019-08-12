package pengrui;

public class Entity {
	public float entityPosX = 0, entityPosY = 0, entityPosZ = 0;//-0.100001f;
	public float entityRotX = 0, entityRotY = 0, entityRotZ = 0;
	public float entityScaX = 0.1f, entityScaY = 0.1f, entityScaZ = 0.1f;
	public VAO vao;
	public int texture;
	public int numberOfRows = 1;//默认一行一列
	public float shineDamper = 16;
	public float reflectivity = 0.8f;
	public boolean hasTransparency = false;
	public boolean useFakeLighting = false;
	
	public int textureIndex = 0;//默认一样一列只有一个纹理图片 索引为0
	
	public Entity(VAO v ,int t) {
		vao = v;
		texture = t;
	}
	public Entity(VAO v ,int t,int ti,int nor) {
		vao = v;
		texture = t;
		textureIndex = ti;
		numberOfRows = nor;
	}
	
	public float getTextureXOffset() {//纹理坐标归一化
		return (float)(textureIndex%numberOfRows)/(float)numberOfRows;
	}
	
	public float getTextureYOffset() {
		return (float)(textureIndex/numberOfRows)/(float)numberOfRows;
	}
}
