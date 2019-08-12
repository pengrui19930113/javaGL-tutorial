package animation;

public class Animations {
	
	private Animation[] animations;
	
	public Animations(Animation[] as) {
		if(null == as) 
			throw new NullPointerException();
		
		this.animations = as;
	}
	
	public Animation get(int index) {
		if(index>animations.length) 
			throw new ArrayIndexOutOfBoundsException();
		
		return animations[index];
	}
	
	public Animation getDefaultAnimation() {
		return this.get(0);
	}
}
