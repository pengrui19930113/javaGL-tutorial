package pengrui.common;

import org.lwjgl.opengl.GL20;

public abstract interface Shaderable {

	//注意location 可以是0
	
	abstract public int getProgramID();
	abstract public int getFragmentShaderID();
	abstract public int getVertexShaderID();
	
	default public int getGeometryShaderID() {
		return -1;
	}
	
	static void test(){
		GL20.glDetachShader(0, 0);
		GL20.glDeleteShader(0);
	}
}
