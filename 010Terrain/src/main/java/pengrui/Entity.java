package pengrui;

public class Entity {
	public float entityPosX = 0, entityPosY = 0, entityPosZ = 0;//-0.100001f;
	public float entityRotX = 0, entityRotY = 0, entityRotZ = 0;
	public float entityScaX = 1, entityScaY = 1, entityScaZ = 1;
	public VAO vao;
	public int texture;
	
	public Entity(VAO v ,int t) {
		vao = v;
		texture = t;
	}
}
