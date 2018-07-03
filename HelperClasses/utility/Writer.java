package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Specialized 
 * */

public class Writer {
	
	private FileWriter writer;
	private boolean append;
	
	//meta is the header info
	public Writer (String path, String file, String [][] meta, boolean append) {
		try {
			writer = new FileWriter ((path + "\\" + file), append);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.append = append;
		
		String toWrite = "";
		
		for (int i = 0; i < meta.length; i++) {
			toWrite += meta[i][0]+"#"+meta[i][1]+",";
		}
		
		if (!append)
			try {
				writer.write(toWrite.substring(0, toWrite.length()-1) + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public Writer (String fileName) {
		try {
			writer = new FileWriter (new File(System.getProperty("user.dir") + "\\" + fileName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeLine(String line) throws IOException {
		
		if (append) {
			appendLine(line);
		}
		
		else {
			noAppendLine(line);
		}
		
	}
	
	private void noAppendLine (String line) {
		try {
			writer.write(line+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void appendLine (String line) throws IOException {
		writer.append(line+"\n");
	}
	
	public void close () throws IOException {
		writer.close();
	}
	
}
