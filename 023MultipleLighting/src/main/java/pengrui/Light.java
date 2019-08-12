package pengrui;

public class Light {
	public static final int MAX_LIGHTS = 4;
	public float xPos = -5,yPos = 5,zPos = 5;
	public float red = 1,green = 1,blue = 1;
	public Light(float xPos, float yPos, float zPos, float red, float green, float blue) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public Light() {
	}
}
