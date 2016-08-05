package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

/**
 * <b>NXT Sound sensor</b><br>
 * Description
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
 * <td>dBA</td>
 * <td>Measures sound level adjusted to the sensitivity of the human ear</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getDBAMode() }</td>
 * </tr>
 * <tr>
 * <td>dB</td>
 * <td>Measures sound level</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getDBMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a href="http://www.lego.com/en-us/mindstorms/downloads/software/nxt-hdk/"> Mindstorms NXT HDK/SDK </a>
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
public class NXTSoundSensor extends AnalogSensor implements SensorConstants {
  protected static final long SWITCH_DELAY = 10;
  /**
   * Create a sound sensor.
   * 
   * @param port
   *          the sensor port to use
   */
  public NXTSoundSensor(Port port) {
    super(port);
    init();
  }

  /**
   * Create a sound sensor.
   * 
   * @param port
   *          the sensor port to use
   */
  public NXTSoundSensor(AnalogPort port) {
    super(port);
    init();
  }

  private void init() {
    setModes(new SensorMode[]{ new DBAMode(), new DBMode() }); 
  }

  public class DBMode implements  SensorMode {

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      switchType(TYPE_SOUND_DB, SWITCH_DELAY);
      sample[offset] = 1.0f - normalize(port.getPin1());
    }

    @Override
    public String getName() {
      return "Sound DB";
    }

  }

  
  /**
   * get a sample provider the returns the sound level 
   * @return the sample provider
   */
  public SampleProvider getDBMode() {
    return getMode(1);
  }

  /**
   * get a sample provider the returns the sound level adjusted to how a human ear would experience it
   * @return the sample provider
   */
  public SampleProvider getDBAMode() {
    return getMode(0);
  }
  
  private class DBAMode implements SensorMode {

  @Override
  public int sampleSize() {
    return 1;
  }

  @Override
  public void fetchSample(float[] sample, int offset) {
    switchType(TYPE_SOUND_DBA, SWITCH_DELAY);
    sample[offset] =  1.0f - normalize(port.getPin1());
  }

  @Override
  public String getName() {
    return "Sound DBA";
  }
  }
}
