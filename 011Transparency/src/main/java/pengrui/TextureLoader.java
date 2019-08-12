package pengrui;

import java.io.IOException;
import java.util.List;

import org.newdawn.slick.opengl.Texture;

public class TextureLoader {
	static final String TEXTURE_FORMAT = "PNG";

	public static int createTexture(List<Integer> textures,String file) {
		Texture texture = null;
		try {
			texture = org.newdawn.slick.opengl.TextureLoader.getTexture(TEXTURE_FORMAT,
					ClassLoader.getSystemResourceAsStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("texture load fialure");
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
}
