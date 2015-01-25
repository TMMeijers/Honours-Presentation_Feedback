import javax.swing.*;

import org.opencv.core.Mat;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class PostureFrame extends JFrame
{
	JLabel label;
	JLabel img;
  /**
   * Creates the frame example and makes it visible
   * 
   * @throws AssertionError if not called on the EDT
   */
  public PostureFrame(String title, Mat org) {
	Mat2Buffered convert = new Mat2Buffered(org);
	BufferedImage pic = convert.returnBuffered();
		
    setTitle(title);
    setSize(640, 600);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Middle of the screen
    
    assert SwingUtilities.isEventDispatchThread();
    setLayout(new FlowLayout(FlowLayout.CENTER));
    label = new JLabel(String.valueOf("Get ready!"));
    label.setFont(new Font("Serif", Font.BOLD, 50));
    
    img = new JLabel(new ImageIcon(pic));
    add(label);
    add(img);
    
    setVisible(true); // This will paint the entire frame
    
  }
 
  /**
   * Prepares the components for display. 
   * 
   * @throws AssertionError if not called on the EDT
   */
  public void updateComponents(double info, Mat org) {
	  
	  int fontSize = 50;
	Mat2Buffered convert = new Mat2Buffered(org);
	BufferedImage pic = convert.returnBuffered();
	  
    assert SwingUtilities.isEventDispatchThread();
    remove(label);
    remove(img);
    
    if (info < 2) {
        label = new JLabel("STATIC");   
        label.setForeground(Color.orange);
        label.setFont(new Font("Serif", Font.BOLD, fontSize)); 	
    } else if (info < 5) {
        label = new JLabel("DYNAMIC");   
        label.setForeground(Color.green);
        label.setFont(new Font("Serif", Font.BOLD, fontSize)); 
    } else {
        label = new JLabel("MOVED");   
        label.setForeground(Color.red);
        label.setFont(new Font("Serif", Font.BOLD, fontSize)); 
    }
    
    img = new JLabel(new ImageIcon(pic));
    
    setLayout(new FlowLayout(FlowLayout.CENTER));
    
    add(label);
    add(img);
    
    revalidate();
    repaint();
  }
}