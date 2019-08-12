package pengrui;

import java.util.List;


public class EventManager {
	
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
		
	}
}
