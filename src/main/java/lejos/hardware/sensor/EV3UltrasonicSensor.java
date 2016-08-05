package lejos.hardware.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;
import lejos.robotics.SampleProvider;

/**
 * <b>Lego EV3 Ultrasonic sensor</b><br>
 * The EV3 Ultrasonic sensor measures distance to an object in front of the
 * sensor. It can also be used to detect other (active) Ultrasonic sensors in
 * the vicinity.
 * 
 * 
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
 * <td>Listen</td>
 * <td>Listens for other ultrasonic sensors</td>
 * <td>Boolean</td>
 * <td> {@link #getListenMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * The sensor can be switched off and on using the {@link #enable} and
 * {@link #disable} methods. Disabling the sensor also shuts down the lights.
 * 
 * <p>
 * 
 * See <a href="http://www.ev-3.net/en/archives/844"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Aswin Bouwmeester
 * 
 */
public class EV3UltrasonicSensor extends UARTSensor {

  private static final int DISABLED    = 3;
  private static final int SWITCHDELAY = 200;

  protected void init() {
    setModes(new SensorMode[] { new DistanceMode(), new ListenMode() });
  }

  /**
   * Create the Ultrasonic sensor class.
   * 
   * @param port
   */
  public EV3UltrasonicSensor(Port port) {
    super(port, 0);
    init();
  }

  /**
   * Create the Ultrasonic sensor class.
   * 
   * @param port
   */
  public EV3UltrasonicSensor(UARTPort port) {
    super(port, 0);
    init();
  }

  /**
   * <b>Lego EV3 Ultrasonic sensor, Listen mode</b><br>
   * Listens for the presence of other ultrasonic sensors. 
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one elements indicating the presence of another ultrasonic sensor. 
   * A value of 1 indicates that the sensor detects another ultrasonic sensor.
   * 
   * @return A sampleProvider
   */  
  public SampleProvider getListenMode() {
    return getMode(1);
  }

  /**
   * <b>Lego EV3 Ultrasonic sensor, Distance mode</b><br>
   * Measures distance to an object in front of the sensor
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one elements representing the distance (in metres) to an object in front of the sensor.
   * unit).
   * 
   * @return A sampleProvider
   */
  public SampleProvider getDistanceMode() {
    return getMode(0);
  }

  /**
   * Enable the sensor. This puts the indicater LED on.
   */
  public void enable() {
    switchMode(0, SWITCHDELAY);
  }

  /**
   * Disable the sensor. This puts the indicater LED off.
   */
  public void disable() {
    switchMode(DISABLED, SWITCHDELAY);
  }

  /**
   * Indicate that the sensor is enabled.
   * 
   * @return True, when the sensor is enabled. <br>
   *         False, when the sensor is disabled.
   */
  public boolean isEnabled() {
    return (currentMode == DISABLED) ? false : true;
  }


private class DistanceMode implements SampleProvider, SensorMode {
    private static final int   MODE = 0;
    private static final float toSI = 0.001f;

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      switchMode(MODE, SWITCHDELAY);
      int raw = port.getShort();
      sample[offset] = (raw == 2550) ? Float.POSITIVE_INFINITY : (float) raw
          * toSI;
    }

    @Override
    public String getName() {
      return "Distance";
    }

  }

  /**
   * Represents a Ultrasonic sensor in listen mode
   */
  private class ListenMode implements SampleProvider, SensorMode {
    private static final int MODE = 2;

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      switchMode(MODE, SWITCHDELAY);
      sample[offset] = port.getShort() & 0xff;
    }

    @Override
    public String getName() {
      return "Listen";
    }
  }

}
