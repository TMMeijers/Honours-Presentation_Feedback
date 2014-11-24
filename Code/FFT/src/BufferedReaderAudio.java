import java.util.*;
import java.io.*;

public class BufferedReaderAudio {

	public static void main(String[] args) {
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader("junk.wav"));
			String line = br.readLine();
			ArrayList<String> real = new ArrayList<String>();
			while(line != null) {
				String[] numbers = line.split("\\s");
				for (int i = 0; i<numbers.length;i++) {
			    	real.add(numbers[i]);
				}
			    line = br.readLine();
			}
			for (int i = 0; i<real.size(); i++) {
				System.out.println(real.get(i));	
			}
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
