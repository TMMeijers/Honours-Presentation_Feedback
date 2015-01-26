package PresentationFeedback;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;

public class Mat2Buffered {
	
	private BufferedImage img;
	
	public Mat2Buffered(Mat m) {
		// Convert opencv.Mat to BufferedImage
		// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		
	    int type = BufferedImage.TYPE_BYTE_GRAY;
	    if ( m.channels() > 1 ) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    }
	    int bufferSize = m.channels()*m.cols()*m.rows();
	    byte [] b = new byte[bufferSize];
	    m.get(0,0,b); // get all the pixels
	    img = new BufferedImage(m.cols(),m.rows(), type);
	    final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
	    System.arraycopy(b, 0, targetPixels, 0, b.length);  
	}

	public void display() {
	    // Display BufferedImage
		
	    ImageIcon icon = new ImageIcon(img);
	    JFrame frame = new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);     
	    JLabel lbl = new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public BufferedImage returnBuffered() {
		return img;
	}
	
	public void display(Rectangle rect) {
	    // Display BufferedImage
		
		Graphics2D draw = img.createGraphics();
		draw.setColor(Color.red);
		draw.drawRect(rect.x, rect.y, rect.height, rect.width);
		
	    ImageIcon icon = new ImageIcon(img);
	    JFrame frame = new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);     
	    JLabel lbl = new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
