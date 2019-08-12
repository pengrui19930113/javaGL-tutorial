package pengrui.water;

import java.util.List;

import pengrui.common.VAO;

public class PolygonWater {
	
	private static final float WAVE_SPEED = 0.4f;
	public VAO vao;
	public float time;
	public float waveSpeed;
	
	public PolygonWater(List<Integer>vaos,List<Integer>vbos) {
		waveSpeed = WAVE_SPEED;
		time = 0;
		this.vao = PolygonWaterVaoLoader.createPolygonWaterVao(vaos, vbos);
	}
	
	public void update(long delta) {
		time += WAVE_SPEED*delta/1000f;
		time %= 1;
	}
}
