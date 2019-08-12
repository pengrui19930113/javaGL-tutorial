package pengrui;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector4f;

public class DrawManager {
	
	static final Vector4f N1 = new Vector4f(0,1,0,0);
	static final Vector4f N2 = new Vector4f(0,-1,0,0);
	static final Vector4f N3 = new Vector4f(0,1,0,10);
	
	public static void draw(EntityShader endityShader,List<Entity> entities,Camera camera,Light light
			, TerrainShader terrainShader, Terrain terrain
			, PlayerShader playerShader, Player player
			, GuiEntityShader guiShader, List<GuiEntity> guis
			, SkyboxShader skyboxShader, Skybox skybox
			, WaterShader waterShader, WaterTile water, WaterFramebuffers waterFramebuffers) {
		
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		Camera invertCamera = camera.getInvertCamera(water);
		bindReflectionFramebuffer(waterFramebuffers);
			drawBegin();
//				N1.w = water.height;//由于水的高度是0 所以改行注释
				drawTerrain(terrainShader, invertCamera, light, terrain,N1);
				drawPlayer(playerShader,invertCamera,light,player,N1);
				drawEntity(endityShader, invertCamera, light, entities);
				drawSkyBox(skyboxShader,invertCamera, light,skybox);
			drawEnd();
		bindRefractionFramebuffer(waterFramebuffers);
			drawBegin();
//				N2.w = water.height;//由于水的高度是0 所以改行注释
				drawTerrain(terrainShader, camera, light, terrain,N2);
				drawPlayer(playerShader,camera,light,player,N2);
				drawEntity(endityShader, camera, light, entities);
				drawSkyBox(skyboxShader,camera, light,skybox);
			drawEnd();
		unbindCurrentFramebuffer();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		
		drawBegin();
			drawTerrain(terrainShader, camera, light, terrain,N3);
			drawPlayer(playerShader,camera,light,player,N3);
			drawEntity(endityShader, camera, light, entities);
			drawSkyBox(skyboxShader,camera, light,skybox);
			drawWater(waterShader,camera,light,water,waterFramebuffers);
			drawGuiEntity(guiShader,guis);
		drawEnd();
	}
	
	static void unbindCurrentFramebuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	static void bindReflectionFramebuffer(WaterFramebuffers waterFramebuffers) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,waterFramebuffers.reflectionFrameBuffer);
		GL11.glViewport(0, 0, WaterFramebuffers.REFLECTION_WIDTH, WaterFramebuffers.REFLECTION_HEIGHT);
	}
	static void bindRefractionFramebuffer(WaterFramebuffers waterFramebuffers) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,waterFramebuffers.refractionFrameBuffer);
		GL11.glViewport(0, 0, WaterFramebuffers.REFRACTION_WIDTH, WaterFramebuffers.REFRACTION_HEIGHT);
	}

	static void drawWater(WaterShader shader, Camera camera, Light light, WaterTile water, WaterFramebuffers waterFramebuffers) {
		GL20.glUseProgram(shader.programID);
		GL20.glUniformMatrix4(shader.location_viewMatrix,false,MatrixUtil.caculateViewMatrixBuffer(camera));
		GL20.glUniform1f(shader.location_moveFactor, water.moveFactor);
		GL20.glUniform3f(shader.location_cameraPosition, camera.cameraPosX, camera.cameraPosY, camera.cameraPosZ);
		GL20.glUniform3f(shader.location_lightColour, light.red, light.green, light.blue);
		GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
		GL30.glBindVertexArray(WaterTile.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL20.glUniformMatrix4(shader.location_modelMatrix, false, MatrixUtil.caculateModelMatrixBuffer(water));
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFramebuffers.reflectionTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFramebuffers.refractionTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, WaterTile.dudvTexture);	
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, WaterTile.normalMap);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFramebuffers.refractionDepthTexture);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, WaterTile.vao.vertexCount);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	static void drawSkyBox(SkyboxShader shader, Camera camera, Light light, Skybox skybox) {
		GL20.glUseProgram(shader.programID);
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.caculateSkyboxViewMatrixBuffer(camera));
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
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.caculateTransformMatrixBuffer(gui));
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
	
	static void drawTerrain(TerrainShader shader,Camera camera,Light light,Terrain terrain,Vector4f clipPalne) {
		GL20.glUseProgram(shader.programID);
		GL30.glBindVertexArray(terrain.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.caculateTransformMatrixBuffer(terrain));
		GL20.glUniformMatrix4(shader.location_viewMatrix,false,MatrixUtil.caculateViewMatrixBuffer(camera));
		GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
		GL20.glUniform3f(shader.location_lightColour, light.red, light.green, light.blue);
		GL20.glUniform4f(shader.location_plane, clipPalne.x, clipPalne.y, clipPalne.z, clipPalne.w);
		
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
//		GL11.glEnable(GL11.GL_CULL_FACE);
//		GL11.glFrontFace(GL11.GL_CCW);//默认 逆时针
//		GL11.glCullFace(GL11.GL_BACK);
		
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
			FloatBuffer transformBuffer = MatrixUtil.caculateTransformMatrixBuffer(entity);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, transformBuffer);
			FloatBuffer viewBuffer = MatrixUtil.caculateViewMatrixBuffer(camera);
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

	static void drawPlayer(PlayerShader shader, Camera camera, Light light, Player player, Vector4f clipPalne) {
//		GL11.glEnable(GL11.GL_CULL_FACE);
//		GL11.glCullFace(GL11.GL_BACK);
		
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
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.caculateTransformMatrixBuffer(player));
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.caculateViewMatrixBuffer(camera));
		GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
		GL20.glUniform3f(shader.location_lightColour,light.red,light.green,light.blue);
		GL20.glUniform1f(shader.location_shineDamper,player.shineDamper); //高光参数
		GL20.glUniform1f(shader.location_reflectivity,player.reflectivity);
		GL20.glUniform4f(shader.location_plane, clipPalne.x, clipPalne.y, clipPalne.z, clipPalne.w);
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
