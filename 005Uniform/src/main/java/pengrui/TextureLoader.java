package pengrui;

import java.io.IOException;
import java.util.List;

import org.newdawn.slick.opengl.Texture;

public class TextureLoader {
	static final String TEXTURE_FORMAT = "PNG";
	static final String TEXTURE_FILE = "textures/awesomeface.png";

	public static int createTexture(List<Integer> textures) {
		Texture texture = null;
		try {
			texture = org.newdawn.slick.opengl.TextureLoader.getTexture(TEXTURE_FORMAT,
					ClassLoader.getSystemResourceAsStream(TEXTURE_FILE));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("texture load fialure");
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
}
