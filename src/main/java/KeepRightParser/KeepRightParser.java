package KeepRightParser;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class KeepRightParser {
	
	final static String APP_VERSION = "1.02";

	public static void main(String[] args) {
		
		System.out.println("Starting KeepRightParser (v"+APP_VERSION+")...");
		
		Instant startTime=Instant.now();
				
		if (args.length<1) {
			
			System.out.println("Missing <input_bz2_file> argument. Quitting...");
			
			return;
		}
		
		String inputFileName=args[0];
		
		BZip2File inputFile=new BZip2File();
		
		if (!inputFile.openFile(inputFileName)) {
			
			System.out.println("Error in inputFile.openFile(). Quitting...");
			
			return;
		}
		
		System.out.println("InputFile <"+inputFileName+"> opened Ok!!");
		
		// List of areas
		ArrayList<AreaInfo> areas=null;
		
		// Open 'area' definition file...
		
		if (args.length>1) {
			
			// There's another argument. Check if it's an area file...
			
			String areaFileName=args[1];
			
			AreaFile areaFile = new AreaFile();
			
			if (!areaFile.openFile(areaFileName)) {
				
				System.out.println("Error in areaFile.openFile(). Quitting...");
				
				return;
			}
			
			System.out.println("AreaFile <"+areaFileName+"> opened Ok!!");
			
			if (!areaFile.parseFile()) {
				
				System.out.println("Error in areaFile.parseFile(). Quitting...");
				
				return;
			}
			
			areas = areaFile.getAreas();
			
			System.out.println("Found "+areas.size()+" areas");
			
			int num=1;
			
			Iterator<AreaInfo> iter=areas.iterator();
			
			while(iter.hasNext()) {
				
				AreaInfo area=iter.next();
				
				System.out.print("Area #"+num+", ");
				System.out.print("areaName="+area.mAreaName+", ");
				System.out.print("minLon="+area.mMinLon+", ");
				System.out.print("maxLon="+area.mMaxLon+", ");
				System.out.print("minLan="+area.mMinLat+", ");
				System.out.print("maxLat="+area.mMaxLat+"\n");
				
				num++;
			}
		}
		
		// Create fullDatabase
		
		String fullOutputFileName=inputFileName.substring(0, inputFileName.lastIndexOf('.'));
		
		fullOutputFileName+=".full.db";
		
		ErrorDatabase fullDatabase=new ErrorDatabase();
		
		if (!fullDatabase.createDatabase(fullOutputFileName, null)) {
			
			System.out.println("Error in fullDatabase.createDatabase(). Quitting...");
			
			return;
		}
		
		ErrorDatabase areaDatabase=null;
		
		if (areas!=null) {
			
			// Create areaDatabase
		
			String areaOutputFileName=inputFileName.substring(0, inputFileName.lastIndexOf('.'));
		
			areaOutputFileName+=".area.db";
		
			areaDatabase=new ErrorDatabase();
			
			if (!areaDatabase.createDatabase(areaOutputFileName, areas)) {
				
				return;
			}
		}
		
	    String line;
	    
	    int lineCount=0;
	    
	    long prevTime=System.currentTimeMillis();
	    
	    while((line=inputFile.readLine())!=null) {
	    	
	    	lineCount++;
	    	
	    	if ((System.currentTimeMillis()-prevTime)>5000) {
	    		
	    		String text=String.format(Locale.US, "Read %,d lines", lineCount);
	    		
	    		System.out.println(text);
	    		
	    		prevTime=System.currentTimeMillis();
	    	}
	    	
	    	fullDatabase.processLine(line);
	    	
	    	if (areaDatabase!=null) {
	    		
	    		areaDatabase.processLine(line);
	    	}
	    	
	    	/*
	    	if (lineCount > 100000)
	    		break;
	    	*/
	    }
	    
	    fullDatabase.saveInfo(inputFile.mFileDateString);
	    
	    fullDatabase.closeDatabase();
	    
	    if (areaDatabase!=null) {
	    	
	    	areaDatabase.saveInfo(inputFile.mFileDateString);
	    	
	    	areaDatabase.closeDatabase();
	    }
		    
	    Duration totalElapsedTime=Duration.between(startTime, Instant.now());
	    
	    System.out.println("Read a total of "+
	    		String.format(Locale.US, "%,d", lineCount)+" lines in "+
	    		totalElapsedTime.toMinutes()+" min. and "+
	    		totalElapsedTime.toSecondsPart()+" secs.");
	    
	    if (areaDatabase!=null) {
	    	
	    	String text=String.format(Locale.US, "Area Database count=%,d",
	    			areaDatabase.getCount());
		    
		    if (lineCount>0) {
		    	
		    	text+=String.format(" (%.1f%%)", 100.0*areaDatabase.getCount()/lineCount);
		    }
		    
		    System.out.println(text);
	    }
	    
	    System.out.println("KeepRightParser finished...");
	}

}
