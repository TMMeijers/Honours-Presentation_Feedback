import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.*;

 
public class EyeTracking{
	public static void main(String args[]) {
		
		// Load openCV lib
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("strawberry.jpg"));
		} catch (IOException e) {
		}
		
		ImageFilter filter = new GrayFilter(true, 50);  
		ImageProducer producer = new FilteredImageSource(img.getSource(), filter);  
		Image image = Toolkit.getDefaultToolkit().createImage(producer);  
		
		
		face_cascade = CascadeClassifier("haarcascade_frontalface_default.xml");
		eye_cascade = CascadeClassifier("haarcascade_eye.xml");
		faces = face_cascade.detectMultiScale(image, 1.3, 5);
				for (x,y,w,h) in faces:
				    cv2.rectangle(img,(x,y),(x+w,y+h),(255,0,0),2)
				    roi_gray = gray[y:y+h, x:x+w]
				    roi_color = img[y:y+h, x:x+w]
				    eyes = eye_cascade.detectMultiScale(roi_gray)
				    for (ex,ey,ew,eh) in eyes:
				        cv2.rectangle(roi_color,(ex,ey),(ex+ew,ey+eh),(0,255,0),2)

		
	}
	
	public void displayImage(Image img)
	{   
	    //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
	    ImageIcon icon=new ImageIcon(img);
	    JFrame frame=new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);     
	    JLabel lbl=new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}
