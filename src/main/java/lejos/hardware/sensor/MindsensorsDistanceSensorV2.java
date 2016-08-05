 package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.utility.EndianTools;


/**
 * <b> Mindsensors DIST-Nx series of Optical Distance Sensors, Version 2</b><br>
 *  Mindsensors DIST Sensor measure the distance to an object in front of the sensor using IR light
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
 * <td>Distance</td>
 * <td>Measures distance to an object in front of the sensor</td>
 * <td>Meter</td>
 * <td> {@link #getDistanceMode() }</td>
 * </tr>
 * <tr>
 * <td>Voltage</td>
 * <td>Returns the output level of the sensors signal processing unit</td>
 * <td>Volt</td>
 * <td> {@link #getVoltageMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * The sensor can be powered on and off using the powerOn and powerOff methods. It is useful to power off the sensor when not in use as it consumes a a fair bit of energy.
 * <br>
 * The sensor can be tuned for a particular Sharp optical distance sensor using the setModule method. See the top of the sensor for the Sharp module installed on the sensor.
 * <br>
 * The sensor supports hardware calibration but this in not supported by this interface.
 * 
 * <p>
 * 
 * See <a href="http://www.mindsensors.com/index.php?module=documents&JAS_DocumentManager_op=downloadFile&JAS_File_id=335"> Sensor datasheet </a>
 * See <a href="http://www.mindsensors.com/index.php?module=pagemaster&PAGE_user_op=view_page&PAGE_id=73"> Sensor Product page </a>
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
public class MindsensorsDistanceSensorV2 extends I2CSensor  {
	private byte[] buf = new byte[2];
	
	//Registers
	private final static int COMMAND = 0x41;
	private final static int DIST_DATA_LSB = 0x42;
	public static final int VOLT_DATA_LSB = 0x44;
  
	//Commands
	private final static byte DE_ENERGIZED = 0x44;
	private final static byte ENERGIZED = 0x45;
	private final static byte GP2D12 = 0x31;
  private final static byte GP2D120 = 0x32;
  private final static byte GP2Y0A21YK = 0x33;
  private final static byte GP2Y0A02YK = 0x34;
  private final static byte CUSTOM_MODULE = 0x31;
	

  
	/**
	 *
	 * @param port NXT sensor port 1-4
	 */
	public MindsensorsDistanceSensorV2(I2CPort port){
	    this(port, DEFAULT_I2C_ADDRESS);
	    init();
	}

    /**
     *
     * @param port NXT sensor port 1-4
     * @param address I2C address for the sensor
     */
    public MindsensorsDistanceSensorV2(I2CPort port, int address){
        super(port, address);
        init();
    }
     
    /**
     *
     * @param port NXT sensor port 1-4
     */
    public MindsensorsDistanceSensorV2(Port port){
        this(port, DEFAULT_I2C_ADDRESS);
        init();
    }

    /**
     *
     * @param port NXT sensor port 1-4
     * @param address I2C address for the sensor
     */
    public MindsensorsDistanceSensorV2(Port port, int address){
        super(port, address, TYPE_LOWSPEED);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{ new DistanceMode(), new VoltageMode() });
    	powerOn();
    }

	/**
	 * Turns the sensor module on.  <br>
	 * Power is turned on by the constructor method.
	 *
	 */
	public void powerOn(){
		sendData(COMMAND, ENERGIZED);
	}
	
	/**
	 * Turns power to the sensor module off.
	 *
	 */
	public void powerOff(){
		sendData(COMMAND, DE_ENERGIZED);
	}
	
	
	/** Configure the sensor for a particular Sharp Module
	 * @param module See static fields for valid modules
	 */
	public void setModule (byte module) {
	  if (module <0x31 || module > 0x35 ) {
	    throw new IllegalArgumentException();
	  }
	  else {
	    sendData(COMMAND,  module);
	  }
	}
	
	/**
	 * Returns a sample provider that measures distance (in meter).
	 */
	public SensorMode getDistanceMode() {
		return getMode(0);
	}
	
	
	//TODO: I think it is milivolt and should be converted to volt (Aswin)
  /**
   * Returns a sample provider that measures the output level (in volt) of the sensors signal processing unit.
   */
  public SensorMode getVoltageMode() {
    return getMode(1);
  }

private class DistanceMode implements SensorMode {	
	@Override
	public int sampleSize() {
		return 1;
	}

	@Override
	public void fetchSample(float[] sample, int offset) {
		getData(DIST_DATA_LSB, buf, 2);
		sample[offset] = (float) EndianTools.decodeShortLE(buf, 0) / 100f;	
	}

	@Override
  public String getName() {
    return "Distance";
  }


}
	
	private void dump() {
	  System.out.print(buf.length+": ");
	  for (int i=0;i<buf.length;i++) System.out.println(buf[i]);
	}

	
	private class VoltageMode implements SensorMode {

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(VOLT_DATA_LSB, buf, 2);
      sample[offset] = (float) EndianTools.decodeShortLE(buf, 0) / 1000f;  
    }

    @Override
    public String getName() {
      return "Voltage";
    }
	  
	}
}
