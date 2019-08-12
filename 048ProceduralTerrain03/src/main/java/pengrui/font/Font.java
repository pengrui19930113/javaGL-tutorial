package pengrui.font;


public class Font {
	/**
	 * 加载的字体集纹理
	 */
	public int texture;
	
	public final FontFile fontFile;
	
	public Font(String file,int texture) {
		this.texture = texture;
		fontFile = new FontFile(file).parse();
	}
}
