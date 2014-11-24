import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class AnalyzeAudio {

	public static void main(String args[]) {
		
		// Open audio
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
		
		// Print format of audio
		int nrSamples = (int) audio.getSampleCount();
		System.out.println("File specifications: \t" + audio.getFormat());
		System.out.println("Sample count: \t\t" + nrSamples);
		
		// Initalize values and arrays for in/output
		long begin = 0;
		double[] realInput = new double[nrSamples];
		double[] imagInput = new double[nrSamples];

		// Convert audio to doubles
		try {
			audio.getInterleavedSamples(begin, nrSamples, realInput);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// FFT
		Fft.transform(realInput, imagInput);
		
		for (double i : realInput) {
			System.out.println(i);
		}
	}
	
}