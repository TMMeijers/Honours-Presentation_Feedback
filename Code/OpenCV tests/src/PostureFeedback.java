import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class PostureFeedback {
	
	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 100; // 1 seconds between every frame (every 30 seconds increases one second)
		int recordTime = 30000; // 30 seconds
		int getReadyTime = 2500; //
		
		// Paths to save results
		String path = "experiment_results/PostureFeedback/"; // Go from static to dynamic (2 second pause between frames)

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
		
		Mat images[] = captureFrame(webcam, pause);
		Mat orgImg = images[1];
		Mat picture = images[0];
		Mat nextImg;
		
		String name = path + "0.0.org.jpg";
		Highgui.imwrite(name, picture);
		PostureFrame frame = new PostureFrame("Posture Feedback", picture);
		
		// Loop that gets images, analyzes them and prints output
		for (int t = 0; t < recordTime; t += pause) {
			// Capture three frames
//			Mat currentImg = captureFrame(webcam, pause);
			images = captureFrame(webcam, pause);
			nextImg = images[1];
			picture = images[0];
			
			// Analyse the frames
			double result = analyzeTwoImages(orgImg, nextImg, path, index);
			outputs[index] = result;
			
			// Show results and save
			frame.updateComponents(result, picture);
			try {
				// Write content to output file
				bw.write(String.valueOf(result) + '\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
			index++;
//			name = path + index + ".0.current.jpg";
//			Highgui.imwrite(name, currentImg);
			name = path + index + ".1.next.jpg";
			Highgui.imwrite(name, picture);
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
	
	private static double analyzeTwoImages(Mat org, Mat next, String path, int i) {
		
		int threshold = 50;
		int matRows = org.rows(); // 480 pixels
		int matCols = org.cols(); // 640 pixels
		int matType = org.type();
		// Declare target matrices for calculations
		Mat result = new Mat(matRows, matCols, matType);

		// Analyze movement, two frames needed per result
		Core.absdiff(org, next, result);

		Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_BINARY);

		i++;
		String name = path + i + ".1.result.jpg";
		Highgui.imwrite(name, result);
		
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
	
	private static double analyzeThreeImages(Mat org, Mat current, Mat next, String path, int i) {
		
		int threshold = 50;
		int matRows = org.rows(); // 480 pixels
		int matCols = org.cols(); // 640 pixels
		int matType = org.type();
		// Declare target matrices for calculations
		Mat temp1 = new Mat(matRows, matCols, matType);
		Mat temp2 = new Mat(matRows, matCols, matType);
		Mat result = new Mat(matRows, matCols, matType);

		// Analyze movement, two frames needed per result
		Core.absdiff(org, current, temp1);
		Core.absdiff(org, next, temp2);
		Core.bitwise_and(temp1, temp2, result);

		Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_BINARY);

		i++;
		String name = path + i + ".2.result.jpg";
		Highgui.imwrite(name, result);
		
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
