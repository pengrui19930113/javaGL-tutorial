package pengrui;

import static pengrui.CommonConstant.*;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ActionManager {
	
	public static void action(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player, Skybox skybox, WaterTile water, long delta) {
//		System.out.println(camera);
//		doEntities(entities,delta);
		
		doCamera(camera,terrain,delta);
		doWater(water,delta);
	}
	
	static void doWater(WaterTile water, long delta) {
		water.moveFactor += WAVE_SPEED*delta/1000.f;
		water.moveFactor %=1;
	}

	static void doCamera(Camera camera,Terrain terrain, long delta) {
		doPlayer(camera.player,terrain,delta);
		float zoomLevel = Mouse.getDWheel()*0.01f; //滚轮向上 是正
		camera.distanceFromPlayer -= zoomLevel;
		if(camera.distanceFromPlayer<0.1f) {
			camera.distanceFromPlayer = 0.1f;
		}
		if(Mouse.isButtonDown(0)){
			float angleChange = Mouse.getDX()*0.3F; // 左负右正
			camera.cameraRotY -= angleChange;
			camera.cameraRotY %= 360;
		}
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY()*0.1f; //上正下负
			camera.cameraRotX += pitchChange;
			if(camera.cameraRotX >90) camera.cameraRotX = 90;
			if(camera.cameraRotX <-90) camera.cameraRotX = -90;
		}
	
		float horizontalDistance = (float)(camera.distanceFromPlayer*Math.sin(Math.toRadians(-camera.cameraRotX)));
		float verticalDistance = (float)(camera.distanceFromPlayer*Math.cos(Math.toRadians(-camera.cameraRotX)));
		float theta = camera.cameraRotY;
		float offsetX = (float) (horizontalDistance*Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance*Math.cos(Math.toRadians(theta)));
//		System.out.println(String.format("dx:%f,dy:%f,dz:%f", offsetX,offsetZ,verticalDistance));
		camera.cameraPosX = camera.player.xPos + offsetX;
		camera.cameraPosZ = camera.player.zPos + offsetZ;
		camera.cameraPosY = camera.player.yPos+verticalDistance;
	}

	static void doEntities(List<Entity> entities, long delta) {
//		entity.entityRotX += (20.0*(delta)/1000);
//		entity.entityRotX %= 360;
//		entity.entityRotY += (10.0*(delta)/1000);
//		entity.entityRotY %= 360;
//		entity.entityRotZ += (15.0*(delta)/1000);
//		entity.entityRotZ %= 360;
	}
	
	static void doPlayer(Player player,Terrain terrain,long delta) {
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
