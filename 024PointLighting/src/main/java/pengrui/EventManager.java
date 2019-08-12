package pengrui;

import java.util.List;

import org.lwjgl.input.Keyboard;

public class EventManager {
	static float CAMERA_MOVE_SPEED = 0.1f;
	static float CAMERA_ROTATE_SPEED = 3f;
	public static void event(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player) {
		keyboardEvent(camera, entities, light, terrain, player);
		mouseEvent(camera, entities, light, terrain, player);
	}
	static void mouseEvent(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player) {
	}
	static void keyboardEvent(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player) {
		//doCamera(camera);
	}
	


	
	
	static void doCamera(Camera camera) {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) camera.cameraPosZ -=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) camera.cameraPosZ +=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) camera.cameraPosX -=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) camera.cameraPosX +=CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) camera.cameraRotY +=CAMERA_ROTATE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_E)) camera.cameraRotY -=CAMERA_ROTATE_SPEED;
		if(camera.cameraRotX>89) camera.cameraRotX = 89;	if(camera.cameraRotX<-89) camera.cameraRotX =-89f;//避免万向节死锁
		camera.cameraPosY %= 360; camera.cameraRotZ %= 360;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_O)) camera.cameraPosY += CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_P)) camera.cameraPosY -= CAMERA_MOVE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_N)) camera.cameraRotX += CAMERA_ROTATE_SPEED;
		if(Keyboard.isKeyDown(Keyboard.KEY_M)) camera.cameraRotX -= CAMERA_ROTATE_SPEED;
	}
}
