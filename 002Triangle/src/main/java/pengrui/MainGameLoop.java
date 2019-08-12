package pengrui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

/**
 * Triangles
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
	static final String WINDOW_TITLE = "Hello Triangles";
	

	
    public static void main( String[] args )
    {
//    	useDrawArray();
    	useDrawElement();
    }
    
	final static float[] TRIANGLE_VERTICES_POISITION_DATA1 ={
			-.5f,	.5f,	.0f
			,-.5f,	-.5f,	.0f
			,.5f,	-.5f,	.0f
			
			,.5f,	-.5f,	.0f
			,.5f,	.5f,	.0f
			,-.5f,	.5f,	.0f
	};
    static void useDrawArray() {
    	System.out.println("useDrawArray");
    	System.getProperties().setProperty("org.lwjgl.librarypath", "D:\\.m2\\repository\\org\\lwjgl\\lwjgl\\lwjgl-platform\\2.9.3");
   	 	Display.setLocation(WINDOW_X_POSITION, WINDOW_Y_POSITION);
        try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(),new ContextAttribs(3,3).withForwardCompatible(true).withProfileCompatibility(true));
			Display.setTitle(WINDOW_TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
        List<Integer> vbos = new LinkedList<Integer>();
        List<Integer> vaos = new LinkedList<Integer>();
        //load data to vao
        int vaoid = GL30.glGenVertexArrays();
        vaos.add(vaoid);
        GL30.glBindVertexArray(vaoid);
        int vboid = GL15.glGenBuffers();
        vbos.add(vboid);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(TRIANGLE_VERTICES_POISITION_DATA1.length);
        buffer.put(TRIANGLE_VERTICES_POISITION_DATA1);
        buffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,0,0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        
        GL11.glClearColor(0.2f, 0.6f, 0.1f, 1);//set background colour;
        while(!Display.isCloseRequested()) {
        	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        	GL30.glBindVertexArray(vaoid);
        	GL20.glEnableVertexAttribArray(0);
        	GL11.glDrawArrays(GL11.GL_TRIANGLES, 0,TRIANGLE_VERTICES_POISITION_DATA1.length);// use draw array
        	GL20.glDisableVertexAttribArray(0);
        	GL30.glBindVertexArray(0);
        	
        	Display.sync(FPS);
        	Display.update();
        }
        
        for(int vbo:vbos) {
       	 GL15.glDeleteBuffers(vbo);
        }
        for(int vao:vaos) {
       	 GL30.glDeleteVertexArrays(vao);
        }
        Display.destroy();
   }
    
	static final float[] TRIANGLE_VERTICES_POISITION_DATA2 = {
			-.5f,	.5f,	.0f
			,-.5f,	-.5f,	.0f
			,.5f,	-.5f,	.0f
			,.5f,	.5f,	.0f
	};
	static final int[] TRIANGLE_VERTICES_INDICES_DATA = {
			0,	1,	2
			,0	,2	,3
	};
	
    static void useDrawElement() {
    	System.out.println("useDrawElement");
    	System.getProperties().setProperty("org.lwjgl.librarypath", "D:\\.m2\\repository\\org\\lwjgl\\lwjgl\\lwjgl-platform\\2.9.3");
   	 	Display.setLocation(WINDOW_X_POSITION, WINDOW_Y_POSITION);
        try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(),new ContextAttribs(3,3).withForwardCompatible(true).withProfileCompatibility(true));
			Display.setTitle(WINDOW_TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
        List<Integer> vbos = new LinkedList<Integer>();
        List<Integer> vaos = new LinkedList<Integer>();
        //load data to vao
        int vaoid = GL30.glGenVertexArrays();
        vaos.add(vaoid);
        GL30.glBindVertexArray(vaoid);
        //vertex array
        int vboid = GL15.glGenBuffers();
        vbos.add(vboid);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(TRIANGLE_VERTICES_POISITION_DATA2.length);
        buffer.put(TRIANGLE_VERTICES_POISITION_DATA2);
        buffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,0,0);
        
        //element array
        vboid = GL15.glGenBuffers();
        vbos.add(vboid);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboid);
        IntBuffer buffer2 = BufferUtils.createIntBuffer(TRIANGLE_VERTICES_INDICES_DATA.length);
        buffer2.put(TRIANGLE_VERTICES_INDICES_DATA);
        buffer2.flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer2, GL15.GL_STATIC_DRAW);
     
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        
        GL11.glClearColor(0.2f, 0.3f, 0.4f, 1);//set background colour;
        while(!Display.isCloseRequested()) {
        	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        	GL30.glBindVertexArray(vaoid);
        	GL20.glEnableVertexAttribArray(0);
        	GL11.glDrawElements(GL11.GL_TRIANGLES,TRIANGLE_VERTICES_INDICES_DATA.length,GL11.GL_UNSIGNED_INT,0);// use draw array
        	GL20.glDisableVertexAttribArray(0);
        	GL30.glBindVertexArray(0);
        	
        	Display.sync(FPS);
        	Display.update();
        }
        
        for(int vbo:vbos) {
       	 GL15.glDeleteBuffers(vbo);
        }
        for(int vao:vaos) {
       	 GL30.glDeleteVertexArrays(vao);
        }
        Display.destroy();
   }
}
