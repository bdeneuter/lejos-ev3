package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.utility.EndianTools;


/**
 * <b>Mindsensors acceleration (tilt) sensor ACCL-Nx-v2/v3</b><br>
 * The Mindsensors Accelerometer Sensor measures acceleration or tilt in three
 * axes. 
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
 * <td>Acceleration</td>
 * <td>Measures acceleration over three axes.</td>
 * <td>meter / second<sup>2</sup></td>
 * <td> {@link #getAccelerationMode() }</td>
 * </tr>
 * <tr>
 * <td>Tilt</td>
 * <td>Measures tilt over three axes.</td>
 * <td>degrees<sup>2</sup></td>
 * <td> {@link #getTiltMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a href="http://www.mindsensors.com/index.php?module=pagemaster&PAGE_user_op=view_page&PAGE_id=101"> Sensor Product page </a>
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
public class MindsensorsAccelerometer extends I2CSensor  {
	private static final byte BASE_TILT = 0x42;
	private static final byte BASE_ACCEL = 0x45;
	private static final byte OFF_X_ACCEL = 0x00;
	private static final byte OFF_Y_ACCEL = 0x02;
	private static final byte OFF_Z_ACCEL = 0x04;
	private static final float TO_SI = 0.00980665f;
	
	private byte[] buf = new byte[6];
	
	/**
	 * Creates a SampleProvider for the Mindsensors ACCL-Nx
	 * 
	 * @param port the I2C port
	 * @param address the I2C address of the sensor
	 */
	public MindsensorsAccelerometer(I2CPort port, int address) {
		super(port, address);
		init();
	}
	
	/**
	 * Creates a SampleProvider for the Mindsensors ACCL-Nx
	 * 
	 * @param port the I2C port
	 */
	public MindsensorsAccelerometer(I2CPort port) {
		super(port);
		init();
	}
	
	/**
	 * Creates a SampleProvider for the Mindsensors ACCL-Nx
	 * 
	 * @param port the sensor port
	 * @param address the I2C address of the sensor
	 */
	public MindsensorsAccelerometer(Port port, int address) {
		super(port, address, TYPE_LOWSPEED_9V);
		init();
	}
	
	/**
	 * Creates a SampleProvider for the Mindsensors ACCL-Nx
	 * 
	 * @param port the sensor port
	 */
    public MindsensorsAccelerometer(Port port) {
        this(port, DEFAULT_I2C_ADDRESS);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{  new AccelMode(), new TiltMode() });
    }

    private class AccelMode implements SensorMode {
	@Override
	public int sampleSize() {
		return 3;
	}
	
	@Override
	public void fetchSample(float[] sample, int offset) {
		getData(BASE_ACCEL, buf, 0, 6);
		sample[offset+0] = EndianTools.decodeShortLE(buf, OFF_X_ACCEL) * TO_SI;
		sample[offset+1] = EndianTools.decodeShortLE(buf, OFF_Y_ACCEL) * TO_SI;
		sample[offset+2] = -EndianTools.decodeShortLE(buf, OFF_Z_ACCEL) * TO_SI;
	}
	

	@Override
	public String getName() {
		return "Acceleration";
	}
    }
	
	/**
	 * Return a SampleProvider that provides acceleration data  (in m/s/s) in X, Y, Z axis
	 */
	public SensorMode getAccelerationMode() {
		return getMode(0);
	}
	
  /**
   * Return a SampleProvider that provides tilt data  (in degree) in X, Y, Z axis
   */
	public SensorMode getTiltMode() {
		return getMode(1);
	}

	private class TiltMode implements SensorMode {
	  private float toSI=180f/128f;
		@Override
		public int sampleSize() {
			return 3;
		}

		@Override
		public void fetchSample(float[] sample, int offset) {
			getData(BASE_TILT, buf, 0, 3);			
			sample[offset+0] = ((buf[0] & 0xFF) - 128.0f)*toSI;
			sample[offset+1] = ((buf[1] & 0xFF) - 128.0f)*toSI;
			sample[offset+2] = ((buf[2] & 0xFF) - 128.0f)*toSI;
		}

		@Override
		public String getName() {
			return "Tilt";
		}
	}
}
