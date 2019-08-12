package pengrui.manager;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import pengrui.common.Camera;
import pengrui.common.Light;
import pengrui.entity.Entity;
import pengrui.entity.EntityShader;
import pengrui.font.FontEffect;
import pengrui.font.StaticText;
import pengrui.font.StaticTextShader;
import pengrui.gui.GuiEntity;
import pengrui.gui.GuiEntityShader;
import pengrui.normalMapping.NormalMappingEntity;
import pengrui.normalMapping.NormalMappingEntityShader;
import pengrui.player.Player;
import pengrui.player.PlayerShader;
import pengrui.postProcessing.PostProcessing;
import pengrui.skybox.Skybox;
import pengrui.skybox.SkyboxShader;
import pengrui.terrain.Terrain;
import pengrui.terrain.TerrainShader;
import pengrui.util.MatrixUtil;

import static pengrui.config.CommonConstant.*;
public class DrawManager {
	
	public static void draw(EntityShader endityShader,List<Entity> entities,Camera camera,Light light
			, TerrainShader terrainShader, Terrain terrain
			, PlayerShader playerShader, Player player
			, GuiEntityShader guiShader, List<GuiEntity> guis
			, SkyboxShader skyboxShader, Skybox skybox
			, NormalMappingEntityShader normalMappingEntityShader, List<NormalMappingEntity> normalEntitys
			, StaticTextShader textShader, List<StaticText> texts, FontEffect fontEffect
			, PostProcessing postProcessing
			) {
		
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height);
			drawBegin();
				drawTerrain(terrainShader, camera, light, terrain);
				drawPlayer(playerShader,camera,light,player);
				drawEntity(endityShader, camera, light, entities);
				drawNormalMappingEntity(normalMappingEntityShader,camera,light,normalEntitys);
				drawSkyBox(skyboxShader,camera, light,skybox);
			drawEnd();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);		
		
//		drawProcessingQuad(postProcessing);
//		drawProcessingQuad2(postProcessing);
		
//		testDrawProcessingToScreen(postProcessing);
		drawProcessingQuad3(postProcessing);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);	
		drawTexts(textShader,texts,fontEffect);
		drawGuiEntity(guiShader,guis);
	}

	/**
	 * 使用glBlitFramebuffer 直接渲染到屏幕
	 * @param postProcessing
	 */
	static void drawProcessingQuad3(PostProcessing postProcessing) {
		
		//attachment0
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.multiTargetFbo0.framebuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL30.glBlitFramebuffer(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height
				, 0, 0, postProcessing.multiTargetFbo0.width, postProcessing.multiTargetFbo0.height
				, GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT
				, GL11.GL_NEAREST);
		
		//attachment1
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT1);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.multiTargetFbo1.framebuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL30.glBlitFramebuffer(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height
				, 0, 0, postProcessing.multiTargetFbo1.width, postProcessing.multiTargetFbo1.height
				, GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT
				, GL11.GL_NEAREST);
		
		GL30.glBindVertexArray(postProcessing.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL20.glUseProgram(postProcessing.horizontalBlurShader.programID);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.multiTargetFbo1.colourTexture);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.horizontalFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.horizontalFbo.width, postProcessing.horizontalFbo.height);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);

		GL20.glUseProgram(postProcessing.verticalBlurShader.programID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.horizontalFbo.colourTexture);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.verticalFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.verticalFbo.width, postProcessing.verticalFbo.height);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);
			
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);	
			
		GL20.glUseProgram(postProcessing.combineFilterShader.programID);	
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.multiTargetFbo0.colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);	
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.verticalFbo.colourTexture);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);

		GL20.glUseProgram(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	static void testDrawProcessingToScreen(PostProcessing postProcessing) {
		//attachment0
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBlitFramebuffer(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height
				, 0, 0, WIDTH,HEIGHT
				, GL11.GL_COLOR_BUFFER_BIT
				, GL11.GL_NEAREST);
		
		//attachment1
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT1);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.multiTargetFbo1.framebuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL30.glBlitFramebuffer(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height
		, 0, 0, postProcessing.multiTargetFbo1.width, postProcessing.multiTargetFbo1.height
		, GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT
		, GL11.GL_NEAREST);
	}
	
	static void drawProcessingQuad2(PostProcessing postProcessing) {
		
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.sceneTexturedAttachmentFbo.framebuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL30.glBlitFramebuffer(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height
				, 0, 0, postProcessing.sceneTexturedAttachmentFbo.width, postProcessing.sceneTexturedAttachmentFbo.height
				, GL11.GL_COLOR_BUFFER_BIT 
				, GL11.GL_NEAREST);
		
		GL30.glBindVertexArray(postProcessing.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL20.glUseProgram(postProcessing.brightFilterShader.programID);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.sceneTexturedAttachmentFbo.colourTexture);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.brightFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.brightFbo.width, postProcessing.brightFbo.height);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);
			
		GL20.glUseProgram(postProcessing.horizontalBlurShader.programID);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.brightFbo.colourTexture);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.horizontalFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.horizontalFbo.width, postProcessing.horizontalFbo.height);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);

		GL20.glUseProgram(postProcessing.verticalBlurShader.programID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.horizontalFbo.colourTexture);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.verticalFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.verticalFbo.width, postProcessing.verticalFbo.height);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);
			
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);	
			
		GL20.glUseProgram(postProcessing.combineFilterShader.programID);	
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.sceneTexturedAttachmentFbo.colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);	
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.verticalFbo.colourTexture);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);

		GL20.glUseProgram(0);
//		GL13.glActiveTexture(GL13.GL_TEXTURE1);	
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	static void drawProcessingQuad(PostProcessing postProcessing) {
		GL30.glBindVertexArray(postProcessing.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		//fbo1.renderbufferAttachment 拷贝到 fbo2.textureAttachment
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.sceneTexturedAttachmentFbo.framebuffer);//指定写向的帧缓冲区
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);//指定读取的帧缓冲区
		GL30.glBlitFramebuffer(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height
				, 0, 0, postProcessing.sceneTexturedAttachmentFbo.width, postProcessing.sceneTexturedAttachmentFbo.height
				, GL11.GL_COLOR_BUFFER_BIT //|GL11.GL_DEPTH_BUFFER_BIT|GL11.GL_STENCIL_BUFFER_BIT// 也可以 写深度 或者 模板 缓冲区 但本例没有使用
				, GL11.GL_NEAREST);//由于是多重采样向单采样缓冲区写  所以只需要 临近采样即可
		
//		//将 多重采样FBO直接渲染到屏幕
//		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
//		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, postProcessing.sceneFbo.framebuffer);
//		GL11.glDrawBuffer(GL11.GL_BACK);//指定渲染到 双缓冲BACK缓冲 也就是下一帧要显示的缓冲
//		GL30.glBlitFramebuffer(0, 0, postProcessing.sceneFbo.width, postProcessing.sceneFbo.height
//				, 0, 0, WIDTH, HEIGHT, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		
		//注意此时的 sceneFbo.colourTexture 不是sceneFbo的颜色附件
		GL20.glUseProgram(postProcessing.horizontalBlurShader.programID);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.sceneTexturedAttachmentFbo.colourTexture);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.horizontalFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.horizontalFbo.width, postProcessing.horizontalFbo.height);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);
//		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);//
//		GL11.glViewport(0, 0, WIDTH, WIDTH);//
//		GL20.glUseProgram(0);//
		GL20.glUseProgram(postProcessing.verticalBlurShader.programID);
//		GL13.glActiveTexture(GL13.GL_TEXTURE0);//
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.horizontalFbo.colourTexture);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessing.verticalFbo.framebuffer);
		GL11.glViewport(0, 0, postProcessing.verticalFbo.width, postProcessing.verticalFbo.height);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
//		GL20.glUseProgram(0);//
		
		GL20.glUseProgram(postProcessing.contrastShader.programID);
//		GL13.glActiveTexture(GL13.GL_TEXTURE0);//
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, postProcessing.verticalFbo.colourTexture);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, postProcessing.vao.vertexCount);
		GL20.glUseProgram(0);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	static void drawTexts(StaticTextShader shader, List<StaticText> texts, FontEffect fontEffect) {
		if(null == shader || null == texts || texts.isEmpty()|| null == fontEffect) return;
//		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL20.glUseProgram(shader.programID);
		GL20.glUniform1f(shader.location_width,fontEffect.width);
		GL20.glUniform1f(shader.location_edge, fontEffect.edge);
		GL20.glUniform1f(shader.location_borderWidth, fontEffect.borderWidth);
		GL20.glUniform1f(shader.location_borderEdge, fontEffect.borderEdge);
		GL20.glUniform2f(shader.location_offset, fontEffect.xOffset,fontEffect.yOffset);
		GL20.glUniform3f(shader.location_outlineColour, fontEffect.redOutlineColour,fontEffect.greenOutlineColour,fontEffect.blueOutlineColour);
		for(StaticText text:texts) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, text.font.texture);
			GL30.glBindVertexArray(text.vao.vaoid);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glUniform3f(shader.location_colour, text.red, text.green, text.blue);
			GL20.glUniform2f(shader.location_translation,text.xPos,text.yPos);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.vao.vertexCount);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	static void drawNormalMappingEntity(NormalMappingEntityShader shader, Camera camera,
			Light light, List<NormalMappingEntity> normalEntitys) {
		if(null == normalEntitys) return;
		GL20.glUseProgram(shader.programID);
		for(NormalMappingEntity entity:normalEntitys) {
			GL30.glBindVertexArray(entity.vao.vaoid);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.calculateTransformMatrix(entity));
			GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.calculateViewMatrix(camera));
			GL20.glUniform1f(shader.location_shineDamper, entity.shineDamper);
			GL20.glUniform1f(shader.location_reflectivity, entity.reflectivity);
			GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
			GL20.glUniform3f(shader.location_lightColour, light.red, light.green, light.blue);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.texture);
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.normalMap);
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.vao.vertexCount,GL11.GL_UNSIGNED_INT,0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL20.glDisableVertexAttribArray(3);
			GL30.glBindVertexArray(0);
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL20.glUseProgram(0);
	}

	static void drawSkyBox(SkyboxShader shader, Camera camera, Light light, Skybox skybox) {
		GL20.glUseProgram(shader.programID);
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.calculateSkyboxViewMatrix(camera));
		GL30.glBindVertexArray(skybox.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox.texture);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, skybox.vao.vertexCount);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	static void drawGuiEntity(GuiEntityShader shader, List<GuiEntity> guis) {
		if(null == guis) return;
		GL20.glUseProgram(shader.programID);
		GL30.glBindVertexArray(GuiEntity.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for(GuiEntity gui:guis) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.texture);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.calculateTransformMatrix(gui));
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, GuiEntity.vao.vertexCount);
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,0);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL20.glUseProgram(0);
	}

	private static void drawBegin() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT
//				|GL11.GL_STENCIL_BUFFER_BIT
				);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
//		GL11.glCullFace(GL11.GL_FRONT);
		GL11.glCullFace(GL11.GL_BACK);
//		GL11.glFrontFace(GL11.GL_CW);
		GL11.glFrontFace(GL11.GL_CCW);//默认 逆时针
	}
	
	private static void drawEnd() {
		//nothing
	}
	
	static void drawTerrain(TerrainShader shader,Camera camera,Light light,Terrain terrain) {
		GL20.glUseProgram(shader.programID);
		GL30.glBindVertexArray(terrain.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.calculateTransformMatrix(terrain));
		GL20.glUniformMatrix4(shader.location_viewMatrix,false,MatrixUtil.calculateViewMatrix(camera));
		GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
		GL20.glUniform3f(shader.location_lightColour, light.red, light.green, light.blue);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.texture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.rTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.gTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.bTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.blendMap);
		
//		GL11.glDisable(GL11.GL_CULL_FACE);
//		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glFrontFace(GL11.GL_CCW);//默认 逆时针
		GL11.glCullFace(GL11.GL_BACK);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES,terrain.vao.vertexCount,GL11.GL_UNSIGNED_INT,0);
		
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	
	static void drawEntity(EntityShader shader,Camera camera,Light light,List<Entity> entities) {
		if(null == entities || entities.isEmpty())	return;
		//indicate shader program
		GL20.glUseProgram(shader.programID);
		for(Entity entity:entities) {
			//bind vao
			GL30.glBindVertexArray(entity.vao.vaoid);
			//enable vertex attribute
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			//bind texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);// indicate the texture unit
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.texture);
			if(entity.useFakeLighting) {
				GL20.glUniform1f(shader.location_useFakeLighting, 1f);
			}else {
				GL20.glUniform1f(shader.location_useFakeLighting, .0f);
			}
			if(entity.hasTransparency) {
				GL11.glDisable(GL11.GL_CULL_FACE);
			}else {
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
			//load uniform matrix
			FloatBuffer transformBuffer = MatrixUtil.calculateTransformMatrixBuffer(entity);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, transformBuffer);
			FloatBuffer viewBuffer = MatrixUtil.calculateViewMatrix(camera);
			GL20.glUniformMatrix4(shader.location_viewMatrix, false, viewBuffer);
			GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
			GL20.glUniform3f(shader.location_lightColour,light.red,light.green,light.blue);
			GL20.glUniform1f(shader.location_shineDamper,entity.shineDamper); //高光参数
			GL20.glUniform1f(shader.location_reflectivity,entity.reflectivity);
			GL20.glUniform2f(shader.location_offset, entity.getTextureXOffset(), entity.getTextureYOffset());//纹理集参数
			GL20.glUniform1f(shader.location_numberOfRows, entity.numberOfRows);
			GL20.glUniform3f(shader.location_attenuation, light.constantAttenuation, light.squareAttenuation, light.cubeAttenuation);
			//draw
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.vao.vertexCount, GL11.GL_UNSIGNED_INT, 0);// use draw array
		}
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		//disable vertex attribute
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		//unbind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		//unbind vao
		GL30.glBindVertexArray(0);
		//unuse shader program
		GL20.glUseProgram(0);

	}

	static void drawPlayer(PlayerShader shader, Camera camera, Light light, Player player) {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GL20.glUseProgram(shader.programID);
		//bind vao
		GL30.glBindVertexArray(player.vao.vaoid);
		//enable vertex attribute
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		//bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);// indicate the texture unit
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, player.texture);
		//load uniform matrix
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.calculateTransformMatrix(player));
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.calculateViewMatrix(camera));
		GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
		GL20.glUniform3f(shader.location_lightColour,light.red,light.green,light.blue);
		GL20.glUniform1f(shader.location_shineDamper,player.shineDamper); //高光参数
		GL20.glUniform1f(shader.location_reflectivity,player.reflectivity);
		//draw
		GL11.glDrawElements(GL11.GL_TRIANGLES, player.vao.vertexCount, GL11.GL_UNSIGNED_INT, 0);// use draw array
		//disable vertex attribute
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		//unbind texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		//unbind vao
		GL30.glBindVertexArray(0);
		//unuse shader program
		GL20.glUseProgram(0);
	}
}
