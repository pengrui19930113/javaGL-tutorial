package pengrui.postProcessing;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import static pengrui.config.CommonConstant.*;

public class Fbo {
	
	public static final int NONE = 0
			,DEPTH_TEXTURE = 1
			,DEPTH_RENDER_BUFFER=2;
	
	public int type;
	public final int width,height;
	public int framebuffer,colourTexture,depthTexture,depthBuffer,colourBuffer;
	public boolean isMultisample = false;
	
	public Fbo(List<Integer> fbos,List<Integer> rbos,List<Integer>textures,int width,int height,int depthBufferType,boolean isMultisample) {
		
		this.isMultisample = isMultisample;
		this.width = width;
		this.height = height;
		//create framebuffer
		this.framebuffer = GL30.glGenFramebuffers();
		fbos.add(this.framebuffer);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		//create texturebuffer
		if(!isMultisample) {
			this.colourTexture = GL11.glGenTextures();
			textures.add(this.colourTexture);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,this.colourTexture);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,GL12.GL_CLAMP_TO_EDGE);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.colourTexture, 0);
		}else {
			this.colourBuffer = GL30.glGenRenderbuffers();
			rbos.add(this.colourBuffer);
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.colourBuffer);
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER	, this.colourBuffer);
		}

		//create depth attachement
		if(DEPTH_TEXTURE==depthBufferType) {
			type = DEPTH_TEXTURE;
			this.depthTexture = GL11.glGenTextures();
			textures.add(this.depthTexture);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthTexture);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, this.width, this.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, this.depthTexture, 0);
		}else if(DEPTH_RENDER_BUFFER == depthBufferType) {
			type = DEPTH_RENDER_BUFFER;
			this.depthBuffer = GL30.glGenRenderbuffers();
			rbos.add(this.depthBuffer);
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.depthBuffer);
			if(isMultisample) {
				GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL14.GL_DEPTH_COMPONENT24, width, height);
			}else {
				GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, this.width, this.height);
			}
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, this.depthBuffer);
		}else {
			type = NONE;
		}
		// unbind
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0,0, WIDTH, HEIGHT);
	}
	
	public Fbo(List<Integer> fbos,List<Integer> rbos,List<Integer>textures,int width,int height,int depthBufferType) {
		this(fbos, rbos, textures, width, height, depthBufferType, false);
	}
}
