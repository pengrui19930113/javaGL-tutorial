package pengrui.manager;


import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import pengrui.common.Camera;
import pengrui.common.Light;
import pengrui.entity.Entity;
import pengrui.font.FontEffect;
import pengrui.font.StaticText;
import pengrui.normalMapping.NormalMappingEntity;
import pengrui.player.Player;
import pengrui.shadow.ShadowBox;
import pengrui.skybox.Skybox;
import pengrui.terrain.Terrain;

public class ActionManager {
	
	private static Random random = new Random();
	public static void action(Camera camera, List<Entity> entities, Light light
			, Terrain terrain, Player player, Skybox skybox, List<NormalMappingEntity> normalEntities
			, List<StaticText> texts, FontEffect fontEffect, ShadowBox shadowBox
			, long delta
			) {
//		System.out.println(camera);
//		doEntities(entities,delta);
		doNormalEntities(normalEntities,delta);
		doCamera(camera,terrain,delta);
		doTexts(texts,delta);
		doFontEffect(fontEffect,delta);
		doShadowBox(shadowBox,camera,light,delta);
	}
	
	static void doShadowBox(ShadowBox box,Camera camera, Light light, long delta) {
		if(null == box) return;
		box.update(camera, light, delta);
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
		if(null == camera || null == terrain) return;
		camera.update(terrain, delta);
	}

	static void doEntities(List<Entity> entities, long delta) {
		Entity entity = entities.get(0);
//		entity.xRot += (20.0*(delta)/1000);
//		entity.xRot %= 360;
//		entity.yRot += (10.0*(delta)/1000);
//		entity.yRot %= 360;
//		entity.zRot += (15.0*(delta)/1000);
//		entity.zRot %= 360;
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			entity.zPos -= 0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			entity.zPos += 0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			entity.xPos += 0.1f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			entity.xPos -= 0.1f;
		}
	}
	
	static void doPlayer(Player player,Terrain terrain,long delta) {
		if(null == player) return;
		
	
	}
}
