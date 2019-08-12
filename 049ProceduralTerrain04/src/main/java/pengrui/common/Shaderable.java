package pengrui.common;

public abstract interface Shaderable {

	//注意location 可以是0
	
	abstract public int getProgramID();
	abstract public int getFragmentShaderID();
	abstract public int getVertexShaderID();
	
	default public int getGeometryShaderID() {
		return -1;
	}
}
