package model;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class VideoGroup extends Group{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public File video;
	private int height, width, frames;
	private List<Mat> selectedFrames;
	
	public VideoGroup(String name, String path) throws Exception{
		super(name,path,0);
		if (path != null && path.matches(".*((\\.[aA][vV][iI])|(\\.[mM][pP][4])|(\\.[mM][kK][vV])|(\\.[wW][mM][vV])|(\\.[mM][4][vV]))")) {
			video = new File(path);
			selectedFrames = new ArrayList<Mat>();
			//extractFrameList();
	    }
	}

	public File getVideo(){
		return video;
	}
	
	public void setParameters(double pyrScale, int levels, int winSize, int iterations, int polyN, double polySigma) {
		this.pyrScale = pyrScale;
		this.levels = levels;
		this.winSize = winSize;
		this.iterations = iterations;
		this.polyN = polyN;
		this.polySigma = polySigma;
	}	

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public int size(){
		return frames;
	}
	
	public void clearFrames(){
		this.selectedFrames.clear();
	}
	
	public void addFrame(Mat img){
		this.selectedFrames.add(img);
	}
	
	public int framesSize(){
		return this.selectedFrames.size();
	}
	
	public Mat getFrame(int i){
		return this.selectedFrames.get(i);
	}
}