package lejos.hardware.sensor; 

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;


/**
 * <b>HiTechnic NXT IRSeeker V2</b><br>
 * The NXT IRSeeker V2 (Version 2) is a multi-element infrared detector that
 * detects infrared signals from sources such as the HiTechnic IRBall soccer
 * ball, infrared remote controls and sunlight.
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
 * <td>modulated</td>
 * <td>Measures the angle to a source of modulated (1200 Hz square wave) infrared light</td>
 * <td>Degrees</td>
 * <td> {@link #getModulatedMode() }</td>
 * </tr>
 * <tr>
 * <td>Unmodulated</td>
 * <td>Measures the angle to a source of unmodulated infrared light</td>
 * <td>Degrees</td>
 * <td> {@link #getUnmodulatedMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a
 *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NSK1042">
 *      Sensor Product page </a>
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
public class HiTechnicIRSeekerV2 extends I2CSensor 
{
    private static final byte   address   = 0x10;
    private byte[] buf = new byte[1]; 

    public HiTechnicIRSeekerV2(I2CPort port) { 
       super(port, address);
       init();
    } 

    public HiTechnicIRSeekerV2(Port port)  { 
       super(port, address);
       init();
    } 
    
    protected void init() {
    	setModes(new SensorMode[]{ new ModulatedMode(), new UnmodulatedMode() });
    }
	
    /**
     * <b>HiTechnic IR seeker V2, modulated mode</b><br>
     * Measures the angle to a source of a modulated infrared light.
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element containing the angle to the infrared source. The angle is expressed in degrees following the right hand rule. 
     */
	public SensorMode getModulatedMode() {
		return getMode(0);
	}
	
	private class ModulatedMode implements SensorMode {
 	@Override
	public int sampleSize() {
		return 1;
	}

	@Override
	public void fetchSample(float[] sample, int offset) {
		getData(0x49, buf, 1);
		float angle = Float.NaN;
		if (buf[0] > 0) {
			// Convert to angle with zero forward, anti-clockwise positive
			angle = -(buf[0] * 30 - 150);
		}
		sample[offset] = angle;
	}	
	
	@Override
	public String getName() {
		return "Modulated";
	}
	}
	
  /**
   * <b>HiTechnic IR seeker V2, Unmodulated mode</b><br>
   * Measures the angle to a source of unmodulated infrared light
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the angle to the infrared source. The angle is expressed in degrees following the right hand rule. 
   */
	public SensorMode getUnmodulatedMode() {
		return getMode(1);
	}
	
	private class UnmodulatedMode implements SensorMode {
		@Override
		public int sampleSize() {
			return 1;
		}

		@Override
		public void fetchSample(float[] sample, int offset) {
			getData(0x42, buf, 1);
			float angle = Float.NaN;
			if (buf[0] > 0) {
				// Convert to angle with zero forward, anti-clockwise positive
				angle = -(buf[0] * 30 - 150);
			}
			sample[offset] = angle;	
		}

		@Override
		public String getName() {
			return "Unmodulated";
		}		
	}
}
