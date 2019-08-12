package pengrui.font;

public class Character {
	public int id;//asiic code
	public int x;//字符左上角在纹理集合原点(左上角 左手系)的横向偏移量
	public int y;//字符左上角在纹理集合原点(左上角 左手系)的纵向偏移量
	public int width;//字符所占宽度
	public int height;//字符所占高度
	public int xOffset;//字符相对于基线和游标的横向偏移(向右)
	public int yOffset;//字符相对于基线和游标的纵向偏移(向上)
	public int xAdvance;//绘制完一个字符后下一个字符的横向起始点需要 的偏移位置(向右)
	
	public Character(int id,int x,int y,int width,int height,int xOffset, int yOffset, int xAdvance) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xAdvance = xAdvance;
	}
	
	@Override
	public String toString() {
		return String.format("assic:%s x:%s y:%s width:%s height:%s xOffset:%s yOffset:%s xAdvance:%s", id,x,y,width,height,xOffset,yOffset,xAdvance);
	}
}
