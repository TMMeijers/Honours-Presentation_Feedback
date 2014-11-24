import java.io.*;

public class AnalyzeAudio {

	static void main(String args[]) {
		
		 WavFile wavFile = WavFile.openWavFile(new File("junk.wav"));
		 wavFile.display();
		
	}
	
}
