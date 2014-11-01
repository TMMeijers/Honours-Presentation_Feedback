import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class ImageCapture {
	
	public static void main(String args[]) {
		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		System.out.println("OpenCV image capture from webcam");

		// Initialize webcam
		VideoCapture webcam = new VideoCapture(0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (webcam.isOpened() == true) {
			System.out.println("Webcam connected");
		} else {
			System.out.println("Webcam not found");
		}
	}
}
