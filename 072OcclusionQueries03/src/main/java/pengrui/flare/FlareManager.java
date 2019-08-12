package pengrui.flare;

import java.util.List;

import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import pengrui.common.Camera;
import pengrui.common.Query;
import pengrui.common.Shaderable;
import pengrui.common.TextureLoader;
import pengrui.common.VAO;
import pengrui.util.MatrixUtil;

import static pengrui.config.CommonConstant.*;

public class FlareManager {
	public static final Vector2f CENTER_SCREEN = new Vector2f(0.f,0.F);// ndc screen center
	private static final int[] FLARE_TEXTURE_INDEX = {6,4,2,7,1,3,9,5,1,7,9,3,5,4,8};
	private static final float[] FLARE_TEXTURE_SCALE = {0.5f,0.23f,0.1f,0.05f,0.02f,0.06f,0.12f,0.07f,0.012f,0.2f,0.1F,0.07F,0.3F,0.4F,0.6F};
	private static final String[] FLARE_TEXTURE_PATH = {
			"textures/flare/tex1.png"
			,"textures/flare/tex2.png"
			,"textures/flare/tex3.png"
			,"textures/flare/tex4.png"
			,"textures/flare/tex5.png"
			,"textures/flare/tex6.png"
			,"textures/flare/tex7.png"
			,"textures/flare/tex8.png"
			,"textures/flare/tex9.png"
	};
	
	
	private static final float[] FLARE_POSITIONS = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };//使用NDC 坐标 所以 是逆时针
	public float spacing;// sun length 以屏幕中心到屏幕中太阳中心为一个单位长度， spacing 表示 多少个单位长度
	public VAO flareVao;
	public FlareShader flareShader;
	public FlareTexture[] flareTextures;
	
	private static final String SUN_TEXTURE_PATH = "textures/flare/sun.png";
	private static final float[] SUN_POSITIONS = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };// v0 v1 v2,v2 v1 v3,v2 v3 v4,v4 v3 v5 ...
	public VAO sunVao;
	public SunShader sunShader;
	public Sun sun;
	
	public static final float TEST_QUAD_WIDTH = 0.07f;
	public static final float TEST_QUAD_HEIGHT = TEST_QUAD_WIDTH/((float)WIDTH/HEIGHT);
	public Query query;
	
	public FlareManager(List<Integer>vaos,List<Integer>vbos,List<Integer>textures,List<Shaderable> shaders,List<Integer>queries,float spacing) {
		this.spacing = spacing;
		this.flareVao = QuadVaoLoader.loadQuadVao(vaos, vbos, FLARE_POSITIONS);
		this.flareShader = FlareShader.createShader(shaders);
		this.flareTextures = new FlareTexture[FLARE_TEXTURE_INDEX.length];
		int[] texs = new int[FLARE_TEXTURE_PATH.length];
		for(int i=0;i<FLARE_TEXTURE_PATH.length;i++) {
			int tex = TextureLoader.createTexture2(textures, FLARE_TEXTURE_PATH[i],0);
			textures.add(tex);
			texs[i] = tex;
		}
				
		for(int i=0;i<FLARE_TEXTURE_INDEX.length;i++) {
			FlareTexture flareTex = new FlareTexture();
			flareTex.scale = FLARE_TEXTURE_SCALE[i];
			flareTex.texture = texs[FLARE_TEXTURE_INDEX[i]-1];//注意-1 是因为索引 由 0-8 共9张图片
			flareTextures[i] = flareTex;
		}
		
		this.sunVao = QuadVaoLoader.loadQuadVao(vaos, vbos, SUN_POSITIONS);
		this.sunShader = SunShader.createShader(shaders);
		int sunTex = TextureLoader.createTexture(textures, SUN_TEXTURE_PATH);
		textures.add(sunTex);
		this.sun = new Sun(sunTex);
		
		this.query = new Query(queries, GL15.GL_SAMPLES_PASSED);
	}
	
	public float brightness;
	public Vector2f sunScreenNDC;
	public float coverage;
	public void update(Camera camera,long delta) {
		Vector3f sunWorldPos = sun.getWorldPosition(camera);
		sunScreenNDC = calculateSunScreenPosition(sunWorldPos,camera);
		if(null == sunScreenNDC) {
			return;
		} 
		
		Vector2f sunToCenter = Vector2f.sub(CENTER_SCREEN, sunScreenNDC, null);
		brightness = 1-(sunToCenter.length()/0.7f);
		if(brightness <= 0 ) {
			return;
		}
		calcFlarePositions(sunToCenter,sunScreenNDC);
	}
	
	public void calcFlarePositions(Vector2f sunToCenter,Vector2f sunScreenNDC) {
		for(int i=0;i<flareTextures.length;i++) {
			Vector2f direction = new Vector2f(sunToCenter);
			direction.scale(i*spacing);
			Vector2f flarePos = Vector2f.add(sunScreenNDC, direction, null);
			flareTextures[i].xPos = flarePos.x;
			flareTextures[i].yPos = flarePos.y;
		}
	}
	
	public Vector2f calculateSunScreenPosition(Vector3f sunWorldPos, Camera camera) {
		Vector4f coords = new Vector4f(sunWorldPos.x,sunWorldPos.y,sunWorldPos.z,1);
		Matrix4f.transform(MatrixUtil.calculateViewMatrix(camera), coords, coords);
		Matrix4f.transform(MatrixUtil.createProjectionMatrix(), coords, coords);
		if(coords.w <=0)
			return null;
		
		float x = (coords.x/coords.w);// 透视除法 和坐标变换 到窗口左上角 
		float y = (coords.y/coords.w);
		return new Vector2f(x,y);
	}
}
