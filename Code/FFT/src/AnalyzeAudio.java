import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class AnalyzeAudio {

	static void main(String args[]) {
		
		File file = new File("D:/Documents and files/KI/BSc KI/2014-2015/Honours - Presentation Feedback/Code/FFT/src/junk.wav");
		
		AudioSampleReader audio = null;
		try {
			audio = new AudioSampleReader(file);
		} catch (UnsupportedAudioFileException e) {
			System.out.println("Unsupported audio format");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long nrSamples = audio.getSampleCount();

		System.out.println("File specifications: \t" + audio.getFormat());
		System.out.println("Sample count: \t\t" + nrSamples);
		
		long begin = 0;
		double[] realInput = new double[(int) nrSamples];
		
		try {
			audio.getInterleavedSamples(begin, nrSamples, realInput);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}