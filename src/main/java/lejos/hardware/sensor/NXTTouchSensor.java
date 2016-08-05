package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;


/**
 * <b>NXT Touch sensor</b><br>
 * A sensor that can be pressed like a button. Also works with RCX touch sensors.
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
 * <td>Touch</td>
 * <td>Detects a press of the button</td>
 * <td>boolean</td>
 * <td> {@link #getTouchMode() }</td>
 * </tr>
 * </table>
 * 
 * <p>
 * 
 * See <a href="http://www.lego.com/en-us/mindstorms/downloads/software/nxt-hdk/"> Mindstorms NXT HDK/SDK </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 */
public class NXTTouchSensor extends AnalogSensor implements SensorConstants
{
	
	/**
	 * Create a touch sensor object attached to the specified open port. Note this
	 * port will not be configured. Any configuration od the sensor port must take
	 * place externally.
	 * @param port an open Analog port
	 */
	public NXTTouchSensor(AnalogPort port)
	{
	   super(port);
	   port.setTypeAndMode(TYPE_SWITCH, MODE_RAW);
	   init();
	}

	/**
	 * Create an NXT touch sensor object attached to the specified port.
	 * @param port the port that has the sensor attached
	 */
	public NXTTouchSensor(Port port)
	{
	    super(port);
	    this.port.setTypeAndMode(TYPE_SWITCH, MODE_RAW);	 
	    init();
	}
	
	protected void init() {
	  setModes(new SensorMode[]{new TouchMode()});
	}

  /**
   * get a sample provider that returns an indication of the button being up(0) or down(1)
   * @return the sample provider
   */
	public SensorMode getTouchMode()
	{
	    return getMode(0);
	}
	
	
	private class TouchMode implements SensorMode {
    @Override
    public int sampleSize()
    {
        return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset)
    {
        sample[offset] = (port.getPin1() > EV3SensorConstants.ADC_REF/2f ? 0.0f : 1.0f);
    }

    @Override
    public String getName()
    {
        return "Touch";
    }
	}
}
