package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.utility.EndianTools;


/**
 * <b>MindSensors Pressure Sensor</b><br>
 * This sensor measures pressures produced by LEGO Pneumatics systems and lot more!
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
 * <td>Pressure</td>
 * <td>Measures the absolute pressure</td>
 * <td>Pascal</td>
 * <td> {@link #getPressureMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * Description of default sensor configuration (when that matters). Description
 * of available methods for configuration.
 * 
 * <p>
 * 
 * See <a href="http://www.mindsensors.com/index.php?module=documents&JAS_DocumentManager_op=downloadFile&JAS_File_id=1046"> Sensor datasheet </a>
 * See <a href="http://www.mindsensors.com/index.php?module=pagemaster&PAGE_user_op=view_page&PAGE_id=150"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author fussel_dlx
 * 
 */
public class MindSensorsPressureSensor extends I2CSensor  {

	/*
	 * Code contributed and tested by fussel_dlx on the forums:
	 * http://lejos.sourceforge.net/forum/viewtopic.php?f=6&t=4329
	 * 
	 * Comment: the sensor can pressure in various units. However, using those
	 * units results in a loss of precision. And furthermore, the conversion to PSI or
	 * whatever can be done in Java. The obvious advantage is, that float can be used.
	 */
	
	private static final int ADDRESS = 0x18; 
	private final byte[] buf = new byte[4];
	
    public MindSensorsPressureSensor(I2CPort port) {
        // also works with high speed mode
        super(port, ADDRESS);
        init();
    }
    
    public MindSensorsPressureSensor(Port port) {
        // also works with high speed mode
        super(port, ADDRESS);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{ new PressureMode() });
    }
    
    /**
     * Return a ample provider for pressure mode. Pressure is expressed in Pascal.
     */
    public SensorMode getPressureMode() {
    	return getMode(0);
    }
    
private class PressureMode implements SensorMode {    

	@Override
	public int sampleSize() {
		return 1;
	}

	@Override
	public void fetchSample(float[] sample, int offset) {
		getData(0x53, buf, 0, 4);		
		sample[offset] = EndianTools.decodeIntLE(buf, 0);
	}

	@Override
	public String getName() {
		return "Pressure";
	}
}
}
