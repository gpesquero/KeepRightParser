package KeepRightParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AreaFile {
	
	private BufferedReader mReader=null;
	
	private ArrayList<AreaInfo> mAreas=new ArrayList<AreaInfo>(); 
	
	AreaFile() {
		
	}
	
	public boolean openFile(String areaFileName) {
		
		try {
			FileInputStream fin = new FileInputStream(areaFileName);
			
			mReader=new BufferedReader(new InputStreamReader(fin));
		    
		} catch (FileNotFoundException e) {
			
			System.out.println("AreaFile Error: "+e.getMessage());
			
			mReader=null;
			
			return false;
		}
		
		return true;
	}
	
	public boolean parseFile() {
		
		if (mReader==null) {
			
			return false;
		}
		
		String line;
		
		try {
			
			while((line=mReader.readLine())!=null) {
				
				AreaInfo area=parseLine(line);
				
				if (area!=null) {
					
					mAreas.add(area);
				}		
			}
		}
		catch (IOException e) {
			
			System.out.println("AreaFile readLine() Error: "+e.getMessage());
			
			return false;
		}
		
		return true;		
	}
	
	private AreaInfo parseLine(String line) {
		
		if (line==null)
			return null;
		
		if (line.isEmpty())
			return null;
		
		if (!line.startsWith("**"))
			return null;
		
		AreaInfo areaInfo=new AreaInfo();
		
		String minLon, maxLon;
		String minLat, maxLat;
		
		try {
			
			int startPos, endPos;
		
			startPos=2;
			endPos=line.indexOf(",", startPos);
			areaInfo.mAreaName=line.substring(startPos, endPos).trim();
			
			startPos=endPos+1;
			endPos=line.indexOf(",", startPos);
			minLon=line.substring(startPos, endPos).trim();
			
			startPos=endPos+1;
			endPos=line.indexOf(",", startPos);
			maxLon=line.substring(startPos, endPos).trim();
			
			startPos=endPos+1;
			endPos=line.indexOf(",", startPos);
			minLat=line.substring(startPos, endPos).trim();
			
			startPos=endPos+1;
			maxLat=line.substring(startPos).trim();
		}
		catch (StringIndexOutOfBoundsException e) {
			
			System.out.println("Error StringIndexOutOfBoundsException while parsing area line <"+line+">");
			
			return null;
		}
		
		try {
			
			areaInfo.mMinLon=Float.parseFloat(minLon);
			areaInfo.mMaxLon=Float.parseFloat(maxLon);
			areaInfo.mMinLat=Float.parseFloat(minLat);
			areaInfo.mMaxLat=Float.parseFloat(maxLat);
		}
		catch(NumberFormatException e) {
			
			System.out.println("Error NumberFormatException while parsing area line <"+line+">");
			
			return null;
		}
		
		areaInfo.mMinLonIndex=Math.round(areaInfo.mMinLon*1e7);
		areaInfo.mMaxLonIndex=Math.round(areaInfo.mMaxLon*1e7);
		areaInfo.mMinLatIndex=Math.round(areaInfo.mMinLat*1e7);
		areaInfo.mMaxLatIndex=Math.round(areaInfo.mMaxLat*1e7);
		
		return areaInfo;
	}
	
	public ArrayList<AreaInfo> getAreas() {
		
		return mAreas;
	}
}
