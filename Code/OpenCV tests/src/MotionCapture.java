import org.opencv.core.Core;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class MotionCapture {

	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 1000; // 1 seconds between every frame (every 30 seconds increases one second)
		int recordTime = 90000; // 30 seconds
		int getReadyTime = 3000; // 3 seconds
		// Save pictures and resulting pictures:
		boolean saveResults = true;
		boolean saveOrgInt = true;
		// Paths to save results
//		String path = "experiment_results/MotionCapture/test/"; // Test folder to overwrite
		String path = "experiment_results/MotionCapture/static/"; // Experiment while static
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
		double outputs[] = new double[recordTime / (3 * pause)];
		int index = 0;
		// Loop that gets images, analyzes them and prints output
		for (int t = 0; t < recordTime; t += 3 * pause) {
			if (t == 30000) {
				pause = 2000;
			} 
			if (t == 60000) {
				pause = 3000;
			}
			// Capture three frames
			WebcamImage images[] = getThreeFrames(pause);
			// Analyse motion
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
		double sigma = stdv.evaluate(outputs, mu);
		try {
			// Write content to output file
			bw.write("\nMean: " + String.valueOf(mu) + "\nStandard Dev: " + String.valueOf(sigma));
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
	private static WebcamImage[] getThreeFrames(int pause) {
		WebcamImage images[] = new WebcamImage[3];
		for (int i = 0; i < 3; i++) {
			//System.out.println(i + 1);
			images[i] = new WebcamImage(pause);
		}
		return images;
	}
} // End class
