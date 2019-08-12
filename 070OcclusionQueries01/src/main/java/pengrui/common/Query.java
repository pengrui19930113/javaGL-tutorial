package pengrui.common;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;

public class Query {
	public int id;
	public int type;
	public boolean inUse;
	
	public Query(List<Integer> queries,int type) {
		int id = GL15.glGenQueries();
		queries.add(id);
		this.id = id;
		this.type = type;
		this.inUse = false;
	}
	
	public void test() {
		
		id = GL15.glGenQueries();
		
		GL15.glBeginQuery(GL15.GL_SAMPLES_PASSED, id);
		GL15.glBeginQuery(GL33.GL_ANY_SAMPLES_PASSED, id);
		//renderQuad();
		GL15.glEndQuery(GL15.GL_SAMPLES_PASSED);
		while(GL11.GL_FALSE == GL15.glGetQueryObjecti(id, GL15.GL_QUERY_RESULT_AVAILABLE)) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int res = GL15.glGetQueryObjecti(id, GL15.GL_QUERY_RESULT);//1 0
		GL15.glDeleteQueries(id);
		
		System.out.println(res);
	}
}
