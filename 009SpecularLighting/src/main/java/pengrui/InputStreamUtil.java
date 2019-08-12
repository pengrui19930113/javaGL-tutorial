package pengrui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamUtil {
	
	private static final String RL = "\n";
	
	public static String readStringFromInputStream(InputStream is) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while(null!=(line = br.readLine())) {
				sb.append(line).append(RL);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return sb.toString();
	}
}
