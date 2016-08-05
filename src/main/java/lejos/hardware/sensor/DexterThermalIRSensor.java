package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lejos.utility.EndianTools;

/**
 * Support for the Dexter Industries Thermal Infrared Sensor.
 * <p>
 * The Dexter Industries Thermal Infrared Sensor reads surface temperatures of objects. 
 * It is a non-contact thermometer based on the Melexis MLX90614xCC. Detecting the infrared radiation 
 * from an object, the sensor can read object temperatures between -90\u00b0F and 700\u00b0F 
 * (-70\u00b0C and +380\u00b0C).  The sensor has a high accuracy of 0.5\u00b0C and a resolution of 0.02\u00b0C.
 * <p>
 * The Thermal Infrared Sensor reads both the ambient temperature (the temperature of the air 
 *around the sensor) and the surface temperature of the object that the sensor is pointed towards.
 *
 * @author Kirk P. Thompson
 *
 */




/**
 * <b>Dexter Industries Thermal Infrared Sensor </b><br>
 * The Dexter Industries Thermal Infrared Sensor reads surface temperatures of objects.
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
 * <td>Object</td>
 * <td>Measures the temperature of an objects surface</td>
 * <td>Kelvin</td>
 * <td> {@link #getObjectMode() }</td>
 * </tr>
 * <tr>
 * <td>Ambient</td>
 * <td>Measures the ambient temperature</td>
 * <td>Kelvin</td>
 * <td> {@link #getAmbientMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * The Emissivity value of the sensor can be configured using the {@link #setEmissivity} method.
 * 
 * <p>
 * 
 * See <a href="https://www.sparkfun.com/datasheets/Sensors/Temperature/MLX90614_rev001.pdf"> Sensor datasheet </a>
 * See <a href="http://www.dexterindustries.com/manual/thermal-infrared-sensor/"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Kirk P. Thompson
 * 
 */public class DexterThermalIRSensor extends I2CSensor {
	private static final int I2C_ADDRESS = 0x0E;
	private static final int REG_GET_OBJECT = 0x01;
	private static final int REG_GET_AMBIENT = 0x00;
	private static final int REG_GET_EMISSIVITY = 0x03;
	private static final int REG_SET_EMISSIVITY = 0x02;
	private static final int MAX_EMISSIVITY = 10000;
	
	private byte [] buf = new byte[2];
	
	/**
	 * Construct a sensor instance that is connected to <code>port</code>.
	 * @param port The NXT port to use
	 */
    public DexterThermalIRSensor(I2CPort port) {
        super(port, I2C_ADDRESS);
        init();
    }
    
    public DexterThermalIRSensor(Port port) {
        super(port, I2C_ADDRESS);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{ new ObjectMode(), new AmbientMode() });
    }
	
	/**
	 * Set the sensor's emissivity value. Valid values are 
	 * 0.01-1.0. The emissivity is stored in non-volatile memory of the sensor and will be 
	 * retained even after power-off.
	 * 	 <p> 
	 * < 0.01 returns with no action. > 1.0 sets to 1.0
	 * @param emissivity The value to set emissivity coefficient to
	 */
	public synchronized void setEmissivity(float emissivity){
		int intEmissivity = Math.round(emissivity * MAX_EMISSIVITY);
		if (intEmissivity<100) return;
		if (intEmissivity>MAX_EMISSIVITY) intEmissivity=MAX_EMISSIVITY;
		
		EndianTools.encodeShortBE(intEmissivity, buf, 0);
		sendData(REG_SET_EMISSIVITY, buf, 2);
		Delay.msDelay(500);
	}
	
	/**
	 * Read the current emissivity value. 
	 * <p>
	 * Caveat Emptor: The sensor appears to only return the emissivity value
	 * after it was intially set after power-up with <code>setEmissivity()</code>. It doesn't seem 
	 * to retrieve it from EEPROM.
	 * 
	 * @return The emissivity value from 0.01 to 1.0
	 */
	public float getEmissivity(){
		getData(REG_GET_EMISSIVITY, buf, 2);
		return EndianTools.decodeUShortLE(buf, 0) / 65535f;
	}
	
	@Override
	public String getProductID() {
		return "dTIR";
	}

	@Override
	public String getVendorID() {
		return "Dexter";
	}
	

	
	
    /**
     * <b>Dexter Industries Thermal Infrared Sensor, Object mode</b><br>
     * Measures the temperature of an objects surface
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one elements representing the surface temperature (in Kelvin) of an object. the sensor can read object temperatures between -90\u00b0F and 700\u00b0F 
     * (-70\u00b0C and +380\u00b0C).  The sensor has a high accuracy of 0.5\u00b0C and a resolution of 0.02\u00b0C.

     * 
     * <p>
    * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a href="https://www.sparkfun.com/datasheets/Sensors/Temperature/MLX90614_rev001.pdf"> Sensor datasheet </a>
     */    
	public SensorMode getObjectMode() {
        return getMode(0);
    }
    
    private class ObjectMode implements SensorMode {
        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(REG_GET_OBJECT, buf, 2);
            sample[offset] = .02f * EndianTools.decodeUShortLE(buf, 0); // degrees Kelvin       
        }   
        
        @Override
        public String getName() {
            return "Object";
        }
       
    }
	
	
	
    /**
     * <b>Dexter Industries Thermal Infrared Sensor, Ambient mode</b><br>
     * Measures the ambient temperature
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one elements representing the ambient temperature (in Kelvin) (the temperature of the air around the sensor).

     * 
     * <p>
    * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a href="https://www.sparkfun.com/datasheets/Sensors/Temperature/MLX90614_rev001.pdf"> Sensor datasheet </a>
     */	public SensorMode getAmbientMode() {
		return getMode(1);
	}

	private class AmbientMode implements SensorMode {
		@Override
		public int sampleSize() {
			return 1;
		}

		@Override
		public void fetchSample(float[] sample, int offset) {
			getData(REG_GET_AMBIENT, buf, 2);
			sample[offset] = .02f * EndianTools.decodeUShortLE(buf, 0); // degrees Kelvin				
		}

		@Override
		public String getName() {
			return "Ambient";
		}	
	}
}
