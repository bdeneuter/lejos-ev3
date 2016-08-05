package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;


/**
 * <b>HiTechnic NXT Gyro Sensor</b><br>
 * The NXT Gyro Sensor contains a single axis gyroscopic sensor that detects rotation.
 * 
 * <p style="color:red;">
 * The code for this sensor has not been tested. Please report test results to
 * the <A href="http://www.lejos.org/forum/"> leJOS forum</a>.
 * </p>
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
 * <td>Rate</td>
 * <td>The Rate mode measures the angular speed of the sensor over a single axis</td>
 * <td>Degrees/second</td>
 * <td> {@link #getRateMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NGY1044"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Lawrie Griffiths
 * 
 */
public class HiTechnicGyro extends AnalogSensor implements SensorConstants {
	private static final float TO_SI=-1;
	private float zero = 600f;
	
    /**
     * Supports the SampleProvider interface. <br>
	 * The sensor measures the angular velocity in degrees per second. 
	 * A positive rate indicates a counterclockwise rotation. A negative rate indicates a clockwise rotation.
	 * 
     * @param port the Analog port
     */
	public HiTechnicGyro(AnalogPort port) {
		super(port);
	    init();
	}
	
	/**
     * Supports the SampleProvider interface. <br>
	 * The sensor measures the angular velocity in degrees per second. 
	 * A positive rate indicates a counterclockwise rotation. A negative rate indicates a clockwise rotation.
	 * 
	 * @param port the Sensor port
	 */
    public HiTechnicGyro(Port port) {
        super(port);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{ new RateMode() });
    	port.setTypeAndMode(TYPE_CUSTOM, MODE_RAW);
    }
    
    /**
     * <b>HiTechnic Gyro sensor, Rate mode</b><br>
     * The Rate mode measures the angular speed of the sensor over three axes
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element giving the angular speed (in degrees/second) of the sensor over its vertical axis (Z-axis). 
     * 
     * <p>
     * */
    public SensorMode getRateMode() {
      return getMode(0);
    }

    private class RateMode implements SensorMode {
	@Override
	public int sampleSize() {
		return 1;
	}

	@Override
	public void fetchSample(float[] sample, int offset) {
		sample[offset] = ((float) NXTRawValue(port.getPin1()) - zero) * TO_SI;
	}

	@Override
	public String getName() {
		return "Rate";
	}
    }
}
