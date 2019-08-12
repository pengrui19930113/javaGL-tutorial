package pengrui;

public class NormalMappingEntity {
	public float xPos = 0, yPos = 1, zPos = -2f;//-0.100001f;
	public float xRot = 0, yRot = 0, zRot = 0;
	public float xSca = 0.1f, ySca = 0.1f, zSca = 0.1f;
	public VAO vao;
	public int texture;//modelTexture
	public int normalMap;//normalTexture
	public float shineDamper = 16;
	public float reflectivity = 0.8f;
	
	public NormalMappingEntity(VAO vao,int texture,int normalMap) {
		this.vao = vao;
		this.texture = texture;
		this.normalMap = normalMap;
	}
}
