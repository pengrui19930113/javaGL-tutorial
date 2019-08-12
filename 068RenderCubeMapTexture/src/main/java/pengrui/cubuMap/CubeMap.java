package pengrui.cubuMap;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import pengrui.common.Textureable;

import static pengrui.config.CommonConstant.*;
public class CubeMap implements Textureable{
	private static float NEAR_PLANE = 0.1F;
	private static float FAR_PLANE = 200F;
	private static final float FOV = 90;//必须为90 因为渲染6次的覆盖空间刚好构成一个立方体的
	private static final float ASPECT_RATIO = 1;
	
	public int texture;
	public int size;
	public float xPos,yPos,zPos;// render cubeMap position
	public float xRot,yRot,zRot;// render cubeMap Rotation;
	public int fbo;//frame buffer object
	public int depthBuffer;
	public final Matrix4f cubeMapProjectionMatrix;
	public CubeMap(List<Integer>textures,List<Integer>fbos,List<Integer>rbos) {
		cubeMapProjectionMatrix = new Matrix4f();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / ASPECT_RATIO;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		cubeMapProjectionMatrix.m00 = x_scale;
		cubeMapProjectionMatrix.m11 = y_scale;
		cubeMapProjectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		cubeMapProjectionMatrix.m23 = -1;
		cubeMapProjectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		cubeMapProjectionMatrix.m33 = 0;
		
		size = 128;
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, size, size);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);
		
		texture = GL11.glGenTextures();
		textures.add(texture);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		for (int i = 0; i < 6; i++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, WIDTH,HEIGHT);
	}
	
	public void switchFace(int face) {
		
		switch (face) {
		case 0:
			xRot = 0;//pitch
			yRot = -90;//yaw
			zRot =  -180;
			break;
		case 1:
			xRot = 0;//pitch
			yRot = 90;//yaw
			zRot =  -180;
			break;
		case 2:
			xRot = 90;//pitch
			yRot = -180;//yaw
			zRot =  -180;
			break;
		case 3:
			xRot = -90;//pitch
			yRot = -180;//yaw
			zRot =  -180;
			break;
		case 4:
			xRot = 0;//pitch
			yRot = -180;//yaw
			zRot =  -180;
			break;
		case 5:
			xRot = 0;//pitch
			yRot = 0;//yaw
			zRot =  -180;
			break;
		}
	}

	@Override
	public int getTexture() {
		return texture;
	}

}
