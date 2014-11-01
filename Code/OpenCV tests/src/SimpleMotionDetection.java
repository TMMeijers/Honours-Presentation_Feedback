import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class SimpleMotionDetection {

	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 1000;
		
		// For displaying and writing to disk
		boolean display = true;
		boolean write = false;
		boolean showResults = true;
		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		// Capture nrOfFrames images
		int nrOfFrames = 3;
		System.out.println("Get ready!");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		WebcamImage images[] = new WebcamImage[nrOfFrames];
		for (int i = 0; i < nrOfFrames; i++) {
			System.out.println(i + 1);
			images[i] = new WebcamImage(pause);
		}
		
		if (display) {
			// Convert to buffered image and display
			for (int i = 0; i < nrOfFrames; i++) {
				Mat2Buffered bufImg = new Mat2Buffered(images[i].imgMatrix);
				bufImg.display();
			}
		}
		if (write) {
			// Convert to buffered image and display
			for (int i = 0; i < nrOfFrames; i++) {
				String name = "webcam" + i + ".jpg";
				Highgui.imwrite(name, images[i].imgMatrix);
			}
		}
		
		// Get image size:
		int matRows = images[0].imgMatrix.rows();
		int matCols = images[0].imgMatrix.cols();
		int matType = images[0].imgMatrix.type();

		// Initialize target matrices for calculations
		Mat temp1 = new Mat(matRows, matCols, matType);
		Mat temp2 = new Mat(matRows, matCols, matType);
		Mat results[] = new Mat[nrOfFrames-2];

		for (int i = 0; i < nrOfFrames - 2; i++) {
			System.out.println("Frame " + (i+1));
			
			temp1 = new Mat(matRows, matCols, matType);
			temp2 = new Mat(matRows, matCols, matType);
			results[i] = new Mat(matRows, matCols, matType);
			
			Core.absdiff(images[i].imgMatrix, images[i+2].imgMatrix, temp1);
			Core.absdiff(images[i+1].imgMatrix, images[i+2].imgMatrix, temp2);
			Core.bitwise_and(temp1, temp2, results[i]);
			// Stil have to look to adaptive thresholds
			Imgproc.threshold(results[i], results[i], 80, 255, Imgproc.THRESH_BINARY);
		}
		
		if (showResults) {
			for (int i = 0; i < results.length; i++) {
				Mat2Buffered bufImg = new Mat2Buffered(results[i]);
				bufImg.display();
			}
		}
		
	} // End main
} // End class
