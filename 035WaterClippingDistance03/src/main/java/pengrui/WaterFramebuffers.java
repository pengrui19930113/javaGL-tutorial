package pengrui;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class WaterFramebuffers {

	public static final int REFLECTION_WIDTH = 320;
	public static final int REFLECTION_HEIGHT = 180;
	public static final int REFRACTION_WIDTH = 1280;
	public static final int REFRACTION_HEIGHT = 720;
	
	public int reflectionFrameBuffer;
	public int reflectionTexture;
	public int reflectionDepthBuffer;
	
	public int refractionFrameBuffer;
	public int refractionTexture;
	public int refractionDepthTexture;
	
	public WaterFramebuffers(List<Integer> textures,List<Integer> framebuffers,List<Integer> renderBuffers) {
		reflectionFrameBuffer = GL30.glGenFramebuffers();
		framebuffers.add(reflectionFrameBuffer);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, reflectionFrameBuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		reflectionTexture = GL11.glGenTextures();
		textures.add(reflectionTexture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, reflectionTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, REFLECTION_WIDTH, REFLECTION_HEIGHT, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, reflectionTexture, 0);//将纹理附件以	颜色缓冲区	的角色附加到当前绑定的FBO上
		reflectionDepthBuffer = GL30.glGenRenderbuffers();
		renderBuffers.add(reflectionDepthBuffer);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, reflectionDepthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, REFLECTION_WIDTH, REFLECTION_HEIGHT);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, reflectionDepthBuffer);
		
		refractionFrameBuffer = GL30.glGenFramebuffers();
		framebuffers.add(refractionFrameBuffer);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, refractionFrameBuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		refractionTexture = GL11.glGenTextures();
		textures.add(refractionTexture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, refractionTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, REFRACTION_WIDTH, REFRACTION_HEIGHT, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, refractionTexture, 0);//将纹理附件以	颜色缓冲区	的角色附加到当前绑定的FBO上	
		refractionDepthTexture = GL11.glGenTextures();
		textures.add(refractionDepthTexture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, refractionDepthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, REFRACTION_WIDTH, REFRACTION_HEIGHT, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, refractionDepthTexture, 0);//将纹理附件以	深度缓冲区	的角色附加到当前绑定的FBO上
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
}
