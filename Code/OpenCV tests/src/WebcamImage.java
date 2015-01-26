import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

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
		 	
		// Convert RGB to grayscale
		imgMatrix = new Mat(temp.rows(), temp.cols(), CvType.CV_8UC1);
	    Imgproc.cvtColor(temp, imgMatrix, Imgproc.COLOR_RGB2GRAY);

	}
}