package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;

/**
 * <p>
 * This class represents a Dexter Industries Laser Sensor. The sensor contains a laser and a photodiode to read ambient light values. This sensor can be calibrated to low and high values.
 * </p>
 * 
 * <p>
 * The Dexter Industries laser can turn on and off very rapidly, with the following characteristics:
 * </p>
 * <li>it takes about 8-10 ms to turn on and reach full power <li>it takes about 5 ms to turn off
 * 
 */



/**
 * <b>Dexter laser sensor</b><br>
 * The sensor contains a laser and a photodiode to read ambient light values.<br>
 * The Dexter Industries laser can turn on and off very rapidly, with the following characteristics:
 * </p>
 * <li>it takes about 8-10 ms to turn on and reach full power <li>it takes about 5 ms to turn off
 * 
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
 * <td>Ambient</td>
 * <td>Measures light level with the laser off </td>
 * <td>N/A</td>
 * <td> {@link #getAmbientMode() }</td>
 * </tr>
 * <tr>
 * <td>Laser</td>
 * <td>Measures light level with the laser on </td>
 * <td>N/A</td>
 * <td> {@link #getLaserMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
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
 * @author ?
 * 
 */public class DexterLaserSensor extends AnalogSensor implements SensorConstants {
    protected static final long SWITCH_DELAY = 10;
    private boolean             laser        = false;

    /**
     * Create a laser sensor object attached to the specified port, and sets the laser on or off.
     * 
     * @param port
     *            an already open analog port
     */
    public DexterLaserSensor(AnalogPort port) {
        super(port);
        init();
    }

    /**
     * Create a laser sensor object attached to the specified port, and sets the laser on or off.
     * 
     * @param port
     *            port, e.g. Port.S1
     */
    public DexterLaserSensor(Port port) {
        super(port);
        init();
    }

    protected void init() {
        setLaser(laser);
        setModes(new SensorMode[] { new Laser(false,"Ambient"), new Laser(true, "laser") });
    }

    public void setLaser(boolean laserState) {
        switchType(laserState ? TYPE_LIGHT_ACTIVE : TYPE_LIGHT_INACTIVE, SWITCH_DELAY);
        this.laser = laserState;
    }

    /**
     * Get a sample provider that returns samples with the laser turned off.
     * 
     * @return the sensor mode
     */

    
    
    /**
     * <b>Dexter laser sensor, Ambient mode</b><br>
     * Measures light level with the laser off
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one elements representing normalised (range 0-1) light level. 
     * 
     * <p>
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     */
public SensorMode getAmbientMode() {
        return getMode(0);
    }

 

/**
 * <b>Dexter laser sensor, Laser mode</b><br>
 * Measures light level with the laser on
 * 
 * <p>
 * <b>Size and content of the sample</b><br>
 * The sample contains one elements representing normalised (range 0-1) light level. 
 * 
 * <p>
 * 
 * @return A sampleProvider
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 */    public SensorMode getLaserMode() {
        return getMode(1);
    }

    private class Laser implements SensorMode {

        private boolean state;
        private String  name;

        private Laser(boolean state, String name) {
            this.state = state;
            this.name = name;
        }

        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            setLaser(state);
            sample[offset] = 1.0f - normalize(port.getPin1());
        }

        @Override
        public String getName() {
            return name;
        }

    }

}
