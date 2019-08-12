package pengrui;

import java.util.List;

public class GuiEntity {
	
	
	public float xPos,yPos;
	public float xSca,ySca;
	public int texture;
	public GuiEntity(float xPos, float yPos, float xSca, float ySca, int texture) {
		super();
		this.xPos = xPos;
		this.yPos = yPos;
		this.xSca = xSca;
		this.ySca = ySca;
		this.texture = texture;
	}
	public static VAO vao;
	public static void createVao(List<Integer> vaos, List<Integer> vbos) {
		float[] position = { //normal device coordination
				-1,1//left top 
				,-1,-1// left bottom
				,1,1//right top
				,1,-1//right bottom
		} ;
		vao = GuiEntityVaoLoader.createGuiEntityVao(vaos, vbos, position);
	}
}
