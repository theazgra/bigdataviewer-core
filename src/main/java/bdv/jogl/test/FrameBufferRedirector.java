package bdv.jogl.test;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import bdv.jogl.shader.UnitCube;

import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.FBObject.Colorbuffer;
import com.jogamp.opengl.GL2;

/**
 * Provides methods to redirect the frame buffer to a cpu readable format mainly for test purposes 
 * @author michael
 *
 */
public class FrameBufferRedirector {
	
	private FBObject internalFrameBuffer;
	
	private List<UnitCube>renderElements =  new ArrayList<UnitCube>();

	private int width = 480;
	
	private int height = 640;
	
	private Colorbuffer buffer;
	
	private void initFrameBufferObject(GL2 gl2){	
		buffer = FBObject.createColorTextureAttachment(gl2, true, width, height);
		buffer.initialize(gl2);
		internalFrameBuffer = new FBObject();
		internalFrameBuffer.init(gl2, width, height, 0);
		internalFrameBuffer.attachColorbuffer(gl2, 0, buffer);
		internalFrameBuffer.bind(gl2);
	
	}
	
	private void disposeFrameBufferObject(GL2 gl2){
		internalFrameBuffer.destroy(gl2);
	}
	
	/**
	 * @return the renderElements
	 */
	public List<UnitCube> getRenderElements() {
		return renderElements;
	}

	/**
	 * init wrapper
	 * @param gl2
	 */
	public void init(GL2 gl2){
		initFrameBufferObject(gl2);
		for(UnitCube element : renderElements){
			element.init(gl2);
		}
	}
	
	/**
	 * render wrapper
	 * @param gl2
	 */
	public void render(GL2 gl2){
		internalFrameBuffer.bind(gl2);
		for(UnitCube element: renderElements){
			element.render(gl2);
		}
		internalFrameBuffer.unbind(gl2);
	}
	
	/**
	 * dispose wrapper
	 * @param gl2
	 */
	public void disposeGL(GL2 gl2){
		for(UnitCube element: renderElements){
			element.disposeGL(gl2);
		}
		disposeFrameBufferObject(gl2);
		buffer.free(gl2);
	}
	
	/**
	 * returns the current content of the framebuffer as a matrix. x,y rgba
	 * @return
	 */
	public float[][][] getFrameBufferContent(GL2 gl2){
		internalFrameBuffer.bind(gl2);
		float[][][] matrix = new float[width][height][4];
		FloatBuffer buffer = FloatBuffer.allocate(height*width*4);
		gl2.glReadPixels(0, 0, width, height, GL2.GL_RGBA, GL2.GL_FLOAT, buffer);
		buffer.rewind();
		
		//to array
		for(int y =0; y< height; y++){
			for(int x = 0; x < width; x++){
				for(int c =0; c < 4; c++){
					matrix[x][y][c] = buffer.get((y*width + x)*4+c);
				}
			}
		}
		
		internalFrameBuffer.unbind(gl2);
		return matrix;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
}