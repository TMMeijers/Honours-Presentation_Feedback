import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class MotionCapture {
	
	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 2000; // 1 seconds between every frame (every 30 seconds increases one second)
		int recordTime = 90000; // 30 seconds
		int getReadyTime = 3000; // 3 seconds
		// Save pictures and resulting pictures:
		boolean saveResults = true;
		boolean saveOrgInt = true;
		// Paths to save results
//		String path = "experiment_results/MotionCapture/test/"; // Test folder to overwrite
//		String path = "experiment_results/MotionCapture/1secondall/"; // Go from static to dynamic (1 second pause between frames)
		String path = "experiment_results/MotionCapture/2secondall/"; // Go from static to dynamic (2 second pause between frames)
//		String path = "experiment_results/MotionCapture/static/"; // Experiment while static
//		String path = "experiment_results/MotionCapture/normal/"; // Experiment while normal
//		String path = "experiment_results/MotionCapture/dynamic/"; // Experiment while very dynamic

		// Open text file for writing results
		File output = new File(path + "output_static.txt");
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
		System.out.println("Get ready!");
		try {
			Thread.sleep(getReadyTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		// Open webcam
		VideoCapture webcam = new VideoCapture(0);
		// Initialise output array
		double outputs[] = new double[recordTime / (3 * pause)];
		int index = 0;
		// Loop that gets images, analyzes them and prints output
		for (int t = 0; t < recordTime; t += 3 * pause) {
//			if (t == 30000) {
//				pause = 2000;
//			} 
//			if (t == 60000) {
//				pause = 3000;
//			}

			// Capture three frames
			Mat images[] = getThreeFrames(webcam, pause);
			// Analyse the three frames
			AnalyzeFrames analysis = new AnalyzeFrames(images, path, index, saveResults, saveOrgInt);
			
			double movementPercentage = analysis.getMovementPercentage();
			outputs[index] = movementPercentage;
			try {
				// Write content to output file
				bw.write(index + ". " + String.valueOf(movementPercentage) + '\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
			index++;
		}
		Mean mean = new Mean();
		StandardDeviation stdv = new StandardDeviation();
		double mu = mean.evaluate(outputs);
		double mu1 = mean.evaluate(outputs, 0, 5);
		double mu2 = mean.evaluate(outputs, 5, 5);
		double mu3 = mean.evaluate(outputs, 10, 5);
		double sigma = stdv.evaluate(outputs, mu);
		double sigma1 = stdv.evaluate(outputs, mu1, 0, 5);
		double sigma2 = stdv.evaluate(outputs, mu2, 5, 5);
		double sigma3 = stdv.evaluate(outputs, mu3, 10, 5);
		try {
			// Write content to output file
			bw.write("\n1 SECOND PAUSE (First 30 seconds):\nMean1: " + String.valueOf(mu1) + "\nStandard Dev1: " + String.valueOf(sigma1) + '\n');
			bw.write("\n2 SECOND PAUSE (Second 30 seconds):\nMean2: " + String.valueOf(mu2) + "\nStandard Dev2: " + String.valueOf(sigma2) + '\n');
			bw.write("\n3 SECOND PAUSE (Third 30 seconds):\nMean3: " + String.valueOf(mu3) + "\nStandard Dev3: " + String.valueOf(sigma3) + '\n');
			bw.write("\nOVERALL (Whole 90 seconds):\nMean: " + String.valueOf(mu) + "\nStandard Dev: " + String.valueOf(sigma));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Close buffered writer
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} // End main
	
	// Captures and returns array with three WebcamImage's
	private static Mat[] getThreeFrames(VideoCapture webcam, int pause) {
		Mat images[] = new Mat[3];
		for (int i = 0; i < 3; i++) {
			images[i] = captureFrame(webcam, pause);
		}
		return images;
	}
	
	public static Mat captureFrame(VideoCapture webcam, int pause) {
		
		if (pause < 1000) {
			pause = 1000;
		} else if (pause > 5000) {
			pause = 5000;
		}
		
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
		Mat imgMatrix = new Mat(temp.rows(), temp.cols(), CvType.CV_8UC1);
	    Imgproc.cvtColor(temp, imgMatrix, Imgproc.COLOR_RGB2GRAY);
	    
	    return imgMatrix;
	}
} // End class
