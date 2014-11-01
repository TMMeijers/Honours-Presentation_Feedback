import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class WebcamImage {
	
	// WebcamImage is a matrix that represents a web cam image
	public Mat imgMatrix;

	public WebcamImage(int pause) {
		
		if (pause < 1000) {
			pause = 1000;
		} else if (pause > 5000) {
			pause = 5000;
		}
		
		// Initialize webcam
		VideoCapture webcam = new VideoCapture(0);
		try {
			// Wait for webcam to be connected
			Thread.sleep(pause);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Capture image
		Mat temp = new Mat();
		// Two reads to empty buffer
		webcam.read(temp);
		webcam.read(temp);
		
		imgMatrix = temp;
	}
}