package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

/**
 * Support for Dexter Industries DPressure250
 * Not tested.
 * 
 * See http://www.dexterindustries.com/Products-dPressure.html.
 * 
 * @author Lawrie Griffiths
 *
 */


/**
 * <b>Dexter Industries DPressure250</b><br>
 * Pressure sensor for LEGO® MINDSTORMS® EV3 and NXT.
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
 * <td>Measures the pressure </td>
 * <td>Pascal</td>
 * <td> {@link #getPressureMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a href="http://www.dexterindustries.com/manual/dpressure/"> Sensor datasheet </a>
 * See <a href="http://www.dexterindustries.com/Products-dPressure.html"> Sensor Product page </a>
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
public class DexterPressureSensor250 extends AnalogSensor implements SensorConstants {
	/*
	 * 
	 * Formula from DPRESS-driver.h:
	 * vRef = 4.85
	 * vOut = rawValue * vRef / 1023
	 * result = (vOut / vRef - CAL1) / CAL2
	 */
	private static final double CAL1 = 0.04;
	private static final double CAL2 = 0.00369;
	
	/*
	 * Optimized:
	 * result = rawValue * DPRESS_MULT - DPRESS_OFFSET;
	 */
	private static final float DPRESS_MULT = (float)(1.0 / (CAL2 ));
	private static final float DPRESS_OFFSET = (float)(CAL1 / CAL2);
	
    public DexterPressureSensor250(AnalogPort port) {
        super(port);
        init();
    }
    
    public DexterPressureSensor250(Port port) {
        super(port);
        init();
    }
    
    protected void init() {
    	setModes(new SensorMode[]{ new PressureMode() });
    	port.setTypeAndMode(TYPE_CUSTOM, MODE_RAW);
    }
    



	
	
    /**
     * <b>Dexter Industries DPressure250, Pressure mode</b><br>
     * Measures the pressure
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element containing the pressure (in PA) measured by the sensor.
     * 
     * <p>
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a href="http://www.dexterindustries.com/manual/dpressure/"> Sensor datasheet </a>
     */
    public SampleProvider getPressureMode() {
        return getMode(0);

    }

    private class PressureMode implements SensorMode {

        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            sample[offset] = (NXTRawValue(normalize(port.getPin1())) * DPRESS_MULT - DPRESS_OFFSET)* 1000f; // in pascals
       }

        @Override
        public String getName() {
            return "Pressure";
        }

    }
}
