package pengrui.postProcessing;


import static pengrui.config.CommonConstant.HEIGHT;
import static pengrui.config.CommonConstant.WIDTH;

import java.util.List;

import pengrui.common.Shaderable;
import pengrui.common.VAO;
import pengrui.postProcessing.bloom.BrightFilterShader;
import pengrui.postProcessing.bloom.CombineFilterShader;
import pengrui.postProcessing.gaussion.HorizontalBlurShader;
import pengrui.postProcessing.gaussion.VerticalBlurShader;

public class PostProcessing {
	
	public VAO vao;
	public Fbo sceneFbo;
	public Fbo sceneTexturedAttachmentFbo;
	public ContrastShader contrastShader;
	
	public HorizontalBlurShader horizontalBlurShader;
	public Fbo horizontalFbo;
	
	public VerticalBlurShader verticalBlurShader;
	public Fbo verticalFbo;
	
	public BrightFilterShader brightFilterShader;
	public Fbo brightFbo;
	
	public CombineFilterShader combineFilterShader;
	
	public Fbo testMultiTargetRenderFbo;
	
	public PostProcessing(List<Integer>vaos,List<Integer>vbos,List<Integer> fbos,List<Integer> rbos,List<Integer>textures,List<Shaderable>shaders) {
		this.vao = PostProcessingVaoLoader.createPostProcessingVao(vaos, vbos);
		this.sceneFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.DEPTH_RENDER_BUFFER,true,true);// 使用 multisample fbo
		this.sceneTexturedAttachmentFbo =  new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.DEPTH_RENDER_BUFFER);
		
		this.contrastShader = ContrastShader.createShader(shaders);
		
		this.horizontalBlurShader = HorizontalBlurShader.createShader(shaders);
		horizontalBlurShader.loadTargetWidth(WIDTH);
		this.horizontalFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.NONE);
		
		this.verticalBlurShader = VerticalBlurShader.createShader(shaders);
		verticalBlurShader.loadTargetHeight(HEIGHT);
		this.verticalFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.NONE);
		
		this.brightFilterShader = BrightFilterShader.createShader(shaders);
		this.brightFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.NONE);
		
		this.combineFilterShader = CombineFilterShader.createShader(shaders);
		
		this.testMultiTargetRenderFbo = new Fbo(fbos,rbos,textures,WIDTH,HEIGHT,Fbo.DEPTH_TEXTURE);
	}
}
