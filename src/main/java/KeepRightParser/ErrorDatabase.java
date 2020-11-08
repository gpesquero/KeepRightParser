package KeepRightParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ErrorDatabase {
	
	Connection mConn=null;
	
	static final String DB_URL_PREFIX = "jdbc:sqlite:";	
	
	private PreparedStatement mPrepStmtInsertError=null;
	
	private String mSqlInsertError="INSERT INTO errors(error_id, error_name, lon1, lon2, lat1, lat2, msg_id) VALUES(?,?,?,?,?,?,?)";
	
	ArrayList<AreaInfo> mAreas=null;
	
	private long mCount=0;
	
	public ErrorDatabase() {		
	}

	public boolean createDatabase(String fileName, ArrayList<AreaInfo> areas) {
		
		mAreas=areas;
		
		Statement stmt=null;
		String sql=null;
		
		try {
			
			// Open a connection
			System.out.println("Creating SQLite database <"+fileName+">: ");
			
			System.out.print(" - Establishing SQLite Connection... ");
			mConn = DriverManager.getConnection(DB_URL_PREFIX+fileName);
			System.out.println("Ok!");
			
			stmt = mConn.createStatement();
			
			sql = "DROP TABLE IF EXISTS errors";
			System.out.print(" - Deleting table <errors>... ");
			stmt.executeUpdate(sql);
		    System.out.println("Ok!");
		    
		    sql = "CREATE TABLE errors (\n"
		    		+ " error_id TEXT,\n"
	                + " error_name TEXT,\n"
	                + "	lon1 INTEGER,\n"
	                + "	lon2 INTEGER,\n"
	                + "	lat1 INTEGER,\n"
	                + "	lat2 INTEGER,\n"
	                + " msg_id TEXT"
	                + ");";
		    
			System.out.print(" - Create table <errors>... ");
			stmt.executeUpdate(sql);
		    System.out.println("Ok!");
		    
		    sql = "DROP TABLE IF EXISTS info";
			System.out.print(" - Deleting table <info>... ");
			stmt.executeUpdate(sql);
		    System.out.println("Ok!");
		    
		    sql = "CREATE TABLE info (\n"
		    		+ " info_key TEXT,\n"
	                + " info_value TEXT"
	                + ");";
		    
			System.out.print(" - Create table <info>... ");
			stmt.executeUpdate(sql);
		    System.out.println("Ok!");
		    
		    mConn.setAutoCommit(false);
		}
		catch(SQLException se) {
			
			System.out.println("SQL Error: "+se.getMessage());
			
			mConn=null;
			
			return false;
		}
		
		try {
			mPrepStmtInsertError=mConn.prepareStatement(mSqlInsertError);
			
		} catch (SQLException e) {
			
			System.out.println("SQL Error prepareStatement(): "+e.getMessage());
		}
		
		return true;
	}
	
	public boolean processLine(String line) {
		
		if (line.startsWith("schema")) {
			return false;
		}
		
		int startPos=0;
		int endPos=line.indexOf('\t', startPos);
		String schemaString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String errorIdString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String errorTypeString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String errorNameString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String objectTypeString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String objectIdString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String stateString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String firstOccurrenceString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String lastCheckedString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String objectTimestampString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String userNameString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String latString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String lonString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String commentString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		//String commentTimestampString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String msgIdString=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String txt1String=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String txt2String=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String txt3String=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		endPos=line.indexOf('\t', startPos);
		String txt4String=line.substring(startPos, endPos);
		
		startPos=endPos+1;
		String txt5String=line.substring(startPos);
		
		///////////////////////////////////////////////////////////////////
		
		int lon1, lon2;
		int lat1, lat2;
		
		String fullErrorIdString=schemaString+"-"+errorIdString;
		
		long lon=Long.parseLong(lonString);
		lon1=(int)(lon/100000);
		lon2=(int)(lon-lon1*100000);
		
		long lat=Long.parseLong(latString);
		lat1=(int)(lat/100000);
		lat2=(int)(lat-lat1*100000);
		
		boolean add=false;
		
		if (mAreas==null) {
			
			// There are no areas defined
			// Include data
			add=true;
		}
		else {
			
			// Check if data is included in one area
			
			for(int i=0; i<mAreas.size(); i++) {
				
				AreaInfo area=mAreas.get(i);
				
				if (area.contains(lon, lat)) {
					
					// This area contains the position
					add=true;
					
					break;
				}				
			}
		}
		
		if (!add) {
			
			// Data shall not be added
			return false;
		}
		
		String newMsgIdString=msgIdString;
		
		newMsgIdString=newMsgIdString.replace("$1", txt1String);
		newMsgIdString=newMsgIdString.replace("$2", txt2String);
		newMsgIdString=newMsgIdString.replace("$3", txt3String);
		newMsgIdString=newMsgIdString.replace("$4", txt4String);
		newMsgIdString=newMsgIdString.replace("$5", txt5String);
		
		newMsgIdString=newMsgIdString.replace("&quot;", "'");
		
		try {
	        	
			mPrepStmtInsertError.setString(1, fullErrorIdString);
	        mPrepStmtInsertError.setString(2, errorNameString);
	        mPrepStmtInsertError.setInt(3, lon1);
	        mPrepStmtInsertError.setInt(4, lon2);
	        mPrepStmtInsertError.setInt(5, lat1);
	        mPrepStmtInsertError.setInt(6, lat2);
	        mPrepStmtInsertError.setString(7, newMsgIdString);
	        mPrepStmtInsertError.executeUpdate();
	    }
        catch (SQLException e) {
        	
            System.out.println("addError() Error: "+e.getMessage());
            
            return false;
        }
		
		mCount++;
		
		return true;
	}
	
	public void closeDatabase() {
		
		Instant start=Instant.now();
        
        try {
        	
        	System.out.print("Database commit()... ");
			
        	mConn.commit();
			
		} catch (SQLException e) {
			
			System.out.println("Failed(): "+e.getMessage());
		}
        
        Instant end=Instant.now();
        
        String text=String.format("Ok!! (Commit took "+Duration.between(start, end).toSeconds()+" seconds)");
        
        System.out.println(text);
	}
	
	public long getCount() {
		
		return mCount;
	}
	
	public void saveInfo(String fileTimeString) {
		
		addInfo("DB Version", KeepRightParser.APP_VERSION);
		addInfo("Date-Time", fileTimeString);
		addInfo("Error Count", String.format(Locale.US, "%,d", mCount));
		
		if (mAreas==null) {
			
			return;
		}
		
		for(int i=0; i<mAreas.size(); i++) {
			
			AreaInfo area=mAreas.get(i);
			
			addInfo("Area #"+i+" Name", area.mAreaName);
		}		
	}
	
	private void addInfo(String key, String value) {
		
		System.out.println("addInfo(): key='"+key+"', value='"+value+"'");
		
		String sqlInsertInfo="INSERT INTO info(info_key, info_value) VALUES(?,?)";
		
		PreparedStatement stmtInsertInfo=null;
		
		try {
			stmtInsertInfo=mConn.prepareStatement(sqlInsertInfo);
			
			stmtInsertInfo.setString(1, key);
			stmtInsertInfo.setString(2, value);
			stmtInsertInfo.executeUpdate();
			
		} catch (SQLException e) {
			
			System.out.println("addInfo() SQL Error: "+e.getMessage());
		}
		
		
		
	}
}
