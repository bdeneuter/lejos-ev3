package lejos.robotics;

/*
 * DEV NOTES: 
 * Raw = hardware values
 * Normalized = sign changed
 * Scaled = converted to % or some other scale
 * Calibrated = user calibrated values
 * TODO: If Detector interface made, move these Javadoc notes to there
 */

/**
 * A platform independent implementation for sensors that can detect white light levels.
 * @author BB modified by RG 11/14
 *
 */
public interface LightDetector {
	
	/**
	 * Returns the calibrated and normalized brightness of the white light detected.
	 * @return A brightness value between 0 and 100%, with 0 = darkness and 100 = intense sunlight
	 */
	public float getLightValue();
	
	
	/**
	 * Returns the normalized value of the brightness of the white light detected, such that
	 * the lowest value is darkness and the highest value is intense bright light.
	 * @return A raw value, between getLow() and getHigh(). Usually 
	 * between 0 and 1.00 but can be anything depending on hardware. low values = dark, high values = bright 
	 */
	public float getNormalizedLightValue();
	
	/**
	 * The highest raw light value this sensor can return from intense bright light.
	 * @return the high value
	 */
	public float getHigh();

	/**
	 * The lowest raw light value this sensor can return in pitch black darkness.
	 * @return the low value
	 */
	public float getLow();
}
