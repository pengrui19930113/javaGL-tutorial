package pengrui.font;

import java.util.ArrayList;
import java.util.List;

import pengrui.config.CommonConstant;

public class Word {
	public List<Character> characters;
	
	public Word() {
		characters = new ArrayList<Character>();
	}
	
	public int getPixelLength(Font font) {
		int length = 0;
		for(Character c:characters) {
			length += CommonConstant.SPACE_ASCII == c.id?c.xAdvance-(font.fontFile.paddingLeft+font.fontFile.paddingRight): c.width-(font.fontFile.paddingLeft+font.fontFile.paddingRight)+c.xOffset;
		}
		return length;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Character c:characters) {
			builder.append((char)c.id);
		}
		return builder.toString();
	}
}
