package pengrui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ObjVaoLoader {

	public static VAO createObjVao(List<Integer>vaos,List<Integer> vbos,String file) {
		return createObjVao(vaos,vbos,ClassLoader.getSystemResourceAsStream(file));
	}
	
	public static VAO createObjVao(List<Integer>vaos,List<Integer> vbos,InputStream is) {
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textureCoords = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();// 现阶段没用到 //no use
		List<Integer> indices = new ArrayList<Integer>();
		int[]indicesArray = null;
		float[]verticesArray = null;
		float[]normalsArray = null;// 现阶段没用到 //no use
		float[]textureArray = null;
		
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try { // v vt vn f
			while(null!=(line = br.readLine())) {
				if(line.startsWith("#") || line.trim().length()<1) 
					continue;
				
				if(line.startsWith("v ")) {
					String[] strs = line.split(" ");
					vertices.add(new Vector3f(Float.parseFloat(strs[1]),Float.parseFloat(strs[2]),Float.parseFloat(strs[3])));
				}else if(line.startsWith("vt ")) {
					String[] strs = line.split(" ");
					textureCoords.add(new Vector2f(Float.parseFloat(strs[1]),Float.parseFloat(strs[2])));
				}else if(line.startsWith("vn ")) {
					String[] strs = line.split(" ");
					normals.add(new Vector3f(Float.parseFloat(strs[1]),Float.parseFloat(strs[2]),Float.parseFloat(strs[3])));
				}else if(line.startsWith("f ")) {
					textureArray = new float[vertices.size()*2];
					normalsArray= new float[vertices.size()*3];
					break;
				}
			}
			
			do {
				if(line.startsWith("#") || line.trim().length()<1) 
					continue;
				//放在得内存数组索引首地址为0 但是 三角形面使用得各个索引起始地址是1
				String[] strs = line.split(" ");//	f v1/t1/n1 v2/t2/n2 v3/t3/n3
				String[] vertex1 = strs[1].split("/");
				String[] vertex2 = strs[2].split("/");
				String[] vertex3 = strs[3].split("/");
				processVertex(vertex1,indices,textureCoords,normals,textureArray,normalsArray);
				processVertex(vertex2,indices,textureCoords,normals,textureArray,normalsArray);
				processVertex(vertex3,indices,textureCoords,normals,textureArray,normalsArray);
			}while(null!=(line = br.readLine()));
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("read obj file exception");
		}
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		int vertPointer = 0;
		for(Vector3f vert:vertices){
			verticesArray[vertPointer++]= vert.x;
			verticesArray[vertPointer++]= vert.y;
			verticesArray[vertPointer++]= vert.z;
		}
		
		for(int i=0;i<indices.size();++i)
			indicesArray[i] = indices.get(i);
		return VAO.createVao(vaos,vbos,verticesArray,textureArray,normalsArray,indicesArray);
	}
	
	private static void processVertex(
			String[] vertexData
			,List<Integer> indices
			,List<Vector2f> textureCoords
			,List<Vector3f> normals
			,float[] textureArray
			,float[] normalsArray
			){
		processVertex(vertexData,indices,textureCoords,normals,textureArray,normalsArray,true);
	}
	private static void processVertex(
			String[] vertexData
			,List<Integer> indices
			,List<Vector2f> textureCoords
			,List<Vector3f> normals
			,float[] textureArray
			,float[] normalsArray
			,boolean transTexture//是否转换纹理坐标
			){
		int currVertPointer = Integer.valueOf(vertexData[0])-1;
		indices.add(currVertPointer);
		Vector2f currTex = textureCoords.get(Integer.valueOf(vertexData[1])-1);
		textureArray[currVertPointer*2] = currTex.x;
		if(transTexture) {
			textureArray[currVertPointer*2+1] = 1-currTex.y;// 因为纹理坐标的原点在纹理的左上角
		}else {
			textureArray[currVertPointer*2+1] = currTex.y;// 因为纹理坐标的原点在纹理的左上角
		}
		Vector3f currNorm = normals.get(Integer.valueOf(vertexData[2])-1);
		normalsArray[currVertPointer*3] = currNorm.x;
		normalsArray[currVertPointer*3+1] = currNorm.y;
		normalsArray[currVertPointer*3+2] = currNorm.z;
	}
	
}
