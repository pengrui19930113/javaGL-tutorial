package pengrui;

public class Player {
	
	public VAO vao;
	public float xPos,yPos,zPos;
	public float xRot,yRot,zRot;
	public float xSca = 0.1f,ySca = 0.1f,zSca = 0.1f;
	public int texture;
	public float shineDamper = 1;
	public float reflectivity = 0.8f;
	
	public float currentMovementSpeed = 0.3f;
	public float currentTurnSpeed = 0.1f;
	public float upwardsSpeed = 0;
	
	public boolean isInAir = false;
	public Player(VAO v,int tex) {
		vao = v;
		texture = tex;
	}
}
