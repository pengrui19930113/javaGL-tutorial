package pengrui.common;

import pengrui.player.Player;

public class Camera {
	public float xPos = 0, yPos = 1f, zPos = 2f;
	public float xRot = -45, yRot = 45, zRot = 0;// pitch(-90,90) yaw[0,360) roll[0,360)
	
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
		return String.format("px:%f,py:%f,pz:%f,cx:%f,cy:%f,cz:%f,rx:%f,ry:%f,rz:%f,distance:%f"
				, player.xPos,player.yPos,player.zPos
				, xPos,yPos,zPos
				, xRot,yRot,zRot
				, distanceFromPlayer
				);
	}
}
