package data;

public class MeshData {
	private static final int DIMENSIONS = 3;

	public float[] vertices;
	public float[] textureCoords;
	public float[] normals;
	public int[] indices;
	public int[] jointIds;
	public float[] vertexWeights;
	
	public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
			int[] jointIds, float[] vertexWeights) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.jointIds = jointIds;
		this.vertexWeights = vertexWeights;
	}
	
	public int getVertexCount() {
		return vertices.length/DIMENSIONS;
	}
	
//	public vao
}
