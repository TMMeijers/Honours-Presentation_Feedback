
import java.awt.Rectangle;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class SimpleMotionDetection {

	public static void main(String args[]) {
		
		// Set time between frames (min 1000 ms. max 5000 ms)
		int pause = 1000;
		int nrOfFrames = 3;
		
		// For displaying and writing to disk
		boolean saveResults = true;
		boolean displayOriginals = true;
		boolean displayIntermediate = true;
		boolean displayResults = true;
		
		// Load openCV lib
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Capture images through webcam, 1 frame per pause
		System.out.println("Get ready!");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		WebcamImage images[] = new WebcamImage[nrOfFrames];
		for (int i = 0; i < nrOfFrames; i++) {
			System.out.println(i + 1);
			images[i] = new WebcamImage(pause);
		}
		
		// Get image size:
		int matRows = images[0].imgMatrix.rows(); // 480 pixels
		int matCols = images[0].imgMatrix.cols(); // 640 pixels
		int matType = images[0].imgMatrix.type();
		// Declare target matrices for calculations
		Mat temp1 = new Mat(matRows, matCols, matType);
		Mat temp2 = new Mat(matRows, matCols, matType);
		Mat results[] = new Mat[nrOfFrames - 2];
		Rectangle rects[] = new Rectangle[nrOfFrames - 2];

		// Analyze movement, three frames needed per result
		for (int i = 0; i < nrOfFrames - 2; i++) {
			results[i] = new Mat(matRows, matCols, matType);
			
			Core.absdiff(images[i].imgMatrix, images[i+2].imgMatrix, temp1);
			Core.absdiff(images[i+1].imgMatrix, images[i+2].imgMatrix, temp2);
			Core.bitwise_and(temp1, temp2, results[i]);

			Imgproc.threshold(results[i], results[i], 100, 255, Imgproc.THRESH_BINARY);
			
			int changes = 0;
			int min_x = 0;
			int max_x = 0;
			int min_y = 0;
			int max_y = 0;
			int movement = 0;
			int nomovement = 0;
			
			// Check movement box
			for(int j = 0; j < matRows; j+=2){ // height
				for(int k = 0; k < matCols; k+=2){ // width
			        // If pixel shows movement (value = 255), update area
			    	if (results[i].get(j, k)[0] == 255) {
			    		movement++;
//			    		for (int z = 0; z < results[i].get(j, k).length; z++) {
//			    			System.out.println(z);
//			    			System.out.println(results[i].get(j, k)[z]);
//			    		}
			            changes++;
			            if(min_x > k) min_x = k;
			            if(max_x < k) max_x = k;
			            if(min_y > j) min_y = j;
			            if(max_y < j) max_y = j;
			        } else {
			        	nomovement++;
			        }
			    }
			}
			System.out.println(movement);
			System.out.println(nomovement);
			
			// Draw rectangle
			if (changes > 0){
	            // Generate rectangle
	            rects[i] = new Rectangle(min_x, min_y, max_x - min_x, max_y - min_y);
	        }
			
			// Display intermediate results
			if (displayIntermediate) {
				Mat2Buffered bufImg1 = new Mat2Buffered(temp1);
				bufImg1.display();
				Mat2Buffered bufImg2 = new Mat2Buffered(temp2);
				bufImg2.display();
			}
			
			// Save temp images to disk
			if (saveResults) {
				String name = "1.intermediate" + (i * 2 + 0) + ".jpg";
				Highgui.imwrite(name, temp1);
				name = "1.intermediate" + (i * 2 + 1) + ".jpg";
				Highgui.imwrite(name, temp2);
			}
		}
		
		// Display original images
		if (displayOriginals) {
			// Convert to buffered image and display
			for (int i = 0; i < nrOfFrames - 2; i++) {
				Mat2Buffered bufImg = new Mat2Buffered(images[i + 2].imgMatrix);
				bufImg.display(rects[i]);
			}
		}
		
		// Display results
		if (displayResults) {
			for (int i = 0; i < results.length; i++) {
				Mat2Buffered bufImg = new Mat2Buffered(results[i]);
				bufImg.display();
			}
		}

		if (saveResults) {
			// Convert to buffered image and display
			for (int i = 0; i < images.length; i++) {
				String name = "0.original" + i + ".jpg";
				Highgui.imwrite(name, images[i].imgMatrix);
			}
			for (int i = 0; i < results.length; i++) {
				String name = "3.result" + i + ".jpg";
				Highgui.imwrite(name, results[i]);
			}
		}
	} // End main
} // End class
