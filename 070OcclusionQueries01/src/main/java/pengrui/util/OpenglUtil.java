package pengrui.util;


import org.lwjgl.opengl.GL11;

public class OpenglUtil {
	public static void clearError() {
		int count = 1;
		int errCode;
		while(GL11.GL_NO_ERROR != (errCode = GL11.glGetError())) {
			StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
			System.out.println("clear error,call position: "+stacks[2]+" count:"+(count++)+" errCode:"+errCode);
//			System.out.println("clear error,call position=[class:"+stacks[2].getClassName()+",method:"+stacks[2].getMethodName()+",line:"+stacks[2].getLineNumber()+"]");
		};
	}
	/**
	 * GL_NO_ERROR ：（0）当前无错误值
		GL_INVALID_ENUM ：（1280）仅当使用非法枚举参数时，如果使用该参数有指定环境，则返回 GL_INVALID_OPERATION 
		GL_INVALID_VALUE ：（1281）仅当使用非法值参数时，如果使用该参数有指定环境，则返回 GL_INVALID_OPERATION 
		GL_INVALID_OPERATION ：（1282）命令的状态集合对于指定的参数非法。
		GL_STACK_OVERFLOW ：（1283）压栈操作超出堆栈大小。
		GL_STACK_UNDERFLOW ：（1284）出栈操作达到堆栈底部。
		GL_OUT_OF_MEMORY ：（1285）不能分配足够内存时。
		GL_INVALID_FRAMEBUFFER_OPERATION ：（1286）当操作未准备好的真缓存时。
		GL_CONTEXT_LOST ：（1287）由于显卡重置导致 OpenGL context 丢失。
	 */
	public static void detectError() {
		int errCode;
		if(GL11.GL_NO_ERROR != (errCode = GL11.glGetError()))  throw new RuntimeException("errcode:"+errCode);
	}
}
