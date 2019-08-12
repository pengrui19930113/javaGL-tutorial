package pengrui;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * Hello Window
 *
 */
public class MainGameLoop 
{
	static final int 
			WIDTH = 1024
			,HEIGHT = 768
			,WINDOW_X_POSITION = 0
			,WINDOW_Y_POSITION = 0
			,FPS = 60;
	static final String WINDOW_TITLE = "Hello Window";
    public static void main( String[] args )
    {
    	System.getProperties().setProperty("org.lwjgl.librarypath", "D:\\.m2\\repository\\org\\lwjgl\\lwjgl\\lwjgl-platform\\2.9.3");
        Display.setLocation(WINDOW_X_POSITION, WINDOW_Y_POSITION);
        try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(),new ContextAttribs(3,3).withForwardCompatible(true).withProfileCompatibility(true));
			Display.setTitle(WINDOW_TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
        
        while(!Display.isCloseRequested()) {
        	Display.sync(FPS);
        	Display.update();
        }
        Display.destroy();
    }
}
