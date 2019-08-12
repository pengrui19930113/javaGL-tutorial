package pengrui.manager;

import static pengrui.config.CommonConstant.*;

import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import pengrui.common.Camera;
import pengrui.common.Light;
import pengrui.entity.Entity;
import pengrui.flare.FlareManager;
import pengrui.font.FontEffect;
import pengrui.font.StaticText;
import pengrui.normalMapping.NormalMappingEntity;
import pengrui.player.Player;
import pengrui.skybox.Skybox;
import pengrui.terrain.Terrain;

public class ActionManager {
	
	private static Random random = new Random();
	public static void action(Camera camera, List<Entity> entities, Light light
			, Terrain terrain, Player player, Skybox skybox, List<NormalMappingEntity> normalEntities
			, List<StaticText> texts, FontEffect fontEffect, FlareManager flareManager, long delta
			) {
//		System.out.println(camera);
//		doEntities(entities,delta);
		doNormalEntities(normalEntities,delta);
		doCamera(camera,terrain,delta);
		doTexts(texts,delta);
		doFontEffect(fontEffect,delta);
		doFlare(flareManager,camera,delta);
	}
	
	static void doFlare(FlareManager flareManager,Camera camera, long delta) {
		if(null == flareManager) return;
		flareManager.update(camera, delta);
	}

	static void doTexts(List<StaticText> texts, long delta) {
		if(null == texts || texts.isEmpty()) return;
		float secondDelta = delta/1000f;
		float range = 2*secondDelta;
		float r = random.nextFloat()*range-range/2f;
		float g = random.nextFloat()*range-range/2f;
		float b = random.nextFloat()*range-range/2f;
		for(StaticText text:texts) {

			text.red += r;
			text.green += g;
			text.blue += b;
			if(text.red<0) text.red = 0;
			if(text.red>1) text.red = 1;
			if(text.green<0) text.green = 0;
			if(text.green>1) text.green = 1;
			if(text.blue<0) text.blue = 0;
			if(text.blue>1) text.blue = 1;
		}
	}

	static void doFontEffect(FontEffect fontEffect, long delta) {
		if(null == fontEffect) return;
		float secondDelta = delta/1000f;
		float range = 3*secondDelta;
		FontEffect e = fontEffect;
		e.width+=(random.nextFloat()*2*range-range)*0.1;
		if(e.width>0.5){
			e.width =0.5f;
		}
		if(e.width<0.1){
			e.width = 0.1f;
		}
		e.edge+=(random.nextFloat()*2*range-range);
		if(e.edge>0.5){
			e.edge =0.5f;
		}
		if(e.edge<0.1){
			e.edge = 0.1f;
		}
		e.borderWidth+=(random.nextFloat()*2*range-range);
		if(e.borderWidth>0.4){
			e.borderWidth = 0.4f;
		}
		if(e.borderWidth<0.01){
			e.borderWidth= 0.01f;
		}
		e.borderEdge+=(random.nextFloat()*2*range-range);
		if(e.borderEdge>0.4){
			e.borderEdge = 0.4f;
		}
		if(e.borderEdge<0.01){
			e.borderEdge= 0.01f;
		}
		e.xOffset+=(random.nextFloat()*2*range-range)*0.1f;
		if(e.xOffset>0.01){
			e.xOffset = 0.01f;
		}
		if(e.xOffset<-0.01){
			e.xOffset= -0.01f;
		}
		e.yOffset+=(random.nextFloat()*2*0.01f-0.01f)*0.1f;
		if(e.yOffset>0.01){
			e.yOffset = 0.01f;
		}
		if(e.yOffset<0.01){
			e.yOffset= 0.01f;
		}
		range = secondDelta*1/100;
		e.redOutlineColour+=(random.nextFloat()*2*range-range)*secondDelta;
		if(e.redOutlineColour>0.3){
			e.redOutlineColour = 0.3f;
		}
		if(e.redOutlineColour<0.1f){
			e.redOutlineColour= 0.1f;
		}
		e.greenOutlineColour+=(random.nextFloat()*2*range-range)*secondDelta;
		if(e.greenOutlineColour>0.3f){
			e.greenOutlineColour = 0.3f;
		}
		if(e.greenOutlineColour<0.1f){
			e.greenOutlineColour= 0.1f;
		}
		e.blueOutlineColour+=(random.nextFloat()*2*range-range)*secondDelta;
		if(e.blueOutlineColour>0.3f){
			e.blueOutlineColour = 0.3f;
		}
		if(e.blueOutlineColour<0.1f){
			e.blueOutlineColour= 0.1f;
		}
	}

	static void doNormalEntities(List<NormalMappingEntity> normalEntities, long delta) {
		if(null == normalEntities) return;
		for(NormalMappingEntity entity:normalEntities) {
			entity.yRot += delta/10;
			entity.yRot %=360;
		}
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
			camera.yRot -= angleChange;
			camera.yRot %= 360;
		}
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY()*0.1f; //上正下负
			camera.xRot += pitchChange;
			if(camera.xRot >90) camera.xRot = 90;
			if(camera.xRot <-90) camera.xRot = -90;
		}
	
		float horizontalDistance = (float)(camera.distanceFromPlayer*Math.sin(Math.toRadians(-camera.xRot)));
		float verticalDistance = (float)(camera.distanceFromPlayer*Math.cos(Math.toRadians(-camera.xRot)));
		float theta = camera.yRot;
		float offsetX = (float) (horizontalDistance*Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance*Math.cos(Math.toRadians(theta)));
//		System.out.println(String.format("dx:%f,dy:%f,dz:%f", offsetX,offsetZ,verticalDistance));
		camera.xPos = camera.player.xPos + offsetX;
		camera.zPos = camera.player.zPos + offsetZ;
		camera.yPos = camera.player.yPos+verticalDistance;
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
