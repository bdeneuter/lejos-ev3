package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;
import lejos.robotics.Color;
import lejos.robotics.LampController;


/**
 * <b>LEGO NXT light Sensor</b><br>
 * The NXT light sensor measures light levels of reflected or ambient light.
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
 * <td>Red</td>
 * <td>Measures the light value when illuminated with a red light source.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getRedMode() }</td>
 * </tr>
 * <tr>
 * <td>Ambient</td>
 * <td>Measures the light value of ambient light.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getAmbientMode() }</td>
 * </tr>
 * </table>
 * 
 * <p>
 *  See <a href="http://www.lego.com/en-us/mindstorms/downloads/software/nxt-hdk/"> Mindstorms NXT HDK/SDK </a>
 *  
 */
public class NXTLightSensor extends AnalogSensor implements LampController, SensorConstants
{
    protected static final long SWITCH_DELAY = 10;
	private boolean floodlight = false;

	private class AmbientMode implements SensorMode
	{

        @Override
        public int sampleSize()
        {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset)
        {
            setFloodlight(false);
            sample[offset] = 1.0f - normalize(port.getPin1());
        }

        @Override
        public String getName()
        {
            return "Ambient";
        }
	    
	}
    protected void init()
    {
        setModes(new SensorMode[]{ new RedMode(), new AmbientMode() });        
        setFloodlight(true);
    }
    
    /**
     * Create a light sensor object attached to the specified port.
     * The sensor will be set to floodlight mode, i.e. the LED will be turned on.
     * @param port port, e.g. Port.S1
     */
    public NXTLightSensor(AnalogPort port)
    {
        super(port);
        init();
    }
    
    /**
     * Create a light sensor object attached to the specified port.
     * The sensor will be set to floodlight mode, i.e. the LED will be turned on.
     * @param port port, e.g. Port.S1
     */
    public NXTLightSensor(Port port)
    {
        super(port);
        init();
    }
    
	public void setFloodlight(boolean floodlight)
	{
	        switchType(floodlight ? TYPE_LIGHT_ACTIVE : TYPE_LIGHT_INACTIVE, SWITCH_DELAY);
	        this.floodlight = floodlight;
	}
	
	public boolean setFloodlight(int color) {
		if(color == Color.RED) {
		    setFloodlight(true);
			return true;
		} else if (color == Color.NONE) {
            setFloodlight(false);
			return true;
		} else return false;
	}

	public int getFloodlight() {
		if(this.floodlight == true)
			return Color.RED;
		else
			return Color.NONE;
	}

	public boolean isFloodlightOn() {
		return this.floodlight;
	}

  /**
   * get a sample provider the returns the light value when illuminated with a
   * Red light source.
   * @return the sample provider
   */
	public SensorMode getRedMode()
	{
	    return getMode(0);
	}
	
  /**
   * get a sample provider the returns the light value when illuminated without a
   * light source.
   * @return the sample provider
   */
	public SensorMode getAmbientMode()
	{
	    return getMode(1);
	}
	
	private class RedMode implements SensorMode {
    @Override
    public int sampleSize()
    {
        return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset)
    {
        setFloodlight(true);
        sample[offset] = 1.0f - normalize(port.getPin1());
        
    }

    @Override
    public String getName()
    {
        return "Red";
    }
	}
}
