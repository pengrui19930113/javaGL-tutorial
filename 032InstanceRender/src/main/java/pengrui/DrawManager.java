package pengrui;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class DrawManager {
	
	public static void draw(EntityShader endityShader,List<Entity> entities,Camera camera,Light light
			, TerrainShader terrainShader, Terrain terrain
			, PlayerShader playerShader, Player player
			, GuiEntityShader guiShader, List<GuiEntity> guis
			, SkyboxShader skyboxShader, Skybox skybox
			, ParticleShader particleShader, ParticleSystem system) {
		drawBegin();
		drawTerrain(terrainShader, camera, light, terrain);
		drawPlayer(playerShader,camera,light,player);
		drawEntity(endityShader, camera, light, entities);
		drawSkyBox(skyboxShader,camera, light,skybox);
		drawParticles(particleShader,camera,light,system);
		//drawGuiEntity(guiShader,guis);
		drawEnd();
	}
	
	static void drawParticles(ParticleShader shader, Camera camera, Light light,
			ParticleSystem system) {
		int pointer = 0;
		GL20.glUseProgram(shader.programID);
		GL30.glBindVertexArray(Particle.vao.vaoid);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDepthMask(false);//disable writing into the depth buffer
		Matrix4f viewMatrix = MatrixUtil.getViewMatrix(camera);
		Matrix4f tempMatrix = new Matrix4f();
		Vector3f Z_NORMALIZE_VECTOR = new Vector3f(0,0,1);
		for(int textureID:system.particles.keySet()) {
			List<Particle> partList = system.particles.get(textureID);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			GL20.glUniform1f(shader.location_numberOfRows, partList.get(0).numberOfRows);
			float[] vboData = new float[partList.size()*ParticleSystem.INSTANCE_DATA_LENGTH];
			for(Particle particle:partList) {
				Matrix4f modelMatrix = tempMatrix;
				modelMatrix.setIdentity();
				Matrix4f.translate(new Vector3f(particle.xPos,particle.yPos,particle.zPos),modelMatrix,modelMatrix);
				modelMatrix.m00 = viewMatrix.m00;// 由于旋转矩阵都是正交矩阵，所以该处也可以求解
				modelMatrix.m01 = viewMatrix.m10;// 模型矩阵左上角3x3的矩阵的可以用视图逆矩阵左上角3x3进行填充但是效率不如转置
				modelMatrix.m02 = viewMatrix.m20;
				modelMatrix.m10 = viewMatrix.m01;
				modelMatrix.m11 = viewMatrix.m11;
				modelMatrix.m12 = viewMatrix.m21;
				modelMatrix.m20 = viewMatrix.m02;
				modelMatrix.m21 = viewMatrix.m12;
				modelMatrix.m22 = viewMatrix.m22;
				Matrix4f.rotate((float)Math.toRadians(particle.rotation), Z_NORMALIZE_VECTOR, modelMatrix, modelMatrix);
				Matrix4f.scale(new Vector3f(particle.scale,particle.scale,particle.scale), modelMatrix, modelMatrix);
				Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, tempMatrix);
				vboData[pointer++] = modelViewMatrix.m00;// attribute 1
				vboData[pointer++] = modelViewMatrix.m01;
				vboData[pointer++] = modelViewMatrix.m02;
				vboData[pointer++] = modelViewMatrix.m03;
				vboData[pointer++] = modelViewMatrix.m10;// attribute 2
				vboData[pointer++] = modelViewMatrix.m11;
				vboData[pointer++] = modelViewMatrix.m12;
				vboData[pointer++] = modelViewMatrix.m13;
				vboData[pointer++] = modelViewMatrix.m20;// attribute 3
				vboData[pointer++] = modelViewMatrix.m21;
				vboData[pointer++] = modelViewMatrix.m22;
				vboData[pointer++] = modelViewMatrix.m23;
				vboData[pointer++] = modelViewMatrix.m30;// attribute 4
				vboData[pointer++] = modelViewMatrix.m31;
				vboData[pointer++] = modelViewMatrix.m32;
				vboData[pointer++] = modelViewMatrix.m33;
				vboData[pointer++] = particle.texOffset1x;// attribute 5
				vboData[pointer++] = particle.texOffset1y;
				vboData[pointer++] = particle.texOffset2x;
				vboData[pointer++] = particle.texOffset2y;
				vboData[pointer++] = particle.blend;// attribute 6
			}
			system.dataBuffer.clear();
			system.dataBuffer.put(vboData);
			system.dataBuffer.flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, system.instanceVaraibleVbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, system.dataBuffer.capacity()*4, GL15.GL_STREAM_DRAW);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, system.dataBuffer);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, Particle.vao.vertexCount, partList.size());
		}
		GL11.glDepthMask(true);//enable writing into the depth buffer
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}

	static void drawSkyBox(SkyboxShader shader, Camera camera, Light light, Skybox skybox) {
		GL20.glUseProgram(shader.programID);
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.calcuateSkyboxViewMatrixBuffer(camera));
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
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.calcuateTransformMatrixBuffer(gui));
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
		
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.calcuateTransformMatrixBuffer(terrain));
		GL20.glUniformMatrix4(shader.location_viewMatrix,false,MatrixUtil.calcuateViewMatrixBuffer(camera));
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
			FloatBuffer transformBuffer = MatrixUtil.calcuateTransformMatrixBuffer(entity);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, transformBuffer);
			FloatBuffer viewBuffer = MatrixUtil.calcuateViewMatrixBuffer(camera);
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
		GL20.glUniformMatrix4(shader.location_transformationMatrix, false, MatrixUtil.calcuateTransformMatrixBuffer(player));
		GL20.glUniformMatrix4(shader.location_viewMatrix, false, MatrixUtil.calcuateViewMatrixBuffer(camera));
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
