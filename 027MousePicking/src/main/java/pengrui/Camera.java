package pengrui;

public class Camera {
	public float cameraPosX = 0, cameraPosY = 1f, cameraPosZ = 2f;
	public float cameraRotX = -45, cameraRotY = 45, cameraRotZ = 0;// pitch(-90,90) yaw[0,360) roll[0,360)
	
	public Player player;
	public float distanceFromPlayer;
	
	public Camera(Player player) {
		this.player = player;
		caculateVaraibleOfCamera();
	}
	
	private void caculateVaraibleOfCamera() {
		if(null == player) return;
		float xDis = cameraPosX - player.xPos;
		float yDis = cameraPosY - player.yPos;
		float zDis = cameraPosZ - player.zPos;
		distanceFromPlayer = (float)Math.sqrt(xDis*xDis +yDis*yDis+zDis*zDis);
		if(distanceFromPlayer-0.001>0) {// 如果坐标不重合
			float xNormalizedDirect = -xDis/distanceFromPlayer;
//			float yNormalizedDirect = -yDis/distanceFromPlayer;
			float zNormalizedDirect = -zDis/distanceFromPlayer;
			cameraRotX = -(float) (Math.acos(-zNormalizedDirect)*180/Math.PI);
			float tan_nMapToXOZ_O_Z = xNormalizedDirect/zNormalizedDirect;
			cameraRotY = (float) (Math.atan(tan_nMapToXOZ_O_Z)*180/Math.PI);
			cameraRotZ = 0;
		}else {//如果坐标重合
			distanceFromPlayer = 5;
			cameraPosX = player.xPos;
			cameraPosY = (float) (player.yPos+distanceFromPlayer*Math.sqrt(2)/2);
			cameraPosZ = (float) (player.zPos+distanceFromPlayer*Math.sqrt(2)/2);
			cameraRotX = -45;
			cameraRotY = 0;
			cameraRotZ = 0;
		}
		
	}

	public Player getPlayer() {
		return player;
	}
	
	@Override
	public String toString() {
		return String.format("px:%f,py:%f,pz:%f,cx:%f,cy:%f,cz:%f,rx:%f,ry:%f,rz:%f,distance:%f"
				, player.xPos,player.yPos,player.zPos
				, cameraPosX,cameraPosY,cameraPosZ
				, cameraRotX,cameraRotY,cameraRotZ
				, distanceFromPlayer
				);
	}
}
