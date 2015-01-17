import org.opencv.core.Core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MotionCapture {

	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 2000; // 2 seconds between every frame
		int recordTime = 30000; // 30 seconds
		int getReadyTime = 5000; // 3 seconds
		String path = "experiment_results/MotionCapture/";

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
		
		// Loop that gets images, analyzes them and prints output
		for (int t = 0; t < recordTime; t += 3 * pause) {
			// Capture three frames
			WebcamImage images[] = getThreeFrames(pause);
			// Analyse motion
			AnalyzeFrames analysis = new AnalyzeFrames(images, path);
			double movementPercentage = analysis.getMovementPercentage();
			// Write content to output file
			try {
				bw.write((int) movementPercentage);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
