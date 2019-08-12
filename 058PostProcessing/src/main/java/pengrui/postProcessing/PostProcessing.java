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
	
	public PostProcessing(List<Integer>vaos,List<Integer>vbos,List<Integer> fbos,List<Integer> rbos,List<Integer>textures,List<Shaderable>shaders) {
		this.vao = PostProcessingVaoLoader.createPostProcessingVao(vaos, vbos);
		this.sceneFbo = new Fbo(fbos, rbos, textures, WIDTH, HEIGHT, Fbo.DEPTH_RENDER_BUFFER);
		
		this.contrastShader = ContrastShader.createShader(shaders);
	}
}
