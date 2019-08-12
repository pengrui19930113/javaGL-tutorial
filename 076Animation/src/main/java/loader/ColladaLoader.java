package loader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import bridge.Loadable;
import data.JointData;
import data.JointTransformData;
import data.KeyFrameData;
import data.MeshData;
import xml.XmlNode;
import xml.XmlParser;

/**
 * http://www.wazim.com/Collada_Tutorial_1.htm
	http://www.wazim.com/Collada_Tutorial_2.htm
	
	.dae file collada
 * @author 14899
 *
 */
public class ColladaLoader implements Loadable{

	/**
	 * collada 文件中的 坐标系 是右手系 
	 * 但是物体向上的向量是 z 不是y所以坐标系的基为
	 * 1	0	0	0
	 * 0	0	1	0
	 * 0	-1	0	0
	 * 0	0	0	1
	 *  由世界坐标系的基到该基的变换矩阵 与上矩阵是同一个矩阵
	 *  将该基下的坐标变换到世界坐标系需要 该基下的列坐标坐成 该矩阵的转置矩阵 
	 */
	private static final Matrix4f CORRECTION;
	static {
//		CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0,0));//使用坐标系旋转来变换坐标系统
		CORRECTION = new Matrix4f();
		CORRECTION.m11 = 0;
		CORRECTION.m12 = 1;
		CORRECTION.m22 = 0;
		CORRECTION.m21 = -1;
		CORRECTION.transpose();
	}
	
	private XmlNode xmlRoot;
	private int maxWights;// 最多受影响 个数
	private JointListData jointOrder;// bone order	骨骼名单列表
	
	/**
	 * 节点列表
	 * @author 14899
	 *
	 */
	private class JointListData {

		public List<String> list;
		
		public JointListData(String [] names) {
			list = new ArrayList<String>(names.length);
			for(String name:names) {
				list.add(name);
			}
		}
		
		@Override
		public String toString() {
			return list == null?"null":list.toString();
		}
	}
	/**
	 * 生成动画数据的数据
	 */
	//模型数据
	private JointData rootJointData;// 骨骼根节点
	private int jointCount;// 骨骼关节个数
	private MeshData meshData;//网格数据用来创建 VAO
	//动画数据
	private float animationTimeDruation;//动画持续时间
	private KeyFrameData[] keyFrameDatas;
	
	public ColladaLoader(String filePath,int maxWights) {
		this.maxWights = maxWights;
		this.jointCount = 0;
		this.xmlRoot = new XmlParser().loadXmlFile(filePath);
		parseData();
	}

	private void parseData() {
		List<VertexSkinData> vertexSkinDataList = 
				libraryControllers();
		libraryVisualScenes();
		libraryGeometries(vertexSkinDataList);
		libraryAnimations();
	}

	private void libraryAnimations() {
		XmlNode animationsNode = xmlRoot.getChild("library_animations");
		String[] rawTimesData = animationsNode.getChild("animation").getChild("source").getChild("float_array").getData().split(" ");
		float[] times = new float[rawTimesData.length];
		for(int i=0;i<times.length;i++) {
			times[i] = Float.parseFloat(rawTimesData[i]);
		}
		this.animationTimeDruation = times[times.length-1];
		this.keyFrameDatas = new KeyFrameData[times.length];
		for(int i=0;i<keyFrameDatas.length;i++) {
			keyFrameDatas[i] = new KeyFrameData(times[i]);
		}
		String rootJointName = 
				xmlRoot.getChild("library_visual_scenes")
				.getChild("visual_scene")
				.getChildWithAttribute("node", "id", "Armature")
				.getChild("node").getAttribute("id");
		List<XmlNode> animationNodes = animationsNode.getChildren("animation");
		for(XmlNode jointNode:animationNodes) {// 每个节点都存储了动画中该关节的关键帧变换矩阵	也就是说如果一个动画有5帧，这个节点就存储了该关节的5个变换矩阵
			String jointNameId = jointNode.getChild("channel").getAttribute("target").split("/")[0];
			String dataId = jointNode.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT").getAttribute("source").substring(1);
			String[] rawMatrixData = jointNode.getChildWithAttribute("source", "id", dataId).getChild("float_array").getData().split(" ");
			FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
			float[] matrixData = new float[16];
			for(int i=0;i<keyFrameDatas.length;i++) {
				for(int j=0;j<16;j++) {
					matrixData[j] = Float.parseFloat(rawMatrixData[i*16+j]);
				}
				buffer.clear();
				buffer.put(matrixData);
				buffer.flip();
				Matrix4f transform = new Matrix4f();
				transform.load(buffer);
				transform.transpose();
				if(rootJointName.equals(jointNameId)) { //如果是根节点 则变换矩阵的基到世界坐标系 由于其他的关节是参考根节点的  所以只需要变化根节点
					Matrix4f.mul(CORRECTION, transform, transform);
				}
				keyFrameDatas[i].addJointTransform(new JointTransformData(jointNameId,transform));
			}
		}
	}

	/**
	 * 顶点数据
	 * @author 14899
	 *
	 */
	private class VertexData {
		
		public static final int NO_INDEX = -1;
		
		public int index;
		public Vector3f position;
		public VertexSkinData weightsData;
		public int textureIndex;
		public int normalIndex;
		// 由于没有使用法线贴图 所以 没有存储 tangents
		/**
		 * 当存在同一个 index  但是 纹理索引或者法线索引存在不一致的情况下添加到该链表下
		 */
		public VertexData next;
		public VertexData(int index, Vector3f position, VertexSkinData weightsData) {
			this.index = index;
			this.position = position;
			this.weightsData = weightsData;
			this.textureIndex = NO_INDEX;
			this.normalIndex = NO_INDEX;
			this.next = null;
		}
		
		public VertexData setTextureIndex(int index) {
			this.textureIndex = index;
			return this;
		}
		public VertexData setNormalIndex(int index) {
			this.normalIndex = index;
			return this;
		}
		
	}
	
	/**
	 * 循环和递归都可以实现顶点处理
	 * 使用循环还是递归 二选一
	 * 
	 * true 递归
	 * false 循环
	 */
	private static boolean recursive = false;
	/**
	 * 教程中有这个变量 未来或许会用到
	 */
	public float furthestPoint = 0;
	private void libraryGeometries(List<VertexSkinData> vertexSkinDataList) {
		List<VertexData> vertices = new ArrayList<VertexData>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Integer> indices = new ArrayList<Integer>();
		XmlNode meshNode = xmlRoot.getChild("library_geometries").getChild("geometry").getChild("mesh");
		//position
		String positionsId = meshNode.getChild("vertices").getChild("input").getAttribute("source").substring(1);
		XmlNode positionNode = meshNode.getChildWithAttribute("source", "id", positionsId).getChild("float_array");
		int count = Integer.parseInt(positionNode.getAttribute("count"));
		String[] positionRawData = positionNode.getData().split(" ");
		for(int i=0;i<count/3;i++) {
			float x = Float.parseFloat(positionRawData[i*3 + 0]);
			float y = Float.parseFloat(positionRawData[i*3 + 1]);
			float z = Float.parseFloat(positionRawData[i*3 + 2]);
			Vector4f p4 = new Vector4f(x,y,z,1);
			Matrix4f.transform(CORRECTION, p4, p4);
			int index = vertices.size();
			vertices.add(new VertexData(index,new Vector3f(p4.x,p4.y,p4.z),vertexSkinDataList.get(index)));
		}
		//normal
		XmlNode polyListNode = meshNode.getChild("polylist");
		
		String normalsId = polyListNode.getChildWithAttribute("input", "semantic", "NORMAL").getAttribute("source").substring(1);
		XmlNode normalNode = meshNode.getChildWithAttribute("source", "id",normalsId).getChild("float_array");
		count = Integer.parseInt(normalNode.getAttribute("count"));
		String normalRawData[] = normalNode.getData().split(" ");
		for(int i=0;i<count/3;i++) {
			float x = Float.parseFloat(normalRawData[i*3 + 0]);
			float y = Float.parseFloat(normalRawData[i*3 + 1]);
			float z = Float.parseFloat(normalRawData[i*3 + 2]);
			Vector4f n4 = new Vector4f(x,y,z,0);
			Matrix4f.transform(CORRECTION, n4, n4);
			normals.add(new Vector3f(n4.x,n4.y,n4.z));
		}
		//texture coordinations
		String texCoordsId = polyListNode.getChildWithAttribute("input", "semantic", "TEXCOORD").getAttribute("source").substring(1);
		XmlNode textureNode = meshNode.getChildWithAttribute("source", "id",texCoordsId).getChild("float_array");
		count = Integer.parseInt(textureNode.getAttribute("count"));
		String[] texRawData = textureNode.getData().split(" ");
		for(int i=0;i<count/2;i++) {
			float s = Float.parseFloat(texRawData[i*2 + 0]);
			float t = Float.parseFloat(texRawData[i*2 + 1]);
			textures.add(new Vector2f(s,t));
		}
		// assemble vertices
		count = polyListNode.getChildren("input").size();// size = 4 ; //VERTEX NORMAL TEXCOORD COLOR
		String[] indexRawData = polyListNode.getChild("p").getData().split(" ");
		for(int i=0;i<indexRawData.length/count;i++) {
			int positionIndex = Integer.parseInt(indexRawData[i*count +0]);
			int normalIndex = Integer.parseInt(indexRawData[i*count +1]);
			int texCoordIndex = Integer.parseInt(indexRawData[i*count +2]);
			VertexData currentVertex = vertices.get(positionIndex);
			if(!(VertexData.NO_INDEX!=currentVertex.textureIndex && VertexData.NO_INDEX!=currentVertex.normalIndex)) {//如果 textureIndex 和 normalIndex 都没被设置过了
				currentVertex.setTextureIndex(texCoordIndex)
						.setNormalIndex(normalIndex);
				indices.add(positionIndex);
			}else {
				if(recursive) {//递归处理链表
					dealWithAlreadyProcessedVertex(currentVertex,texCoordIndex,normalIndex,vertices,indices);//递归处理链表
				}else{//循环处理链表 	循环和递归二选一
					VertexData another = currentVertex;
					VertexData temp = currentVertex;
					while(true) {
						//如果查找  链表（VertexData.next） 中存在该纹理 则不创建新的纹理添加到链表末尾 直接添加索引
						if(texCoordIndex == another.textureIndex && another.normalIndex == normalIndex) {
							indices.add(another.index);
							break;
						}else { 
							//查找该节点是否存在下一个节点 如果存在 则递归处理
							temp = another;
							another = another.next;
							if(null!=another) {
								continue;
							}else {//如果不存在 也就是查找整个 共同纹理索引和法向量索引的 VertexData 链表中不存在 需要新创建并添加到 vertices 并添加到链表末尾
								VertexData last = new VertexData(vertices.size(),temp.position,temp.weightsData);
								last.textureIndex = texCoordIndex;
								last.normalIndex = normalIndex;
								temp.next = last; //添加到链表末尾
								vertices.add(last);
								indices.add(last.index);
								break;
							}
						}
					}
				}
			}
		}
		// set unused vertices texture index and normal index to zero
		for(VertexData vertex:vertices) // vertex.averageTangents;// 学习的源码中有这行代码， 但是没看到动画这个教程中用到 所以注释掉
			if(!(VertexData.NO_INDEX!=vertex.normalIndex && VertexData.NO_INDEX!=vertex.textureIndex))//如果索引没有被设置过 则设置为0
				vertex.setNormalIndex(0).setTextureIndex(0);
		
		float[] verticesArray = new float[vertices.size()*3];
		float[] texturesArray = new float[vertices.size()*2];
		float[] normalsArray = new float[vertices.size()*3];
		int[] jointIdsArray = new int[vertices.size()*3];
		float[] weightsArray = new float[vertices.size()*3];
		float length;
		for(int i=0;i<vertices.size();i++) {
			VertexData cur = vertices.get(i);
			if((length = cur.position.length()) > furthestPoint) //计算最远点  或许其他的教程中有用到吧
				furthestPoint = length;
			
			Vector3f position = cur.position;
			Vector2f texCoord = textures.get(cur.textureIndex);
			Vector3f normal = normals.get(cur.normalIndex);
			VertexSkinData weights = cur.weightsData;
			
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = texCoord.x;
			texturesArray[i * 2 + 1] = 1 - texCoord.y; // 做了坐标变换  因为纹理坐标是左手系
			normalsArray[i * 3] = normal.x;
			normalsArray[i * 3 + 1] = normal.y;
			normalsArray[i * 3 + 2] = normal.z;
			jointIdsArray[i * 3] = weights.jointIds.get(0);
			jointIdsArray[i * 3 + 1] = weights.jointIds.get(1);
			jointIdsArray[i * 3 + 2] = weights.jointIds.get(2);
			weightsArray[i * 3] = weights.weights.get(0);
			weightsArray[i * 3 + 1] = weights.weights.get(1);
			weightsArray[i * 3 + 2] = weights.weights.get(2);
		}
		int[] indicesArray = new int[indices.size()];
		for(int i=0;i<indices.size();i++) {
			indicesArray[i] = indices.get(i);
		}
		
		this.meshData = new MeshData(verticesArray, texturesArray, normalsArray, indicesArray, jointIdsArray, weightsArray);
	}

	private void dealWithAlreadyProcessedVertex(VertexData previousVertex
			, int newTextureIndex, int newNormalIndex, List<VertexData> vertices, List<Integer> indices) {
		//递归返回条件。如果递归查找  链表（VertexData.next） 中存在该纹理 则不创建新的纹理添加到链表末尾 直接添加索引
		if(newTextureIndex == previousVertex.textureIndex && previousVertex.normalIndex == newNormalIndex) {
			indices.add(previousVertex.index);
			return;
		}else {//递归体  
			//查找该节点是否存在下一个节点 如果存在 则递归处理
			VertexData another = previousVertex.next;
			if(null!=another) {
				dealWithAlreadyProcessedVertex(another,newTextureIndex,newNormalIndex,vertices,indices);
				return;
			}else {//如果不存在 也就是递归查找整个 共同纹理索引和法向量索引的 VertexData 链表中不存在 需要新创建并添加到 vertices 并添加到链表末尾
				VertexData last = new VertexData(vertices.size(),previousVertex.position,previousVertex.weightsData);
				last.textureIndex = newTextureIndex;
				last.normalIndex = newNormalIndex;
				previousVertex.next = last; //添加到链表末尾
				vertices.add(last);
				indices.add(last.index);
				return;
			}
		}
	}

	private void libraryVisualScenes() {
		XmlNode armatureNode = 
				xmlRoot.getChild("library_visual_scenes")
				.getChild("visual_scene")
				.getChildWithAttribute("node","id","Armature");
		XmlNode joinRootNode = armatureNode.getChild("node");
		rootJointData = loadJointData(joinRootNode,true);
	}

	private JointData loadJointData(XmlNode node, boolean isRoot) {
		String nameId = node.getAttribute("id");
		int index = jointOrder.list.indexOf(nameId);
		String[] matrixRawData = node.getChild("matrix").getData().split(" ");
		float[] matrixData = new float[16];
		for(int i=0;i<16;i++) 
			matrixData[i] = Float.parseFloat(matrixRawData[i]);
		
		FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
		matrixBuffer.put(matrixData);
		matrixBuffer.flip();
		Matrix4f localMatrix = new Matrix4f();
		localMatrix.load(matrixBuffer);
		localMatrix.transpose();
		if(isRoot) 
			Matrix4f.mul(CORRECTION, localMatrix, localMatrix);
		
		JointData joint = new JointData(index,nameId,localMatrix);
		jointCount++;
		
		for(XmlNode childNode:node.getChildren("node")) 
			joint.addChild(loadJointData(childNode,false));
		
		return joint;
	}

	private ArrayList<VertexSkinData> libraryControllers() {
		XmlNode skinNode = 
				xmlRoot.getChild("library_controllers")
				.getChild("controller")
				.getChild("skin");
		XmlNode vertexWeightsNode = skinNode.getChild("vertex_weights");
		//
		String joinDataId = vertexWeightsNode.getChildWithAttribute("input", "semantic", "JOINT").getAttribute("source").substring(1);
		String names[] = skinNode.getChildWithAttribute("source", "id", joinDataId).getChild("Name_array").getData().split(" ");
		this.jointOrder = new JointListData(names);
		//获取权重原始数据列表
		String weightDataId = vertexWeightsNode.getChildWithAttribute("input", "semantic", "WEIGHT").getAttribute("source").substring(1);
		String[] weightRawData = skinNode.getChildWithAttribute("source", "id", weightDataId).getChild("float_array").getData().split(" ");
		float[] weights = new float[weightRawData.length];
		for(int i=0;i<weights.length;i++) {
			weights[i] = Float.parseFloat(weightRawData[i]);
		}
		//获取顶点技术列表
		String[] vcountRawData = vertexWeightsNode.getChild("vcount").getData().split(" ");
		int[] effectorJointCounts = new int[vcountRawData.length];
		for(int i=0;i<effectorJointCounts.length;i++) {
			effectorJointCounts[i] = Integer.parseInt(vcountRawData[i]);
		}
		
		//配对骨骼索引和权重数据
		ArrayList<VertexSkinData> vertexSkinDataList = new ArrayList<VertexSkinData>();
		int pointer = 0;
		String[] vRawData = vertexWeightsNode.getChild("v").getData().split(" ");
		for(int count:effectorJointCounts) {
			VertexSkinData skinData = new VertexSkinData();
			for(int i=0;i<count;i++) {
				int jointId = Integer.parseInt(vRawData[pointer++]);
				int weightId = Integer.parseInt(vRawData[pointer++]);
				skinData.addJointEffect(jointId, weights[weightId]);
			}
			skinData.limitJointNumber(maxWights);//消除多余权重影响
			vertexSkinDataList.add(skinData);
		}
		return vertexSkinDataList;
	}
	
	@Override
	public JointData getRootJointData() {
		return rootJointData;
	}

	@Override
	public int getJointCount() {
		return jointCount;
	}

	@Override
	public MeshData getMeshData() {
		return meshData;
	}

	@Override
	public float getAnimationTime() {
		return animationTimeDruation;
	}

	@Override
	public KeyFrameData[] getKeyFrameDatas() {
		return keyFrameDatas;
	}
	
	private class VertexSkinData {
		
		public List<Integer> jointIds;
		public List<Float> weights;
		
		public VertexSkinData() {
			jointIds = new ArrayList<Integer>();
			weights = new ArrayList<Float>();
		}
		
		/**
		 * 添加权重 降序摆列  例如 先后添加    0.2,0.5,0.13,0.17 则放在 list 中的 顺序 为  0.5 0.2 0.17 0.13 
		 * @param jointId
		 * @param weight
		 */
		public void addJointEffect(int jointId,float weight) {
			for(int i=0;i<weights.size();i++) {
				if(weight>weights.get(i)) {
					jointIds.add(i,jointId);//insert
					weights.add(i,weight);//insert
					return;
				}
			}
			jointIds.add(jointId);//insert
			weights.add(weight);//insert
		}

		/**
		 * 如果设定的最大权重个数为3个  但是从文件中读出多个，例如4个  分别是 0.4,0.3,0.2,0.1
		 *  则取最大的3个 0.4,0.3,0.2 然后按3个值得占比重新赋值 令他们得和为1 则最后得到的3个权值分别为	4/9,3/9,2/9
		 * @param max
		 */
		public void limitJointNumber(int max) {
			if(jointIds.size()>max) {
				float topMaxTotal = 0;
				for(int i=0;i<jointIds.size();i++) {
					if(i<max) {
						topMaxTotal += weights.get(i);
					}else {// 移除多余的权重参数
						jointIds.remove(i);
						weights.remove(i);
					}
				}
				// 根据权重比值设置最后给的权重 使得其和为1
				for(int i=0;i<max;i++) {
					weights.set(i, Math.min(weights.get(i)/topMaxTotal, 1));
				}
			}else if(jointIds.size() < max) {
				do {
					jointIds.add(0);
					weights.add(0f);
				}while(jointIds.size() < max);
			}
		}
	}
}
