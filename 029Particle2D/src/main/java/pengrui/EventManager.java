package pengrui;

import java.util.List;

import org.lwjgl.input.Keyboard;

public class EventManager {
	static float CAMERA_MOVE_SPEED = 0.1f;
	static float CAMERA_ROTATE_SPEED = 3f;
	public static void event(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player, Skybox skybox, ParticleSystem system) {
		keyboardEvent(camera, entities, light, terrain, player,skybox,system);
		mouseEvent(camera, entities, light, terrain, player,skybox,system);
	}
	static void mouseEvent(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player, Skybox skybox, ParticleSystem system) {
	}
	static void keyboardEvent(Camera camera, List<Entity> entities, Light light, Terrain terrain, Player player, Skybox skybox, ParticleSystem system) {
		//doCamera(camera);
		doParticles(system);
	}
	static void doParticles(ParticleSystem system) {
		if(Keyboard.isKeyDown(Keyboard.KEY_Y)){
			system.particles.add(new Particle(5,0,5,0,30,0,1,2));
			system.particles.add(new Particle(-5,0,5,0,30,0,1,2));
			System.out.println("put after"+system.particles);
//			system.genParticles(0, 0, 5, 20);
		}
	}
	static void networkEvent() {
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
