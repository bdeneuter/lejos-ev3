package lejos.hardware.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;
import lejos.robotics.SampleProvider;

/**
 * <b>Sensor name</b><br>
 * Description
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
 * <td>Some</td>
 * <td></td>
 * <td></td>
 * <td> {@link #getSomeMode() }</td>
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
 * See <a href=""> Sensor datasheet </a>
 * See <a href=""> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Your name
 * 
 */
public class CodeTemplate extends UARTSensor {

    /**
     * Constructor using a unopened port
     * 
     * @param port
     */
    public CodeTemplate(Port port) {
        super(port);
        init();
    }

    /**
     * Constructor using a opened and configured port
     * 
     * @param port
     */
    public CodeTemplate(UARTPort port) {
        super(port);
        init();
    }

    /**
     * Configures the sensor for first use and registers the supported modes
     * 
     */
    protected void init() {
        setModes(new SensorMode[] { new SomeMode() });
    }

    /**
     * <b>Sensor name, mode</b><br>
     * Mode description
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains # elements. Each element gives Something (in some
     * unit).
     * 
     * <p>
     * <b>Configuration</b><br>
     * The sensor is configured for.... . Currently there are no configurable
     * settings.
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a href=""> Sensor datasheet </a>
     */
    public SampleProvider getSomeMode() {
        return getMode(0);

    }

    private class SomeMode implements SensorMode {

        @Override
        public int sampleSize() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            // TODO Auto-generated method stub

        }

        @Override
        public String getName() {
            // TODO Auto-generated method stub
            return "Some";
        }

    }

}
