package pengrui.player;

import static pengrui.config.CommonConstant.GRAVITY;
import static pengrui.config.CommonConstant.JUMP_POWER;
import static pengrui.config.CommonConstant.MOVE_SPEED;
import static pengrui.config.CommonConstant.TURN_SPEED;

import org.lwjgl.input.Keyboard;

import pengrui.common.VAO;
import pengrui.terrain.Terrain;

public class Player {
	
	public VAO vao;
	public float xPos,yPos,zPos;
	public float xRot,yRot = 180f,zRot;
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
	
	@Override
	public String toString() {
		return String.format("pos x:%s,y:%s,z:%s", xPos,yPos,zPos);
	}
	
	public void update(Terrain terrain,long delta) {
		this.action(terrain, delta);
	}
	
	private void action(Terrain terrain,long delta) {
		Player player = this;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			player.currentMovementSpeed = MOVE_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			player.currentMovementSpeed = -MOVE_SPEED;
		} else {
			player.currentMovementSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			player.currentTurnSpeed = TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			player.currentTurnSpeed = -TURN_SPEED;
		} else {
			player.currentTurnSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			if(!player.isInAir){
				player.upwardsSpeed = JUMP_POWER;
				player.isInAir = true;
			}
		}
		
		float frameTimeSeconds = delta/1000f;
		player.yRot += player.currentTurnSpeed*frameTimeSeconds;
		float distance = player.currentMovementSpeed * frameTimeSeconds;
		float dx = (float) (distance *Math.sin(Math.toRadians(player.yRot)));
		float dz = (float) (distance *Math.cos(Math.toRadians(player.yRot)));
		player.xPos += dx;
		player.zPos += dz;
		
		player.upwardsSpeed += GRAVITY*frameTimeSeconds;
		player.yPos += player.upwardsSpeed;
		
		float terrainHeight = terrain.getHeightOfTerrain(player.xPos,player.zPos);
		if(player.yPos < terrainHeight) {
			player.upwardsSpeed = 0;
			player.isInAir = false;
			player.yPos = terrainHeight;
		}
	}
}
