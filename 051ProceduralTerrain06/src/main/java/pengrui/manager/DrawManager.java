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
import pengrui.skybox.Skybox;
import pengrui.skybox.SkyboxShader;
import pengrui.terrain.Terrain;
import pengrui.terrain.TerrainShader;
import pengrui.util.MatrixUtil;

public class DrawManager {
	
	public static void draw(EntityShader endityShader,List<Entity> entities,Camera camera,Light light
			, TerrainShader terrainShader, Terrain terrain
			, PlayerShader playerShader, Player player
			, GuiEntityShader guiShader, List<GuiEntity> guis
			, SkyboxShader skyboxShader, Skybox skybox
			, NormalMappingEntityShader normalMappingEntityShader, List<NormalMappingEntity> normalEntitys
			, StaticTextShader textShader, List<StaticText> texts, FontEffect fontEffect
			) {
		drawBegin();
		drawTerrain(terrainShader, camera, light, terrain);
		drawPlayer(playerShader,camera,light,player);
		drawEntity(endityShader, camera, light, entities);
		drawNormalMappingEntity(normalMappingEntityShader,camera,light,normalEntitys);
		drawSkyBox(skyboxShader,camera, light,skybox);
		drawTexts(textShader,texts,fontEffect);
		//drawGuiEntity(guiShader,guis);
		drawEnd();
	}
	
	static void drawTexts(StaticTextShader shader, List<StaticText> texts, FontEffect fontEffect) {
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
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.caculateTransformMatrix(entity));
			GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.caculateViewMatrix(camera));
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
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.caculateSkyboxViewMatrix(camera));
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
		GL20.glUseProgram(shader.programID);
		GL30.glBindVertexArray(GuiEntity.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for(GuiEntity gui:guis) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.texture);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.caculateTransformMatrix(gui));
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
		
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.caculateTransformMatrix(terrain));
		GL20.glUniformMatrix4(shader.location_viewMatrix,false,MatrixUtil.caculateViewMatrix(camera));
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);;
		GL20.glDisableVertexAttribArray(2);;
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
			if(entity.useFakeLighting) {
				GL20.glUniform1f(shader.location_useFakeLighting, 1f);
			}else {
				GL20.glUniform1f(shader.location_useFakeLighting, .0f);
			}
			if(entity.hasTransparency) {
				GL11.glDisable(GL11.GL_CULL_FACE);
			}
			//bind texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);// indicate the texture unit
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.texture);
			//load uniform matrix
			FloatBuffer transformBuffer = MatrixUtil.caculateTransformMatrix(entity);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, transformBuffer);
			FloatBuffer viewBuffer = MatrixUtil.caculateViewMatrix(camera);
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
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.caculateTransformMatrix(player));
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.caculateViewMatrix(camera));
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
