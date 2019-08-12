package pengrui.common;

import org.lwjgl.input.Mouse;

import pengrui.player.Player;
import pengrui.terrain.Terrain;

public class Camera {
	public float xPos = 0, yPos = 0f, zPos = 0f;
	public float xRot = 0, yRot = 0, zRot = 0;// pitch(-90,90) yaw[0,360) roll[0,360)
	
	public Player player;
	public float distanceFromPlayer;
	
	public Camera(Player player) {
		this.player = player;
		caculateVaraibleOfCamera();
	}
	
	private void caculateVaraibleOfCamera() {
		if(null == player) return;
		float xDis = xPos - player.xPos;
		float yDis = yPos - player.yPos;
		float zDis = zPos - player.zPos;
		distanceFromPlayer = (float)Math.sqrt(xDis*xDis +yDis*yDis+zDis*zDis);
		if(distanceFromPlayer-0.001>0) {// 如果坐标不重合
			float xNormalizedDirect = -xDis/distanceFromPlayer;
//			float yNormalizedDirect = -yDis/distanceFromPlayer;
			float zNormalizedDirect = -zDis/distanceFromPlayer;
			xRot = -(float) (Math.acos(-zNormalizedDirect)*180/Math.PI);
			float tan_nMapToXOZ_O_Z = xNormalizedDirect/zNormalizedDirect;
			yRot = (float) (Math.atan(tan_nMapToXOZ_O_Z)*180/Math.PI);
			zRot = 0;
		}else {//如果坐标重合
			distanceFromPlayer = 5;
			xPos = player.xPos;
			yPos = (float) (player.yPos+distanceFromPlayer*Math.sqrt(2)/2);
			zPos = (float) (player.zPos+distanceFromPlayer*Math.sqrt(2)/2);
			xRot = -45;
			yRot = 0;
			zRot = 0;
		}
		
	}

	public Player getPlayer() {
		return player;
	}
	
	@Override
	public String toString() {
		return String.format("cx:%f,cy:%f,cz:%f,rx:%f,ry:%f,rz:%f,distance:%f"
				, xPos,yPos,zPos
				, xRot,yRot,zRot
				, distanceFromPlayer
				);
	}
//	@Override
//	public String toString() {
//		return String.format("px:%f,py:%f,pz:%f,cx:%f,cy:%f,cz:%f,rx:%f,ry:%f,rz:%f,distance:%f"
//				, player.xPos,player.yPos,player.zPos
//				, xPos,yPos,zPos
//				, xRot,yRot,zRot
//				, distanceFromPlayer
//				);
//	}
	
	public void update(Terrain terrain,long delta) {
		if(null!=player) {
			player.update(terrain, delta);
			this.action(delta);
		}else {
			//TODO
		}
	}
	
	private void action(long delta) {
		Camera camera = this;
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
}
