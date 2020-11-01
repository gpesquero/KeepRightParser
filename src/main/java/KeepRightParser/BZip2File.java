package KeepRightParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class BZip2File {
	
	private BufferedReader mReader=null;
	
	public BZip2File() {
		
	}
	
	public boolean openFile(String fileName) {
		
		try {
			FileInputStream fin = new FileInputStream(fileName);
			
			BufferedInputStream bis = new BufferedInputStream(fin);
		    
			CompressorInputStream input;
		    input = new CompressorStreamFactory().createCompressorInputStream(bis);
		    
		    mReader=new BufferedReader(new InputStreamReader(input));
		    
		} catch (FileNotFoundException e) {
			
			System.out.println("BZip2File Error: "+e.getMessage());
			
			mReader=null;
			
			return false;
		}
		catch (CompressorException e) {
			
			System.out.println("BZip2File Compressor Error: "+e.getMessage());
			
			mReader=null;
			
			return false;
		}
		
		return true;
	}
	
	public String readLine() {
		
		if (mReader==null)
			return null;
		
		String line;
		
		try {
			line=mReader.readLine();
			
		} catch (IOException e) {
			
			System.out.println("BZip2File readLine Error: "+e.getMessage());
			
			line=null;
		}
		
		return line;
	}

}
