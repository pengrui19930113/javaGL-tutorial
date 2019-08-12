package main;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import bridge.Loadable;
import loader.ColladaLoader;
import model.AnimatedModel;
import shader.AnimatedModelShader;
import utils.DisplayManager;
import utils.OpenglUtil;

public class Main {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loadable loader = new ColladaLoader("model/model.dae",3); 
		Camera camera = new Camera();
		AnimatedModelShader shader = new AnimatedModelShader();
		AnimatedModel model = new AnimatedModel(loader,"model/diffuse.png");
		Vector3f lightDirection = new Vector3f(0, -1, 0);

		while(!Display.isCloseRequested()) {
			OpenglUtil.clearError();
			camera.move();
			model.update();
			//render
			GL11.glClearColor(0.8f, 0.8f, 0.8f, 1);
	
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			GL11.glDisable(GL11.GL_BLEND);
			GL20.glUseProgram(shader.program);
			GL20.glUniformMatrix4(shader.location_projectionViewMatrix, false, camera.getProjectionViewMatrixBuffer());
			GL20.glUniform3f(shader.location_lightDirection, lightDirection.x, lightDirection.y, lightDirection.z);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture);
			
			Matrix4f[] jointTransforms = model.getJointTransforms();
			for(int i=0;i< jointTransforms.length;i++) {
				FloatBuffer buf = BufferUtils.createFloatBuffer(16);
				jointTransforms[i].store(buf);
				buf.flip();
				GL20.glUniformMatrix4(shader.location_jointTransforms[i], false, buf);
			}
	
			GL30.glBindVertexArray(model.vaoid);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			GL20.glEnableVertexAttribArray(4);
//			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.indexCount, GL11.GL_UNSIGNED_INT, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL20.glDisableVertexAttribArray(3);
			GL20.glDisableVertexAttribArray(4);
			GL30.glBindVertexArray(0);
			GL20.glUseProgram(0);
			DisplayManager.update();
		}
		shader.delete();
		model.delete();
		DisplayManager.closeDisplay();
	}
}
