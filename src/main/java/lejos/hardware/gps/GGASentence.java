package lejos.hardware.gps;

import java.util.NoSuchElementException;

/**
 * This class has been designed to manage a GGA Sentence
 * 
 * GGA - essential fix data which provide 3D location and accuracy data.
 * 
 * $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47
 * 
 * Where:
 *      GGA          Global Positioning System Fix Data
 *      123519       Fix taken at 12:35:19 UTC
 *      4807.038,N   Latitude 48 deg 07.038' N
 *      01131.000,E  Longitude 11 deg 31.000' E
 *      1            Fix quality: 0 = invalid
 *                                1 = GPS fix (SPS)
 *                                2 = DGPS fix
 *                                3 = PPS fix
 * 			       4 = Real Time Kinematic
 * 			       5 = Float RTK
 *                                6 = estimated (dead reckoning) (2.3 feature)
 * 			       7 = Manual input mode
 * 			       8 = Simulation mode
 *      08           Number of satellites being tracked
 *      0.9          Horizontal dilution of position
 *      545.4,M      Altitude, Meters, above mean sea level
 *      46.9,M       Height of geoid (mean sea level) above WGS84
 *                       ellipsoid
 *      (empty field) time in seconds since last DGPS update
 *      (empty field) DGPS station ID number
 *      *47          the checksum data, always begins with *
 * 
 * @author Juan Antonio Brenha Moral
 * 
 * Added changes by Alan M Gilkes - Nov 12th 2014 - Lawrie Griffiths
 * 
 */

public class GGASentence extends NMEASentence {
	
	private double latitude = 0;
	private double longitude = 0;
	private String nmeaHeader = "";
	private int dateTimeOfFix = 0;
	private char latitudeDirection;
	private char longitudeDirection;
	private int quality = 0;
	private int satellitesTracked = 0;
	private float hdop = 0;
	private float altitude = 0;
	private String altitudeUnits;
	private float geoidalSeparation;
	private String geoidalSeparationUnit;

	//Header
	public static final String HEADER = "$GPGGA";

	/**
	 * Any GPS Receiver gives Lat/Lon data in the following way:
	 * 
	 * http://www.gpsinformation.org/dale/nmea.htm
	 * http://www.teletype.com/pages/support/Documentation/RMC_log_info.htm
	 * 
	 * 4807.038,N   Latitude 48 deg 07.038' N
	 * 01131.000,E  Longitude 11 deg 31.000' E
	 * 
	 * This data is necessary to convert to Decimal Degrees.
	 * 
	 * Latitude values has the range: -90 <-> 90
	 * Longitude values has the range: -180 <-> 180
	 * 
	 * @param DD_MM
	 * @param CoordinateType
	 * @return the degrees
	 */
	protected double degreesMinToDegreesDbl(String DD_MM,int CoordinateType) {//throws NumberFormatException
		// This methods accept all strings of the format
		// DDDMM.MMMM
		// DDDMM
		// MM.MMMM
		// MM
		
		// check first character, rest is checked by parseInt/parseFloat
		int len = DD_MM.length();
		if (len <= 0 || DD_MM.charAt(0) == '-')
			throw new NumberFormatException();
		
		int dotPosition = DD_MM.indexOf('.');
		if (dotPosition < 0)
			dotPosition = len;
		
		int degrees;
		double minutes;		
		if (dotPosition > 2)
		{
			degrees = Integer.parseInt(DD_MM.substring(0, dotPosition-2));
			// check first character of minutes since '-' is not allowed
			// rest is checked by parseFloat
			if (DD_MM.charAt(dotPosition-2) == '-')
				throw new NumberFormatException();
			minutes = Double.parseDouble(DD_MM.substring(dotPosition-2));
		}
		else
		{
			degrees = 0;
			minutes = Double.parseDouble(DD_MM);
		}
		
//		if(CoordenateType == NMEASentence.LATITUDE){
//			if((degrees >=0) && (degrees <=90)){
//				throw new NumberFormatException();
//			}
//		}else{
//			if((degrees >=0) && (degrees <=180)){
//				throw new NumberFormatException();
//			}
//		}
		
		return (double)(degrees + minutes * (double)(1.0 / 60.0));
	}
	
	/*
	 * GETTERS & SETTERS
	 */
	
	/**
	 * Returns the NMEA header for this sentence.
	 */
	@Override
	public String getHeader() {
		return nmeaHeader;//The header actually read in when the sentence was parsed
	}

	/**
	 * Get Latitude
	 * 
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Get Latitude Direction
	 * 
	 * @return the latitude direction
	 */
	public char getLatitudeDirection(){
		return latitudeDirection;
	}
	
	/**
	 * Get Longitude
	 * 
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Get Longitude Direction
	 * @return the longitude direction
	 */
	public char getLongitudeDirection(){
		return longitudeDirection;
	}
	
	/**
	 * Get Altitude
	 * 
	 * @return the altitude
	 */
	public float getAltitude(){
		return altitude;
	}

	/**
	 * Returns the last time stamp retrieved from a satellite
	 * 
	 * @return The time as a UTC integer. 123519 = 12:35:19 UTC
	 */
	public int getTime(){
		return dateTimeOfFix;
	}
	
	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesTracked() {
		return satellitesTracked;
	}

	/**
	 * Get GPS Quality Data
	 * 
	 * @return the fix quality
	 */
	public int getFixQuality(){
		return quality;
	}

	/**
	 * Get Horizontal Dilution of Precision (HDOP)
	 * 
	 * @return the hdop
	 */
	public float getHDOP(){
		return hdop;
	}

	
	/**
	 * Method used to parse a GGA Sentence
	 */
	public void parse(String sentence) {
		
		String[] parts = sentence.split(",");

		try{
			//Processing GGA data
			
			nmeaHeader = parts[0];

			if (parts[1].length() == 0) {
				dateTimeOfFix = 0;
			} else {
				dateTimeOfFix = Math.round(Float.parseFloat(parts[1]));
			}
						
			if (isNumeric(parts[2])) {
				latitude = degreesMinToDegreesDbl(parts[2],NMEASentence.LATITUDE);
			} else {
				latitude = 0.0;
			}
			
			latitudeDirection = parts[3].charAt(0);
			
			if (isNumeric(parts[4])) {
				longitude = degreesMinToDegreesDbl(parts[4],NMEASentence.LONGITUDE);
			} else {
				longitude = 0.0;
			}

			longitudeDirection = parts[5].charAt(0);
			
			if (longitudeDirection != 'E') {
				longitude = -longitude;
			}
			
			if (latitudeDirection != 'N') {
				latitude = -latitude;
			}

			if (parts[6].length() == 0) {
				quality = 0;
			} else{
				quality = Math.round(Float.parseFloat(parts[6]));//Fix quality
			}

			if (parts[7].length() == 0) {
				satellitesTracked = 0;
			} else{
				satellitesTracked = Math.round(Float.parseFloat(parts[7]));
			}

			if (parts[8].length() == 0) {
				hdop = 0;
			} else {
				hdop = Float.parseFloat(parts[8]);//Horizontal dilution of position
			}
			
			if (isNumeric(parts[9])) {
				altitude = Float.parseFloat(parts[9]);
			} else{
				altitude = 0f;
			}	
		} catch(NoSuchElementException e) {
			//System.err.println("GGASentence: NoSuchElementException");
		} catch(NumberFormatException e) {
			//System.err.println("GGASentence: NumberFormatException");
		} catch(Exception e){
			//System.err.println("GGASentence: Exception");
		}
	}//End parse

}
