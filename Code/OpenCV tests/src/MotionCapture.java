import org.opencv.core.Core;
public class MotionCapture {

	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 2000; // 2 seconds between every frame
		int recordTime = 30000; // 30 seconds
		int getReadyTime = 3000; // 3 seconds
		
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
			AnalyzeFrames analysis = new AnalyzeFrames(images);
			double movementPercentage = analysis.getMovementPercentage();
			System.out.println(movementPercentage);
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
