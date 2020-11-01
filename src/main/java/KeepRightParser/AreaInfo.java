package KeepRightParser;

public class AreaInfo {
	
	public String mAreaName;
	
	public float mMinLon;
	public float mMaxLon;
	public float mMinLat;
	public float mMaxLat;
	
	public long mMinLonIndex;
	public long mMaxLonIndex;
	public long mMinLatIndex;
	public long mMaxLatIndex;
	
	public boolean contains(long lon, long lat) {
		
		if (lon<mMinLonIndex)
			return false;
		
		if (lon>mMaxLonIndex)
			return false;
		
		if (lat<mMinLatIndex)
			return false;
		
		if (lat>mMaxLatIndex)
			return false;
		
		return true;
	}
}
