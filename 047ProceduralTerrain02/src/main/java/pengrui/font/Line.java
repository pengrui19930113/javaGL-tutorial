package pengrui.font;

import java.util.ArrayList;
import java.util.List;


public class Line {
	public List<Word> words;
	public Line() {
		words = new ArrayList<Word>();
	}
	
	public int getPixelLength(Font font) {
		int appendSpaceWidthPixel = (font.fontFile.spaceWidth - font.fontFile.paddingLeft - font.fontFile.paddingRight);
		int length = 0;
		for(Word word:words) {
			length+=word.getPixelLength(font)+appendSpaceWidthPixel;
//			for(Character c:word.characters) {
//				length += CommonConstant.SPACE_ASCII == c.id?c.xAdvance: c.width+font.fontFile.paddingLeft+font.fontFile.paddingRight;

//			}
		}
		length -= appendSpaceWidthPixel;
		return length;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Word word:words) {
			builder.append(word).append(" ");
		}
		return builder.toString();
	}
}
