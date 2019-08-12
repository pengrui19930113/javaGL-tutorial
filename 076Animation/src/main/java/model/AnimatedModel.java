package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import animation.Animation;
import animation.Animations;
import animation.Joint;
import animation.KeyFrame;
import bridge.Loadable;
import utils.DisplayManager;
import utils.TextureUtils;

public class AnimatedModel {
	public int vaoid;
	public int indexCount;
	public int texture;
	
	
	private Animations animations;
	private Animation currentAnimation;
	private float animationTime;
	private boolean repeatAnimation;
	private Joint rootJoint;
	private int jointCount;
	
	public AnimatedModel(Loadable loader, String textureFile) {
		MeshVaoCreator holder = new MeshVaoCreator().createVao(loader.getMeshData());
		this.vaoid = holder.vaoid;
		this.vbos = holder.vbos;
		this.indexCount = holder.indexCount;
		
		
		this.texture = TextureUtils.decodeTextureFile(textureFile);
		this.rootJoint = JointCreator.createJoints(loader.getRootJointData()).calcInverseBindTransform(ROOT_TRANSFORM);
		this.jointCount = loader.getJointCount();
		this.animations = new Animations(new Animation[] {new Animation(loader)});
		
		this.animationTime = 0;
		this.currentAnimation = animations.getDefaultAnimation();
		this.repeatAnimation = true;
	}

	private static final Matrix4f ROOT_TRANSFORM;
	static {
		ROOT_TRANSFORM = new Matrix4f();
	}
	
	boolean stop = false;
	long lastModityTime;
	public void update() {
		stopCheck();
		if(null!=currentAnimation && !stop) {
			animationTime += DisplayManager.getFrameTime();
			if(animationTime>currentAnimation.length) {//如果动画时间超过了该动画的最长时间
				if(repeatAnimation) {//设置了重复动画 则不切换到默认动画 只处理时间
					animationTime %= currentAnimation.length;
				}else {//如果没有设置重复动画 则动画设置为默认动画
					currentAnimation = animations.getDefaultAnimation();
					animationTime = 0;
				}
			}
			
			//求取当前时间所在哪两个关键帧之间关键帧
			KeyFrame previousFrame = currentAnimation.keyframes[0];
			KeyFrame nextFrame = previousFrame;//currentAnimation.keyframes[0];
			for(int i=1;i<currentAnimation.keyframes.length;i++) {
				nextFrame = currentAnimation.keyframes[i];
				if(nextFrame.time>animationTime)
					break;
				
				previousFrame = nextFrame;
			}
			
			float progression = (animationTime-previousFrame.time)/(nextFrame.time-previousFrame.time);
			Map<String,Matrix4f> currentPose = KeyFrame.interpolatePoses(previousFrame, nextFrame, progression);
			applyPoseTojoints(currentPose,rootJoint,ROOT_TRANSFORM);
		}
	}
	
	/**
	 * 变换到父节点空间中的变换矩阵
	 *  bindTransform = parentBindTransform * localBindTransform
	 * 	https://blog.csdn.net/qq_29523119/article/details/72848913
	 * 
	 * 举个例子  四个节点 p[0],p[1],p[2],p[3] 并且代表4个本地坐标	p[n-1] 是 p[n] 的父节点
	 * 四个变换矩阵分别为 m[0-4] 其中 m[0] 是单位矩阵（即指定p[0]在模型空间）
	 *  p[1] 坐标需要变换到 p[0] 所在的坐标系的坐标 为   p[1]r0 = m[1]*p[1];
	 *  以此类推	所以 p[3]r0 = m[0]*m[1]*m[2]*m[3]*p[3];
	 * 	
	 * 
	 * 本例使用的递归 来求取变换到模型坐标空间的变换矩阵	currentTransform
	 * @param currentPose
	 * @param joint
	 * @param parentTransform 变换到父坐标空间的变换矩阵
	 */
	private void applyPoseTojoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f curLocationTransform = currentPose.get(joint.name);
		
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, curLocationTransform, null);
		for(Joint childJoint:joint.children) {
			applyPoseTojoints(currentPose,childJoint,currentTransform);
		}
		Matrix4f.mul(currentTransform, joint.inverseBindTransform, currentTransform);//将vao 顶点变换到模型空间的原点
		joint.animatedTransform = currentTransform;
	}
	
	/**
	 * 暂停状态检测 当按下S的时候 转换暂停动画的状态 来控制动画是否暂停
	 */
	private void stopCheck(){//stop control
		if((System.currentTimeMillis() - lastModityTime > 200) &&Keyboard.isKeyDown(Keyboard.KEY_S)) {
			stop = !stop;
			lastModityTime = System.currentTimeMillis();
		}
	}
	
	private int[] vbos = new int[0];
	public void delete() {
		GL30.glDeleteVertexArrays(vaoid);
		GL11.glDeleteTextures(texture);
		for(int vbo:vbos)
			GL15.glDeleteBuffers(vbo);
	}
	
	/**
	 * {@link #getJointTransforms()}
	 * 不使用递归还获取变换坐标
	 * @return
	 */
	public Matrix4f[] getJointTransforms2() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		List<Joint> list = new LinkedList<Joint>();
		List<Joint> list2 = new LinkedList<Joint>();
		list.add(rootJoint);
		while(true) {
			if(list.isEmpty())
				break;
			
			list2.clear();
			for(Joint joint:list) {
				jointMatrices[joint.index] = joint.animatedTransform;
				for(Joint child:joint.children) {
					list2.add(child);
				}
			}
			list.clear();
			for(Joint child:list2) {
				list.add(child);
			}
		}
		return jointMatrices;
	}
	
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}
	
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.animatedTransform;
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}
}
