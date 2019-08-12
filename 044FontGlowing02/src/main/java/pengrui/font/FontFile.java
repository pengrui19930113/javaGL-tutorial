package pengrui.font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static pengrui.config.CommonConstant.SPACE_ASCII;

public class FontFile{
	
	private String file;//*.fnt
	
	public int paddingTop;
	public int paddingLeft;
	public int paddingBottom;
	public int paddingRight;
	public int lineHeight;//line height
	public int base;//字的基本大小
	public int scaleW;//image width
	public int scaleH;//image height
	
	// calculated variable
	public int spaceWidth;//' ' width
	public int maxCharWidth;
	//finally hold data
	public Map<Integer,Character> infos;
	
	private static final int UNKNOW = 127;//未知字符(没有图像)的ID
	public Character getCharacter(int id) {
		Character c = infos.get(id);
		if(null == c) {
			c = infos.get(UNKNOW);
		}
		return c;
	}
	
	public FontFile(String file) {
		this.file = file;
		infos = new HashMap<Integer,Character>();
		maxCharWidth = 0;
	}
	public FontFile parse(){
		Map<String,Map<String,String>> fontInfo = new HashMap<String,Map<String,String>>();
		List<String> charecterInfo = new LinkedList<String>();
		this.getDataToMemory(fontInfo,charecterInfo);
		this.parseDataToMemory(fontInfo,charecterInfo);
		this.calculateDataToMemory(fontInfo,charecterInfo);
		return this;
	}
	

	private void calculateDataToMemory(Map<String, Map<String, String>> fontInfo, List<String> charecterInfo) {
		for(String charLine:charecterInfo) {
			String[] parts = charLine.split("\\s+");
			//ascii x y width height xoffset yoffset xadvance
			Character info = new Character(
					Integer.valueOf(parts[1].split("=")[1])
					,Integer.valueOf(parts[2].split("=")[1])
					,Integer.valueOf(parts[3].split("=")[1])
					,Integer.valueOf(parts[4].split("=")[1])
					,Integer.valueOf(parts[5].split("=")[1])
					,Integer.valueOf(parts[6].split("=")[1])
					,Integer.valueOf(parts[7].split("=")[1])
					,Integer.valueOf(parts[8].split("=")[1])
					);
			if(SPACE_ASCII == info.id) {
				spaceWidth = info.xAdvance;
			}
			if(info.width>maxCharWidth) {
				maxCharWidth = info.width;
			}
			infos.put(info.id, info);
		}
	}
	
	private void parseDataToMemory(Map<String, Map<String, String>> fontInfo, List<String> charecterInfo) {
		getPadding(fontInfo);
		lineHeight = Integer.valueOf(fontInfo.get("common").get("lineHeight"));
		base = Integer.valueOf(fontInfo.get("common").get("base"));
		scaleW = Integer.valueOf(fontInfo.get("common").get("scaleW"));
		scaleH = Integer.valueOf(fontInfo.get("common").get("scaleH"));
	}
	
	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT =3;
	private void getPadding(Map<String, Map<String, String>> fontInfo) {
		String[] paddingStr = fontInfo.get("info").get("padding").split(",");
		paddingTop = Integer.valueOf(paddingStr[PAD_TOP]);
		paddingLeft = Integer.valueOf(paddingStr[PAD_LEFT]);
		paddingBottom = Integer.valueOf(paddingStr[PAD_BOTTOM]);
		paddingRight = Integer.valueOf(paddingStr[PAD_RIGHT]);
	}
	
	private void getDataToMemory(Map<String, Map<String, String>> fontInfo, List<String> charecterInfo) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(file)));
		try {
			String line;
			while(null!=(line = reader.readLine())) {
				String[] current = line.split(" ");
				if("char".equals(current[0])) {
					charecterInfo.add(line);
				}else {
					if(current.length>1) {
						String header = current[0];
						Map<String,String>info = new HashMap<String,String>();
						for(int i=1;i<current.length;i++) {
							String[] entry = current[i].split("=");
							if(2==entry.length) {
								info.put(entry[0], entry[1]);
							}
						}
						fontInfo.put(header, info);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public static void main(String[] args) {
		new FontFile("font/arial.fnt").parse();
	}
}