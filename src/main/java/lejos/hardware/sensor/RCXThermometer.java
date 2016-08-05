package lejos.hardware.sensor;

import lejos.hardware.port.LegacySensorPort;


/**
 * <b>Lego RCX temperature sensor</b><br>
 * The sensor measures both atmospheric pressure and temperature.
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
 * <td>Temperature</td>
 * <td>Measures temperature</td>
 * <td>Degree Celcius</td>
 * <td> {@link #getTemperatureMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Soren Hilmer
 * 
 */
public class RCXThermometer extends AnalogSensor implements SensorConstants {
    LegacySensorPort port;
    
    /**
     * Create an RCX temperature sensor object attached to the specified port.
     * @param port port, e.g. Port.S1
     */
    public RCXThermometer(LegacySensorPort port)
    {
        super(port);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{ new TemperatureMode() });
    	port.setTypeAndMode(TYPE_TEMPERATURE, MODE_RAW);
    }
    
    /**
     * Return a sample provider in temperature mode
     */
    public SensorMode getTemperatureMode() {
    	return getMode(0);
    }

    private class TemperatureMode implements SensorMode { 
	@Override
	public int sampleSize() {
		return 1;
	}

	@Override
	public void fetchSample(float[] sample, int offset) {
		sample[offset] = (785-NXTRawValue(port.getPin1()))/8.0f +273.15f; // Kelvin
	}

	@Override
	public String getName() {
		return "Temperature";
	}
    }
}
