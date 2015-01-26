package PresentationFeedback;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class PresentationFeedback {
	
	public static void main(String args[]) {

		// Load openCV lib
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Time variables in ms
		int pause = 50; 
		int recordTime = 10000;
		int getReadyTime = 1000; 
		
		// Pause before start recording
		System.out.println("Get ready!");
		try {
			Thread.sleep(getReadyTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		// Open webcam
		VideoCapture webcam = new VideoCapture(0);

		Mat images[] = captureFrame(webcam, pause); // Throw away first one (overexposed)
		images = captureFrame(webcam, pause);
		Mat orgImg = images[1];
		Point orgPos = getInitialPoint(orgImg);
		Mat picture = images[0];
		Mat nextImg;
		
		FeedbackFrame frame = new FeedbackFrame("Presentation Feedback", picture);
		
		// Loop that gets images, analyzes them and prints output
		for (int t = 0; t < recordTime; t += pause) {
			// Capture three frames
//			Mat currentImg = captureFrame(webcam, pause);
			images = captureFrame(webcam, pause);
			nextImg = images[1];
			picture = images[0];
			
			// Analyse the frames
			double postureResult = analyzePosture(orgImg, nextImg);
			double gestureResult = analyzeGesture(orgPos, nextImg);

			// Show results and save
			frame.updateComponents(postureResult, gestureResult, picture);
		}
		
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	} // End main
	
	private static double analyzePosture(Mat org, Mat next) {
		
		int threshold = 50;
		int matRows = org.rows(); // 480 pixels
		int matCols = org.cols(); // 640 pixels
		int matType = org.type();
		// Declare target matrices for calculations
		Mat result = new Mat(matRows, matCols, matType);

		// Analyze movement, two frames needed per result
		Core.absdiff(org, next, result);
		Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_BINARY);

		int movement = 0;
		for(int j = 0; j < matRows; j++){ // height
			for(int k = 0; k < matCols; k++){ // width
		        // If pixel shows movement (value = 255), update area
		    	if (result.get(j, k)[0] == 255) {
		    		movement++;
		    	}
		    }
		}
		return ((double)movement / (matRows * matCols)) * 100;
	}	
	
	private static Point getInitialPoint(Mat org) {
		Mat temp = org.clone();

		// Get red blobss
		Scalar min = new Scalar(0, 0, 170);
		Scalar max = new Scalar(100, 100, 255);
		Core.inRange(temp, min, max, temp);
		
		// Close gaps
		double erosion_size = 0.5;
        int dilation_size = 5;
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
		Imgproc.erode(temp, temp, element);
        element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
		Imgproc.dilate(temp, temp, element);

		// Get blob positions
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(temp, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Point position = new Point();
		
		for (int j = 0; j < contours.size(); j++) {
			MatOfPoint tempPt = contours.get(j);
			Point[] tempPoints = tempPt.toArray();
			double x = 0;
			double y = 0;
			for (Point pt : tempPoints) {
				x += pt.x;
				y += pt.y;
			}
			position = new Point((x / tempPoints.length), (y / tempPoints.length));
		}
		return position;
	}
	
	private static double analyzeGesture(Point org, Mat next) {
		Mat temp = next.clone();

		// Get red blobss
		Scalar min = new Scalar(0, 0, 170);
		Scalar max = new Scalar(100, 100, 255);
		Core.inRange(temp, min, max, temp);
		
		// Close gaps
		double erosion_size = 0.5;
        int dilation_size = 10;
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
		Imgproc.erode(temp, temp, element);
        element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
		Imgproc.dilate(temp, temp, element);

		// Get blob positions
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(temp, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Point[] newPositions = new Point[contours.size()];
		
		for (int j = 0; j < contours.size(); j++) {
			MatOfPoint tempPt = contours.get(j);
			Point[] tempPoints = tempPt.toArray();
			double x = 0;
			double y = 0;
			for (Point pt : tempPoints) {
				x += pt.x;
				y += pt.y;
			}
			newPositions[j] = new Point((x / tempPoints.length), (y / tempPoints.length));
		}
		double result = 0;
		if (newPositions.length > 1) {
			result = Math.sqrt(Math.pow(newPositions[0].x - org.x, 2) + Math.pow(newPositions[0].y - org.y, 2));
			return result + Math.sqrt(Math.pow(newPositions[1].x - org.x, 2) + Math.pow(newPositions[1].y - org.y, 2));
		} else if (newPositions.length == 1) {
			return Math.sqrt(Math.pow(newPositions[0].x - org.x, 2) + Math.pow(newPositions[0].y - org.y, 2));
		}
		return 0.0;
	}	
		
	private static Mat[] captureFrame(VideoCapture webcam, int pause) {
				
		try {
			// Wait for webcam to be connected
			Thread.sleep(pause);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Capture image
		Mat temp = new Mat();
		// Two reads (One to empty buffer)
		webcam.read(temp);
		webcam.read(temp);
		 	
		// Convert RGB to grayscale
		Mat imgMatrix[] = new Mat[2];
		imgMatrix[0] = temp;
		Mat img = new Mat();
	    Imgproc.cvtColor(temp, img, Imgproc.COLOR_RGB2GRAY);
	    imgMatrix[1] = img;
	    
	    return imgMatrix;
	}
} // End class
