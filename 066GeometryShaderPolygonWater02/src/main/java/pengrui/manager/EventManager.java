package pengrui.manager;

import java.util.List;

import org.lwjgl.input.Keyboard;

import pengrui.common.Camera;
import pengrui.common.Light;
import pengrui.entity.Entity;
import pengrui.player.Player;
import pengrui.skybox.Skybox;
import pengrui.terrain.Terrain;

public class EventManager {
	static float CAMERA_MOVE_SPEED = 0.1f;
	static float CAMERA_ROTATE_SPEED = 3f;
	public static void event(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player, Skybox skybox) {
		keyboardEvent(camera, entities, light, terrain, player);
		mouseEvent(camera, entities, light, terrain, player);
	}
	static void mouseEvent(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player) {
	}
	static void keyboardEvent(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player) {
		//doCamera(camera);
	}
	
	static void doCamera(Camera camera) {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) camera.zPos -=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) camera.zPos +=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) camera.xPos -=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) camera.xPos +=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) camera.yRot +=CAMERA_ROTATE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_E)) camera.yRot -=CAMERA_ROTATE_SPEED;
		if(camera.xRot>89) camera.xRot = 89;	if(camera.xRot<-89) camera.xRot =-89f;//避免万向节死锁
		camera.yPos %= 360; camera.zRot %= 360;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_O)) camera.yPos += CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_P)) camera.yPos -= CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_N)) camera.xRot += CAMERA_ROTATE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_M)) camera.xRot -= CAMERA_ROTATE_SPEED;
	}
}
