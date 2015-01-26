import java.io.File;
import java.io.IOException;
import java.lang.*;
import java.util.Arrays;

import javax.sound.sampled.UnsupportedAudioFileException;

public class AnalyzeAudio {

	public static void main(String args[]) {
		
		// Open audio
		File file = new File("junk.wav");
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
		int rate = (int) audio.getSampleRate();
		double lengthAudio = (double)nrSamples/(double)rate;
 		System.out.println("Sample Rate: \t\t" + rate);
		System.out.println("Sample count: \t\t" + nrSamples);
		System.out.println("Total time of audio: \t" + lengthAudio + " seconds");

		
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

		// dB per sample
		double counter = 0;
		for (int i = 0; i < nrSamples; i++) {
			double vol =+ -20 * Math.log10(Math.abs(realInput[i]));
			if (vol < 60) {
				counter += 1.0;	
			}
		}
		counter = counter/rate;
		System.out.println("Your volume level was under 60 dB for " + counter
				 + " seconds of the total of " + lengthAudio + " seconds.");


		// FFT per time unit
		int window = 3*rate;
		double[] absoluteVal = new double[window];
		int[] powerfulFreq = new int[(((nrSamples-window)/rate)+1)];
		int loop = 0;
		for (int f = 0; f < (nrSamples - window); f = f+rate) {
			double[] real = Arrays.copyOfRange(realInput, f, (f+window));
			double[] imag = Arrays.copyOfRange(imagInput, f, (f+window));
			Fft.transform(real, imag);
			for (int j = 0; j < real.length ; j++) {
				absoluteVal[j] = Math.sqrt(Math.pow(real[j], 2) + Math.pow(imag[j], 2));
			}
			double max = 0;
			int index = 0;
			for (int h = 1; h < absoluteVal.length; h++) {
				if (absoluteVal[h] > max) {
					max = absoluteVal[h];
					index = h;
				}
			}
			powerfulFreq[loop] = index;
			loop++;
		}

		for (int k = 0; k<powerfulFreq.length; k++) {

			System.out.println(powerfulFreq[k]);
		}

	}
	
}
