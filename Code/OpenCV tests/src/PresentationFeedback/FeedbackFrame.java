package PresentationFeedback;

import javax.swing.*;

import org.opencv.core.Mat;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class FeedbackFrame extends JFrame {
	JLabel postureLabel;
	JLabel gestureLabel;
	JLabel img;

	public FeedbackFrame(String title, Mat org) {
	
	Mat2Buffered convert = new Mat2Buffered(org);
	BufferedImage pic = convert.returnBuffered();
		
    setTitle(title);
    setSize(1200, 480);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Middle of the screen
    
    assert SwingUtilities.isEventDispatchThread();
    setLayout(new FlowLayout(FlowLayout.CENTER));
    postureLabel = new JLabel(String.valueOf("Get ready!"));
    postureLabel.setFont(new Font("Serif", Font.BOLD, 50));
    gestureLabel = new JLabel(String.valueOf("Get ready!"));
    gestureLabel.setFont(new Font("Serif", Font.BOLD, 50));
    
    img = new JLabel(new ImageIcon(pic));
    add(postureLabel);
    add(gestureLabel);
    add(img);
    
    setVisible(true); // This will paint the entire frame
    
  }

	public void updateComponents(double postureResult, double gestureResult, Mat org) {
	  
	int fontSize = 40;
	Mat2Buffered convert = new Mat2Buffered(org);
	BufferedImage pic = convert.returnBuffered();
	  
	assert SwingUtilities.isEventDispatchThread();
	remove(gestureLabel);
	remove(postureLabel);
	remove(img);
	
	if (postureResult < 2) {
		postureLabel = new JLabel("STATIC");   
		postureLabel.setForeground(Color.orange);
		postureLabel.setFont(new Font("Serif", Font.BOLD, fontSize)); 	
	} else if (postureResult < 5) {
		postureLabel = new JLabel("DYNAMIC");   
		postureLabel.setForeground(Color.green);
		postureLabel.setFont(new Font("Serif", Font.BOLD, fontSize)); 
	} else if (postureResult >= 5){
		postureLabel = new JLabel("MOVED");   
		postureLabel.setForeground(Color.red);
		postureLabel.setFont(new Font("Serif", Font.BOLD, fontSize)); 
	}
    if (gestureResult < 100) {
    	gestureLabel = new JLabel("STATIC");   
    	gestureLabel.setForeground(Color.orange);
    	gestureLabel.setFont(new Font("Serif", Font.BOLD, fontSize)); 	
    } else if (gestureResult < 350) {
    	gestureLabel = new JLabel("HAND MOVEMENT");   
    	gestureLabel.setForeground(Color.green);
        gestureLabel.setFont(new Font("Serif", Font.BOLD, fontSize)); 
    } else if (gestureResult >= 350){
    	gestureLabel = new JLabel("TOO MUCH");   
        gestureLabel.setForeground(Color.red);
        gestureLabel.setFont(new Font("Serif", Font.BOLD, fontSize)); 
    }
		
		img = new JLabel(new ImageIcon(pic));
		
		setLayout(new FlowLayout(FlowLayout.CENTER));

		add(postureLabel);
		add(gestureLabel);
		add(img);
		
		revalidate();
		repaint();
	}
}