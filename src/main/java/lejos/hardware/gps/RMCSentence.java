package lejos.hardware.gps;

import java.util.NoSuchElementException;


/**
 * RMC is a Class designed to manage RMC Sentences from a NMEA GPS Receiver
 * 
 * RMC - NMEA has its own version of essential gps pvt (position, velocity, time) data. It is called RMC, The Recommended Minimum, which will look similar to:
 * 
 * $GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A
 * 
 * Where:
 *      RMC          Recommended Minimum sentence C
 *      123519       Fix taken at 12:35:19 UTC
 *      A            Status A=active or V=Void.
 *      4807.038,N   Latitude 48 deg 07.038' N
 *      01131.000,E  Longitude 11 deg 31.000' E
 *      022.4        Speed over the ground in knots
 *      084.4        Track angle in degrees True
 *      230394       Date - 23rd of March 1994
 *      003.1,W      Magnetic Variation
 *      *6A          The checksum data, always begins with *
 * 
 * @author Juan Antonio Brenha Moral
 * 
 * Added changes by Alan M Gilkes - Nov 12th 2014 - Lawrie Griffiths
 * 
 */

public class RMCSentence extends NMEASentence {
	
	//RMC Sentence
	private String nmeaHeader = "";
	private int dateTimeOfFix = 0;
	private final int DATETIMELENGTH = 6;
	private String status = "";
	private final String ACTIVE = "A";
	private final String VOID = "V";
	private double latitude = 0;
	private String latitudeDirection = "";
	private double longitude = 0;
	private String longitudeDirection = "";
	private final float KNOT = 1.852f;
	private float groundSpeed;//In knots
	private float compassDegrees;
	private int dateOfFix = 0;
	private float magneticVariation = 0f;
	private String magneticVariationLetter = "";

	private float speed;//In Kilometers per hour

	//Header
	public static final String HEADER = "$GPRMC";
	
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
		return nmeaHeader;
	}
	
	public String getStatus(){
		return status;
	}
	
	/**
	 * Get Latitude
	 * 
	 */
	public double getLatitude(){
		return latitude;  
	}

	/**
	 * Get Longitude
	 * 
	 * @return the longitude
	 */
	public double getLongitude(){
		return longitude;
	}

	/**
	 * Get Speed in Kilometers
	 * 
	 * @return the speed
	 */
	public float getSpeed(){
		return speed;  
	}

	/**
	 * Get time in integer format
	 * 
	 * @return the time
	 */
	public int getTime(){
		return dateTimeOfFix;
	}
	
	/**
	 * Get date in integer format
	 * 
	 * @return the date
	 */
	public int getDate(){
		return dateOfFix;
	}

	/**
	 * Return compass value from GPS
	 * 
	 * @return the compass heading
	 */
	public float getCompassDegrees(){
		return compassDegrees;
	}
	
	/**
	 * Parse a RMC Sentence
	 * 
	 * $GPRMC,081836,A,3751.65,S,14507.36,E,000.0,360.0,130998,011.3,E*62
	 */
	public void parse (String sentence) {
		
		String[] parts = sentence.split(",");
		
		try{		
			//Processing RMC data
			
			nmeaHeader = parts[0];//$GPRMC
		
			if (parts[1].length() == 0) {
				dateTimeOfFix = 0;
			} else {
				dateTimeOfFix = Math.round(Float.parseFloat(parts[1]));
			}
			
			if (parts[2].equals(ACTIVE)) {
				status = ACTIVE;
			} else {
				status = VOID;
			}
			
			if (isNumeric(parts[3])) {
				latitude = degreesMinToDegreesDbl(parts[3],NMEASentence.LATITUDE);
			} else {
				latitude = 0f;
			}
			
			latitudeDirection = parts[4];
			
			if (isNumeric(parts[5])) {
				longitude = degreesMinToDegreesDbl(parts[5],NMEASentence.LONGITUDE);
			} else {
				longitude = 0f;
			}

			longitudeDirection = parts[6];
			
			if (longitudeDirection.equals("E") == false) {
				longitude = -longitude;
			}
			
			if (latitudeDirection.equals("N") == false) {
				latitude = -latitude;
			}
			
			if (parts[7].length() == 0) {
				groundSpeed = 0f;
				speed = 0f;
			} else {
				groundSpeed = Float.parseFloat(parts[7]);
				
				//Speed
				if (groundSpeed > 0) {
					// km/h = knots * 1.852
					speed = groundSpeed * KNOT;
				}
				
				// A negative speed doesn't make sense.
				if (speed < 0) {
					speed = 0f;
				}
			}
			
			if (parts[8].length() == 0) {
				compassDegrees = 0;
			} else {
				compassDegrees = Float.parseFloat(parts[8]);
			}
			
			if (parts[9].length() == 0) {
				dateOfFix = 0;
			} else{
				dateOfFix = Math.round(Float.parseFloat(parts[9]));
			}

			if (parts[10].length() == 0) {
				magneticVariation = 0;
			} else{
				magneticVariation = Float.parseFloat(parts[10]);
			}
			
			if (parts[11].length() == 0) {
				magneticVariationLetter = "";
			} else{
				magneticVariationLetter = parts[11];
			}
		} catch(NoSuchElementException e) {
			//System.err.println("RMCSentence: NoSuchElementException");
		} catch(NumberFormatException e) {
		 	//System.err.println("RMCSentence: NumberFormatException");
		} catch(Exception e) {
			//System.err.println("RMCSentence: Exception");
		}
	}//End Parse

}
