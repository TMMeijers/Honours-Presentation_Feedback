import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class HandFeedback {
	
	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 100; //
		int recordTime = 30000; // 
		int getReadyTime = 500; //
		
		// Paths to save results
		String path = "experiment_results/handFeedback/"; // Go from static to dynamic (2 second pause between frames)

		// Open text file for writing results
		File output = new File(path + "output.txt");
		BufferedWriter bw = null;
		try {
			if (!output.exists()) {
				// Create if it doesn't exist yet
				output.createNewFile();
				// Open buffered writer
				bw = new BufferedWriter(new FileWriter(output.getAbsoluteFile()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Load openCV lib
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Pause before start recording
		System.out.println("Get ready! Make sure you stand still in the first image");
		try {
			Thread.sleep(getReadyTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		// Open webcam
		VideoCapture webcam = new VideoCapture(0);
		// Initialise output array
		double outputs[] = new double[recordTime / pause];
		int index = 0;
		
		// Get first image and its position
		Mat orgImg = captureFrame(webcam, pause);
		Point orgPos = getInitialPoint(orgImg, path);
		HandFrame frame = new HandFrame("Posture Feedback", orgImg);
	
		Mat nextImg;
		
		// Loop that gets images, analyzes them and prints output
		for (int t = 0; t < recordTime; t += pause) {
			// Capture three frames
			nextImg = captureFrame(webcam, pause);
			
			// Analyse the frames
			double result = analyzeTwoImages(orgPos, nextImg, path, index);
			outputs[index] = result;
			
			// Show results and save
			frame.updateComponents(result, nextImg);
			try {
				// Write content to output file
				bw.write(String.valueOf(result) + '\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
			index++;
		}
		
		// Close buffered writer
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	} // End main
	
	private static Point getInitialPoint(Mat org, String path) {
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

		// Save img
		String name = path + "0.StartPosition.jpg";
		Highgui.imwrite(name, temp);
		
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
	
	private static double analyzeTwoImages(Point org, Mat next, String path, int i) {
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

		// Save img
		i++;
		String name = path + i + ".next.jpg";
		Highgui.imwrite(name, temp);
		
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
		
	private static Mat captureFrame(VideoCapture webcam, int pause) {
				
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
	    
	    return temp;
	}
} // End class
