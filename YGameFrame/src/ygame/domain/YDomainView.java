package ygame.domain;

import android.opengl.GLES20;
import ygame.framework.core.YGL_Configuration;
import ygame.framework.core.YSystem;
import ygame.framework.domain.YADomainView;
import ygame.framework.domain.YBaseDomain;
import ygame.framework.domain.YReadBundle;
import ygame.skeleton.YSkeleton;

/**
 * <b>实体视图</b>
 * 
 * <p>
 * <b>概述</b>： 该视图使用您在构造方法中传入的<b>着色程序</b>
 * {@link YAShaderProgram}进行渲染
 * 
 * <p>
 * <b>建议</b>： TODO
 * 
 * <p>
 * <b>详细</b>： TODO
 * 
 * <p>
 * <b>注</b>： TODO
 * 
 * <p>
 * <b>例</b>： TODO
 * 
 * @author yunzhong
 * 
 */
public final class YDomainView extends YADomainView
{
	private YAShaderProgram program;
	private YSkeleton skeleton;

	public YDomainView(YSkeleton skeleton, YAShaderProgram program)
	{
		this.program = program;
		this.skeleton = skeleton;
	}

	@Override
	protected void onDraw(YReadBundle bundle, YBaseDomain domainContext)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		program.startRendering();
		program.applyParams(program.iProgramHandle, bundle, skeleton);
		program.endRendering();

		// int clearMask =
		// GLES20.GL_COLOR_BUFFER_BIT;
		// GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,
		// 0);
		// GLES20.glClearColor(1, 0, 0, 1);
		// clearMask |=
		// GLES20.GL_DEPTH_BUFFER_BIT;
		// GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		// GLES20.glDepthFunc(GLES20.GL_LESS);
		// GLES20.glDepthMask(true);
		// GLES20.glClearDepthf(1.0f);
		//
		// GLES20.glClear(clearMask);
		//
		// YASkeleton1 geometry3d =
		// ((YDomain)
		// domainContext).geometry3d;
		// geometry3d.validateBuffers();
		// GLES20.glEnable(GLES20.GL_CULL_FACE);
		// GLES20.glCullFace(GLES20.GL_BACK);
		// GLES20.glFrontFace(GLES20.GL_CCW);
		// GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		// GLES20.glDepthFunc(GLES20.GL_LESS);
		// GLES20.glDepthMask(true);
		// material.useProgram();
		//
		// // setShaderParams(camera);
		// material.bindTextures();
		// if
		// (geometry3d.hasTextureCoordinates())
		// material.setTextureCoords(geometry3d
		// .getTexCoordBufferInfo().bufferHandle);
		// if (geometry3d.hasNormals())
		// material.setNormals(geometry3d.getNormalBufferInfo().bufferHandle);
		// if (material.usingVertexColors())
		// material.setVertexColors(geometry3d
		// .getColorBufferInfo().bufferHandle);
		//
		// material.setVertices(geometry3d.getVertexBufferInfo().bufferHandle);
		// material.applyParams();
		// GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,
		// 0);
		//
		// if (null !=
		// bundle.readMVP_Matrix())
		// material.setMVPMatrix(bundle.readMVP_Matrix());
		// if (null !=
		// bundle.readModelMatrix())
		// material.setModelMatrix(bundle.readModelMatrix());
		// if (null !=
		// bundle.readMV_Matrix())
		// material.setModelViewMatrix(bundle.readMV_Matrix());
		// if (null !=
		// bundle.readNormalMatrix())
		// material.setNormalMatrix(bundle.readNormalMatrix());
		//
		// GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,
		// geometry3d.getIndexBufferInfo().bufferHandle);
		// GLES20.glDrawElements(GLES20.GL_TRIANGLES,
		// geometry3d.getNumIndices(),
		// GLES20.GL_UNSIGNED_SHORT, 0);
		// GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,
		// 0);
		// material.unbindTextures();
	}

	@Override
	protected void onGL_Initialize(YSystem system,
			YGL_Configuration configurationGL, int iWidth,
			int iHeight)
	{
		int iProgram = program.initialize();
		skeleton.initilize(iProgram);
		GLES20.glClearColor(1f, 1f, 0.8f, 1f);
		// YTextureManager textureManager =
		// YTextureManager.getInstance();
		// ArrayList<ATexture> textureList =
		// material.getTextureList();
		// for (ATexture texture :
		// textureList)
		// textureManager.taskAdd(texture);
		// YMaterialManager.getInstance().taskAdd(material);
	}

}
