package pengrui.shadow;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class ShadowFrameBuffer {
	public int width;
	public int height;
	public int fbo;
	public int shadowMap;
	
	public ShadowFrameBuffer(List<Integer>textures,List<Integer> fbos,int width,int height) {
		this.width = width;
		this.height = height;
		fbo = GL30.glGenFramebuffers();
		fbos.add(fbo);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glDrawBuffer(GL11.GL_NONE);//由于只是得到深度纹理 所以不需要读写的颜色缓冲
		GL11.glReadBuffer(GL11.GL_NONE);
		shadowMap = GL11.glGenTextures();
		textures.add(shadowMap);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMap);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_LINEAR); 
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST); // 注意没有使用线性
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, shadowMap, 0);
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		if(GL30.GL_FRAMEBUFFER_COMPLETE != GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)) {
			throw new RuntimeException("framebuffer error");
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
}
