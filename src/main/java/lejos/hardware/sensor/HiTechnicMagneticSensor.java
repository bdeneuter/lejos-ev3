package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;


/**
 * <bHiTechnic Magnetic Sensor</b><br>
 * The sensor detects magnetic fields that are present around the front of the sensor in a vertical orientation.
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
 * <td>Magnetic</td>
 * <td>Measures the strength of a vertical magnetic field</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getMagneticMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NMS1035"> Sensor Product page </a>
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
 */public class HiTechnicMagneticSensor extends AnalogSensor implements SensorConstants {;
	
	/**
	 * Create a magnetic sensor on an analog port
	 * 
	 * @param port the analog port
	 */
    public HiTechnicMagneticSensor(AnalogPort port) {
        super(port);
        init();
    }
    
	/**
	 * Create a magnetic sensor on a sensor port
	 * 
	 * @param port the analog port
	 */
    public HiTechnicMagneticSensor(Port port) {
        super(port);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{ new MagneticMode() });
    	port.setTypeAndMode(TYPE_CUSTOM, MODE_RAW);
    }
    
    /**
     * <b>HiTechnic Magnetic sensor, Magnetic mode</b><br>
     * Measures the strength og the vertical magnetic field 
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element containing the strength of the magnetic field around the sensor. 
     * The value is normalized (0-1) where 1 corresponds with the maximum field strength. 
     */
    public SampleProvider getMagneticMode() {
    	return getMode(0);
    }

    private class MagneticMode implements SensorMode {
	@Override
	public int sampleSize() {
		return 1;
	}

	@Override
	public void fetchSample(float[] sample, int offset) {
		sample[offset] = normalize(port.getPin1());
	}

	@Override
	public String getName() {
		return "Magnetic";
	}
    }
}
	
	
