package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;

/**
 * <b>HiTechnic EOPD Sensor</b><br>
 * The EOPD or Electro Optical Proximity Detector uses an internal light source to detect the presence of a target or determine changes in distance to a target.
 * 
 * <p>
 * <table border=1>
 * <tr>
 * <th colspan=4>Supported modes</th>
 * </tr>
 * <tr>
 * <th>Mode name</th>
 * <th>Description</th>
 * <th>unit(s)</th>
 * <th>Getter</th>
 * </tr>
 * <tr>
 * <td>Long Distance</td>
 * <td>Measures the relative distance to an object</td>
 * <td>N/A, a normalized value that represents the relative distance to an object. 0 = minimum range, 1 = maximum range. </td>
 * <td> {@link #getLongDistanceMode() }</td>
 * </tr>
 * <tr>
 * <td>Short Distance</td>
 * <td>Measures the relative distance to an object</td>
 * <td>N/A, a normalized value that represents the relative distance to an object. 0 = minimum range, 1 = maximum range. </td>
 * <td> {@link #getShortDistanceMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * 
 * See <a href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NEO1048"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Michael Smith <mdsmitty@gmail.com>
 * 
 */

public class HiTechnicEOPD extends AnalogSensor implements SensorConstants {



    protected static final long SWITCH_DELAY = 10;
    /**
     * @param port NXT sensor port 1-4
     */
    public HiTechnicEOPD (AnalogPort port){
        super(port);
        init();
    }
    
    /**
     * @param port NXT sensor port 1-4
     */
    public HiTechnicEOPD (Port port){
        super(port);
        init();
    }
		
    protected void init() {
    	setModes(new SensorMode[]{ new LongDistanceMode(), new ShortDistanceMode() });
    }
    
	
    /**
     * <b>HiTechnic EOPD sensor, Long distance mode</b><br>
     * Measures the relative distance to a surface.
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element containing the relative distance to a surface in the range of 0 to 1. 
     * Where 0 corresponds to the minimum of the measurement range and 1 corresponds to the maximum of the measyurement range.
     * The measurement range depends on the color and reflectivity of the measured surface. The measurement is more or less linear to the distance for a given surface. 
     */
	public SensorMode getLongDistanceMode() {
		return getMode(0);
	}
	
	
private class LongDistanceMode implements SensorMode {	
	 @Override
	  public int sampleSize() {
	    return 1;
	  }
	  
	  @Override
	  public void fetchSample(float[] sample, int offset) {
	    switchType(TYPE_LIGHT_INACTIVE, SWITCH_DELAY);
	    sample[offset] = (float) Math.sqrt((normalize(port.getPin1())));
	  }

	  @Override
	  public String getName() {
	    return "Long distance";
	  }
}

/**
 * <b>HiTechnic EOPD sensor, Short distance mode</b><br>
 * Measures the relative distance to a surface. This mode is suited for white objects at short distance.
 * 
 * <p>
 * <b>Size and content of the sample</b><br>
 * The sample contains one element containing the relative distance to a surface in the range of 0 to 1. 
 * Where 0 corresponds to the minimum of the measurement range and 1 corresponds to the maximum of the measyurement range.
 * The measurement range depends on the color and reflectivity of the measured surface. The measurement is more or less linear to the distance for a given surface. 
 */	 public SensorMode getShortDistanceMode() {
	    return getMode(1);
	  }

	
	


  public class ShortDistanceMode implements SensorMode {

  @Override
  public int sampleSize() {
    return 1;
  }

  @Override
  public void fetchSample(float[] sample, int offset) {
    switchType(TYPE_LIGHT_ACTIVE, SWITCH_DELAY);
    sample[offset] = (float) Math.sqrt((normalize(port.getPin1())));
  }

  @Override
  public String getName() {
    return "Short distance";
  }

}
}
