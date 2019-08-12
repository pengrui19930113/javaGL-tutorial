package pengrui;

public class CommonConstant {
	//window about
	public static final int WIDTH = 1024, HEIGHT = 768, WINDOW_X_POSITION = 0, WINDOW_Y_POSITION = 0, FPS = 60;

	
	//shader about
	public static final int PROGRAM_ATTRIBUTE_POSITION_INDEX = 0;
	public static final String PROGRAM_ATTRIBUTE_POSITION_NAME = "position";
	
	public static final int PROGRAM_ATTRIBUTE_TEXTURE_COORDS_INDEX = 1;
	public static final String PROGRAM_ATTRIBUTE_TEXTURE_COORDS_NAME = "textureCoords";
	
	public static final int PROGRAM_ATTRIBUTE_NORMALS_INDEX = 2;
	public static final String PROGRAM_ATTRIBUTE_NORMALS_NAME = "normal";
	
	public static final int LOG_LENGTH = 512;
	
	//terrain about
	public static final int TERRAIN_ONE_LINE_VERTEX_COUNT = 11;
	public static final float TERRAIN_ONE_LINE_SIZE = 10;
	public static float TERRAIN_HEIGHT = 0;
	
	public static final float MAX_HEIGHT = 2;
	public static final float MAX_PIXEL_COLOUR = 256*256*256;
	
	//sky color
	public static final float SKY_COLOR_RED = 0.1F
			, SKY_COLOR_GREEN = 0.3F
			, SKY_COLOR_BLUE = 0.4F;
	
	//player about
	public static final float MOVE_SPEED = 5f; 
	public static final float TURN_SPEED = 100F;
//	public static final float ROTATION_SPEED = 1.1F;// no use
	public static final float GRAVITY = -0.5f;
	public static final float JUMP_POWER = 0.15f;
}
