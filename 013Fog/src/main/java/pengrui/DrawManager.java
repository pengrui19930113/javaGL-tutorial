package pengrui;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class DrawManager {
	
	public static void drawBegin() {
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
	
	public static void drawEnd() {
		//nothing
	}
	
	public static void drawTerrain(TerrainShader shader,Camera camera,Light light,Terrain terrain) {
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
	
	public static void drawEntity(EntityShader shader,Camera camera,Light light,List<Entity> entities) {
		if(null == entities || entities.isEmpty())	return;
		//indicate shader program
		GL20.glUseProgram(shader.programID);
		Entity same = entities.get(0);
		//bind vao
		GL30.glBindVertexArray(same.vao.vaoid);
		if(same.useFakeLighting) {
			GL20.glUniform1f(shader.location_useFakeLighting, 1f);
		}else {
			GL20.glUniform1f(shader.location_useFakeLighting, .0f);
		}
		if(same.hasTransparency) {
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
		//enable vertex attribute
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		//bind texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);// indicate the texture unit
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, same.texture);
		for(Entity entity:entities) {
			//load uniform matrix
			FloatBuffer transformBuffer = MatrixUtil.caculateTransformMatrix(entity);
			GL20.glUniformMatrix4(shader.location_transformationMatrix, false, transformBuffer);
			FloatBuffer viewBuffer = MatrixUtil.caculateViewMatrix(camera);
			GL20.glUniformMatrix4(shader.location_viewMatrix, false, viewBuffer);
			GL20.glUniform3f(shader.location_lightPosition, light.xPos, light.yPos, light.zPos);
			GL20.glUniform3f(shader.location_lightColour,light.red,light.green,light.blue);
			GL20.glUniform1f(shader.location_shineDamper,entity.shineDamper); //高光参数
			GL20.glUniform1f(shader.location_reflectivity,entity.reflectivity);
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
}
