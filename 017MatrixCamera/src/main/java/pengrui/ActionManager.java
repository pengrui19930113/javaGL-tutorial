package pengrui;

import static pengrui.CommonConstant.*;

import java.util.List;

import org.lwjgl.input.Keyboard;

public class ActionManager {
	
	public static void action(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player, long delta) {
//		System.out.println(camera);
//		doEntities(entities,delta);
		doCamera(camera,delta);
	}
	
	static void doCamera(Camera camera, long delta) {
		camera.update(delta);
	}

	static void doEntities(List<Entity> entities, long delta) {
//		entity.entityRotX += (20.0*(delta)/1000);
//		entity.entityRotX %= 360;
//		entity.entityRotY += (10.0*(delta)/1000);
//		entity.entityRotY %= 360;
//		entity.entityRotZ += (15.0*(delta)/1000);
//		entity.entityRotZ %= 360;
	}
	
	static void doPlayer(Player player,long delta) {
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
		
		if(player.yPos < TERRAIN_HEIGHT) {
			player.upwardsSpeed = 0;
			player.isInAir = false;
			player.yPos = TERRAIN_HEIGHT;
		}
	}
}
