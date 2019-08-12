package pengrui.font;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import pengrui.common.VAO;

import static pengrui.config.CommonConstant.SPACE_ASCII;

public class StaticText {

	public static final int VERTICES_INDEX = 0;
	public static final int TEXTURE_COODINATION_INDEX = 1;
	
	public VAO vao;
	public float red,green,blue;
	public float xPos,yPos;
	
	public String text;
	public int lineMaxPixel;
	public boolean isCenter;
	public double ndcLineHight;
	public double expectFontWidthHightRatio;
	public float fontSize;
	
	public Font font;
	
	//by calculate automatic set
	public int numberOfLines;
	
	//control variable
	private boolean canBeUsed;
	//self managed variable
	public int[] selfManagedVbos;
	
	public StaticText(float xPos,float yPos,String text,float red,float green,float blue,int lineMaxPixel,boolean isCenter,Font font,float fontSize,double ndcLineHight,double fontWHRatio) {
		canBeUsed = true;
		selfManagedVbos = new int[2];
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.text = text;
		this.red = red;
		this.green = green;
		this.blue = blue;
		
		this.font = font;
		this.lineMaxPixel = lineMaxPixel;
		this.isCenter = isCenter;
		this.fontSize = fontSize;
		this.ndcLineHight = ndcLineHight;
		this.expectFontWidthHightRatio = fontWHRatio;
		try {
			checkMemberVariable();
		}catch(RuntimeException e) {
			this.lineMaxPixel = font.fontFile.maxCharWidth+font.fontFile.paddingLeft+font.fontFile.paddingRight;
		}
		checkMemberVariable();
		this.vao = genVao();
	}
	public StaticText(float xPos,float yPos,String text,int lineMaxPixel,Font font) {
		this(xPos, yPos, text, 0.5f, 0.5f, 0.5f, lineMaxPixel, false,font,5,0.03,1);
	}
	
	private void checkMemberVariable() {
		if(null == font||lineMaxPixel<(font.fontFile.maxCharWidth+font.fontFile.paddingLeft+font.fontFile.paddingRight) 
				||null == text||expectFontWidthHightRatio<0|| ndcLineHight <0 || fontSize<0)
			throw new RuntimeException("invalid menber variable");
	}
	
	private VAO genVao() {
		List<Line> lines = analyze();
		numberOfLines = lines.size();
		List<Float> vertices = new ArrayList<Float>();
		List<Float> textureCoords = new ArrayList<Float>();
		calculate(vertices,textureCoords,lines);
		float[] verticesArray = new float[vertices.size()];
		for(int i=0;i<vertices.size();i++)
			verticesArray[i] = vertices.get(i);
		float[] textureCoordsArray = new float[textureCoords.size()];
		for(int i=0;i<textureCoords.size();i++)
			textureCoordsArray[i] = textureCoords.get(i); 
		return StaticTextLoader.loadVao(verticesArray,textureCoordsArray,this);
	}
	
	private void calculate(List<Float> vertices, List<Float> textureCoords, List<Line> lines) {
		int textPixelHeight = numberOfLines*font.fontFile.lineHeight;
		int spacePixel = font.fontFile.spaceWidth-font.fontFile.paddingLeft-font.fontFile.paddingRight;
		int widthPaddingPixel =  font.fontFile.paddingLeft+font.fontFile.paddingRight;
		int xPixelCurser = 0;
		int yPixelCurser = textPixelHeight;
		yPixelCurser -= font.fontFile.lineHeight;
		List<Integer> pixelVertices = new ArrayList<Integer>();
		List<Integer> pixelTextureCoords = new ArrayList<Integer>();
		for(Line line:lines) {
			if(isCenter) {
				xPixelCurser = (lineMaxPixel-line.getPixelLength(font))/2;
			}else {
				xPixelCurser = 0;
			}
			for(Word word:line.words) {
				for(Character letter:word.characters) {
					if(SPACE_ASCII != letter.id) {
						addPixelVerticesFromCharacter(xPixelCurser,yPixelCurser,letter,pixelVertices);
						addPixelTextureCoordination(letter,pixelTextureCoords);
					}
					xPixelCurser += letter.xAdvance - widthPaddingPixel;
				}
				xPixelCurser += spacePixel;//每个单词需要一个空格
			}
			yPixelCurser -= font.fontFile.lineHeight;
		}
		transform(pixelVertices,pixelTextureCoords,vertices,textureCoords);
	}
	
	/**
	 * 	 	1——————4
	 * 		|	   |
	 * 		2——————3
	 * 两个三角形的定义 :  1->2->3		3->4->1
	 * @param letter
	 * @param pixelTextureCoords
	 */
	private void addPixelTextureCoordination(Character letter, List<Integer> pixelTextureCoords) {
		
		int textureLeftTop_x = letter.x;
		int textureLeftTop_y = letter.y;
//		int textureRightBottom_x = textureLeftTop_x+letter.width-(font.fontFile.paddingRight); //由于 alpha 通道使用的是一个模糊效果 纹理如果过于贴近则边缘效果欠佳 可以查看资源下的图片对比
//		int textureRightBottom_y = textureLeftTop_y+letter.height-(font.fontFile.paddingBottom);
		int textureRightBottom_x = textureLeftTop_x+letter.width;
		int textureRightBottom_y = textureLeftTop_y+letter.height;
		
		int textureLeftBottom_x = textureLeftTop_x;
		int textureLeftBottom_y = textureRightBottom_y;
		int textureRightTop_x = textureRightBottom_x;
		int textureRightTop_y = textureLeftTop_y;		
		
		pixelTextureCoords.add(textureLeftTop_x);
		pixelTextureCoords.add(textureLeftTop_y);
		pixelTextureCoords.add(textureLeftBottom_x);
		pixelTextureCoords.add(textureLeftBottom_y);
		pixelTextureCoords.add(textureRightBottom_x);
		pixelTextureCoords.add(textureRightBottom_y);
		pixelTextureCoords.add(textureRightBottom_x);
		pixelTextureCoords.add(textureRightBottom_y);
		pixelTextureCoords.add(textureRightTop_x);
		pixelTextureCoords.add(textureRightTop_y);
		pixelTextureCoords.add(textureLeftTop_x);
		pixelTextureCoords.add(textureLeftTop_y);
		
	}
	/**
	 * 	 	1——————4
	 * 		|	   |
	 * 		2——————3
	 * 两个三角形的定义 :  1->2->3		3->4->1
	 */
	private void addPixelVerticesFromCharacter(int xPixelCurser, int yPixelCurser, Character letter, List<Integer> pixelVertices) {
		// 当前游标指向的是一行的左下角 而绘制图片的参考点再左上角 +上一行后参考点 变为上一行的左下角 也就是当前行的左上角
		int referPosition_x = xPixelCurser;
		int referPosition_y = yPixelCurser+font.fontFile.lineHeight;
		
		int vertexLeftTop_x = referPosition_x + letter.xOffset;//向右偏移xOffset 就是绘制该字符的横坐标
		int vertexLeftTop_y = referPosition_y - letter.yOffset;//向下偏移yOffset 就是绘制该字符的纵坐标
		int vertexRightBottom_x = vertexLeftTop_x + letter.width;
		int vertexRightBottom_y = vertexLeftTop_y - letter.height;
		int vertexLeftBottom_x = vertexLeftTop_x;
		int vertexLeftBottom_y = vertexRightBottom_y;
		int vertexRightTop_x = vertexRightBottom_x;
		int vertexRightTop_y = vertexLeftTop_y;
		
		pixelVertices.add(vertexLeftTop_x);//顶点顺序和纹理顺序一致
		pixelVertices.add(vertexLeftTop_y);
		pixelVertices.add(vertexLeftBottom_x);
		pixelVertices.add(vertexLeftBottom_y);
		pixelVertices.add(vertexRightBottom_x);
		pixelVertices.add(vertexRightBottom_y);
		pixelVertices.add(vertexRightBottom_x);
		pixelVertices.add(vertexRightBottom_y);
		pixelVertices.add(vertexRightTop_x);
		pixelVertices.add(vertexRightTop_y);
		pixelVertices.add(vertexLeftTop_x);
		pixelVertices.add(vertexLeftTop_y);
	}
	
	
	/**
	 * 假设期望显示宽高比是 4:3
	 * 字体描述文件的 行高 是 80个像素，标准设备空间(NDC)的行高是0.02
	 * 如果一个字符的高是100个像素	
	 * 则 对应的  80:0.02 = 100:ndc.h  得到 ndc.h = 100/(80/0.02) = 0.025
	 * 	ndc.w/ndc.h = 4:3 得到 ndc.w = 0.025*4/3 = 1/30 = 0.03333;
	 * @param pixelVertices
	 * @param pixelTextureCoords
	 * @param vertices
	 * @param textureCoords
	 */
	private void transform(List<Integer> pixelVertices, List<Integer> pixelTextureCoords, List<Float> vertices,
			List<Float> textureCoords) {
		if(pixelVertices.size()!=pixelTextureCoords.size())
			throw new RuntimeException("parse error");
		double pixelLineHightNdcLineHightRadio = ((double) font.fontFile.lineHeight)/ndcLineHight;// 相当于 (80/0.02)
		double widthRadio = pixelLineHightNdcLineHightRadio*expectFontWidthHightRatio;
		
		for(int i=0;i<pixelVertices.size();i++) {
			double vertexValue = pixelVertices.get(i)*fontSize;
			double textureCoordValue = pixelTextureCoords.get(i);
			if(0 == i%2) {
				vertices.add((float)(vertexValue/pixelLineHightNdcLineHightRadio));// 相当于ndc.h = 100/(80/0.02)
				textureCoords.add((float)textureCoordValue/font.fontFile.scaleW);
			}else {
				vertices.add((float)(vertexValue/widthRadio));// 相当于 ndc.w = ndc.h*4:3
				textureCoords.add((float)textureCoordValue/font.fontFile.scaleH);
			}
		}
	}
	
	private List<Line> analyze() {
		List<Line> lines = new ArrayList<Line>();
		char[] chars = text.toCharArray();
		Line curLine = new Line();
		Word curWord = new Word();
		LABEL1:
		for(char c:chars) {
			int ascii = (int)c;
			Character curChar = font.fontFile.getCharacter(ascii);
			if(SPACE_ASCII == ascii) {// 表示一个单词已经结束 或者连续多个空格
				if( curWord.characters.isEmpty()//当前单词是空的但是仍然下一个字符仍然是空格
						||SPACE_ASCII == curWord.characters.get(curWord.characters.size()-1).id// 或当前单词的最后一个字符串是空格
						) {//连续多个空格
					curWord.characters.add(curChar);
				}else {//一个单词已经结束
					if(curWord.getPixelLength(font) >= lineMaxPixel) {//如果一个单词的长度就大于一行允许的最大像素长度 则补齐当前行的字符为一整行 剩余的拆分为多行
						LABEL2:
						for(;;) {
							int canBeUsedLength = lineMaxPixel-curLine.getPixelLength(font);
							Word appendCurLineWord = new Word();
							int boundIndex = 0;
							LABEL3:
							for(int i=0;i<curWord.characters.size();i++) {
								boundIndex = i;
								if(appendCurLineWord.getPixelLength(font)
										+curWord.characters.get(i).width
										-font.fontFile.paddingLeft
										-font.fontFile.paddingRight 
										<= canBeUsedLength) {//查看当前单词前几个字母可以填充到当前行
									appendCurLineWord.characters.add(curWord.characters.get(i));
								}else {
									break LABEL3;
								}
							}
							curLine.words.add(appendCurLineWord);
							if(boundIndex == curWord.characters.size()-1) {
								curWord = new Word();
								break LABEL2;
//								continue LABEL1;
							}else {
								lines.add(curLine);
								curLine = new Line();
								Word used = curWord;
								curWord = new Word();
								if(boundIndex-1 >= 0
										&&boundIndex<used.characters.size()//可能这个判断不需要
										&&SPACE_ASCII != used.characters.get(boundIndex-1).id
										&& SPACE_ASCII != used.characters.get(boundIndex).id
										) {//如果上一个单词(本质是上一行)的最后一个字符不是空格 ，并且换行后第一个字符也不是空格 则换行的时候新增加一个'-'字符 以表示上下两行是同一个单词分割为了两部分
									curWord.characters.add(font.fontFile.getCharacter((int)'-'));
								}
								for(;boundIndex<used.characters.size();boundIndex++) {
									curWord.characters.add(used.characters.get(boundIndex));
								}	
							}
						}
					}else {//如果一个单词 长度小于一行允许的最大像素长度
						if((curWord.getPixelLength(font)+curLine.getPixelLength(font)) > lineMaxPixel) {// 如果当前行的长度  + 当前单词长度 超过了行允许的最大长度
							lines.add(curLine);//换行
							curLine = new Line();
							curLine.words.add(curWord);//由于一行容得下整个单词 所以直接添加
							curWord = new Word();
						}else {// 如果当前行的长度允许放下当前单词
							curLine.words.add(curWord);
							curWord = new Word();
						}
					}
				}
				continue LABEL1;
			}else {
				curWord.characters.add(curChar);
			}
		}
		//最后一次处理
		if(curWord.getPixelLength(font) >= lineMaxPixel) {
			for(;;) {
				int indexBound = 0;
				int canBeUsedLength = lineMaxPixel-curLine.getPixelLength(font);
				int lengthOfBetweenZeroAndIndexBound = 0;
				for(int i=0;i<curWord.characters.size();i++) {
					indexBound = i;
					int charLength = curWord.characters.get(i).width-font.fontFile.paddingLeft-font.fontFile.paddingRight;
					if(lengthOfBetweenZeroAndIndexBound+charLength <= canBeUsedLength) {
						lengthOfBetweenZeroAndIndexBound+=charLength;
					}else {
						break;
					}
				}
				if(indexBound == curWord.characters.size()-1) {//刚好读完
					curLine.words.add(curWord);
					lines.add(curLine);
					break;
				}else {
					Word appendWord = new Word();
					Word remainningWord = new Word();
					if(indexBound-1 >= 0
						&&indexBound<curWord.characters.size()
						&&SPACE_ASCII != curWord.characters.get(indexBound-1).id
						&& SPACE_ASCII != curWord.characters.get(indexBound).id
						) {
						remainningWord.characters.add(font.fontFile.getCharacter((int)'-'));
					}
					for(int i=0;i<curWord.characters.size();i++) {
						if(i<indexBound) {
							appendWord.characters.add(curWord.characters.get(i));
						}else {
							remainningWord.characters.add(curWord.characters.get(i));
						}
					}
					curLine.words.add(appendWord);
					lines.add(curLine);
					curLine = new Line();
					curWord = remainningWord;
				}
			}
		}else{
			if(curWord.getPixelLength(font)+curLine.getPixelLength(font) > lineMaxPixel) {
				lines.add(curLine);
				curLine = new Line();
				curLine.words.add(curWord);
				lines.add(curLine);
			}else {
				curLine.words.add(curWord);
				lines.add(curLine);
			}
		}
		return lines;
	}

	/**
	 * 如果改变了对象的某个属性可能需要重新计算顶点和纹理
	 */
	public void modify() {
		if(!canBeUsed)
			throw new RuntimeException("vao was destroyed");
		
		//后续实现
		throw new UnsupportedOperationException();
	}
	
	public void destroy() {
		GL30.glDeleteVertexArrays(vao.vaoid);
		for(int vboid:selfManagedVbos) {
			GL15.glDeleteBuffers(vboid);
		}
		canBeUsed = false;
	}
	
}
