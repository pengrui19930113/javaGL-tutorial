package toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Res {
	public static InputStream get(String baseMainLoopPath){
//		return Res.class.getResourceAsStream(baseMainLoopPath); // /shaders/fragmentShader.txt
		return ClassLoader.getSystemResourceAsStream(baseMainLoopPath); //shaders/fragmentShader.txt
	}
	
	public static void main(String args[]) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(Res.get("shaders/fragmentShader.txt")));
		String line;
		while(null != (line = br.readLine())) {
			System.out.println(line);
		}
	}
}
