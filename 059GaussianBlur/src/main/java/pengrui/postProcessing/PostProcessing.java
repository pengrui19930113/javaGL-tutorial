package pengrui.postProcessing;


import static pengrui.config.CommonConstant.HEIGHT;
import static pengrui.config.CommonConstant.WIDTH;

import java.util.List;

import pengrui.common.Shaderable;
import pengrui.common.VAO;

public class PostProcessing {
	
	public VAO vao;
	public Fbo sceneFbo;
	
	public ContrastShader contrastShader;
	
	public HorizontalBlurShader horizontalBlurShader;
	public Fbo horizontalFbo;
	
	public VerticalBlurShader verticalBlurShader;
	public Fbo verticalFbo;
	
	public PostProcessing(List<Integer>vaos,List<Integer>vbos,List<Integer> fbos,List<Integer> rbos,List<Integer>textures,List<Shaderable>shaders) {
		this.vao = PostProcessingVaoLoader.createPostProcessingVao(vaos, vbos);
		this.sceneFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.DEPTH_RENDER_BUFFER);

		this.contrastShader = ContrastShader.createShader(shaders);
		
		this.horizontalBlurShader = HorizontalBlurShader.createShader(shaders);
		horizontalBlurShader.loadTargetWidth(WIDTH);
		this.horizontalFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.NONE);
//		this.horizontalFbo = new Fbo(fbos, rbos, textures, WIDTH/5, HEIGHT/5, Fbo.NONE);
		
		this.verticalBlurShader = VerticalBlurShader.createShader(shaders);
		verticalBlurShader.loadTargetHeight(HEIGHT);
		this.verticalFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.NONE);
//		this.verticalFbo = new Fbo(fbos, rbos, textures, WIDTH/5, HEIGHT/5, Fbo.NONE);
		/**
		 * 除以5后更模糊了 但是出现了明显的锯齿 要解决这个问题 可以在 渲染的时候多次使用高斯模糊
		 * 例如 先通过 horizontalBlurShader 再通过 verticalBlurShader  再通过 horizontalBlurShader  再通过 verticalBlurShader 相当于使用了2次高斯模糊 可以使画面更模糊
		 */
	}
}
