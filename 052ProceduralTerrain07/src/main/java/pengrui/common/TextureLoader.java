package pengrui.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class TextureLoader {
	static final String TEXTURE_FORMAT = "PNG";

	public static int createTexture(List<Integer> textures,String file) {
		return createTexture(textures,file,-0.8f);
	}
	
	public static int createTexture(List<Integer> textures,String file,float lodBias) {
		Texture texture = null;
		try {
			texture = org.newdawn.slick.opengl.TextureLoader.getTexture(TEXTURE_FORMAT,
					ClassLoader.getSystemResourceAsStream(file));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, lodBias);// level of detail bias
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("texture load fialure");
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	public static int createTexture2(List<Integer> textures,String file) {
		return createTexture2(textures,file,-0.8f);
	}
	public static int createTexture2(List<Integer> textures,String file,float lodBias) {
		int texture = GL11.glGenTextures();
		textures.add(texture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		TextureData data = decodeTextureFile(ClassLoader.getSystemResourceAsStream(file));
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, lodBias);// level of detail bias
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return texture;
	}
	
	public static int createCubeMap(List<Integer> textures,String[] files) {
		int texture = GL11.glGenTextures();
		textures.add(texture);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		for(int i=0;i<files.length;i++) {
			TextureData data = decodeTextureFile(ClassLoader.getSystemResourceAsStream("textures/skybox/"+files[i]+".png"));
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		return texture;
	}
	public static TextureData decodeTextureFile(InputStream is){
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try{
			PNGDecoder decoder = new PNGDecoder(is);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4*width*height);
//			buffer = ByteBuffer.allocate(4*width*height);
			decoder.decode(buffer, width*4, Format.RGBA);
//			decoder.decodeFlipped(buffer, width*4, Format.RGBA);// 上下颠倒
			buffer.flip();
			is.close();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
		return new TextureData(buffer,width,height);
	}
	private static class TextureData{
		private int width;
		private int height;
		private ByteBuffer buffer;
		public TextureData(ByteBuffer buffer,int width, int height){
			super();
			this.width = width;
			this.height = height;
			this.buffer = buffer;
		}
		public int getWidth() {
			return width;
		}
		public int getHeight() {
			return height;
		}
		public ByteBuffer getBuffer() {
			return buffer;
		}
	}
}
