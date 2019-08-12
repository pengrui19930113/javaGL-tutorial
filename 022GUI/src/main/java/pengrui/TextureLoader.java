package pengrui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;

public class TextureLoader {
	static final String TEXTURE_FORMAT = "PNG";

	public static int createTexture(List<Integer> textures,String file) {
		Texture texture = null;
		try {
			texture = org.newdawn.slick.opengl.TextureLoader.getTexture(TEXTURE_FORMAT,
					ClassLoader.getSystemResourceAsStream(file));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.8F);// level of detail bias
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("texture load fialure");
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
}
