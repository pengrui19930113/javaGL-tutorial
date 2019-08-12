package pengrui.terrain;

public interface IHeightGenerator {
	float generateHeight(int x,int z);
	
	public static IHeightGenerator getGenerator() {
		return new HeightGenerator4();
	}
}
