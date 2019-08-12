package pengrui.reflection;

import pengrui.common.VAO;

public class ReflectionEntity {
	public float xPos = 0, yPos = 0, zPos = 0;//-0.100001f;
	public float xRot = 0, yRot = 0, zRot = 0;
	public float xSca = 1f, ySca = 1f, zSca = 1f;
	public VAO vao;
	public int texture;
	
	public ReflectionEntity(VAO v ,int t) {
		vao = v;
		texture = t;
	}
}
