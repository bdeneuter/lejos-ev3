package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.robotics.Calibrate;

/**
 * This class supports the <a href="http://mindsensors.com">Mindsensors</a> compass sensor.
 * 
 * See http://www.mindsensors.com/index.php?module=pagemaster&PAGE_user_op=view_page&PAGE_id=56
 * 
 * author Lawrie Griffiths
 * 
 */
/**
 * <b>MindSensor Compass sensor</b><br>
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
 * <td>Compass</td>
 * <td>Measures the orientation of the sensor</td>
 * <td>Degrees, corresponding to the compass rose</td>
 * <td> {@link #getCompassMode() }</td>
 * </tr>
 * <tr>
 * <td>Angle</td>
 * <td>Measures the orientation of the sensor</td>
 * <td>Degrees, corresponding to the right hand coordinate system</td>
 * <td> {@link #getAngleMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * The sensor can be calibrated for magnetic disturbances coming from the robot
 * (soft iron calibration). Use the startCalibration method to put the sensor in
 * calibration mode. While in calibration mode the sensor should be rotated
 * slowly for making 2 full rotations taking at least 20 seconds per turn. Then
 * end calibration with the endCalibration method.
 * <p>
 * 
 * See <a
 *      href="http://www.mindsensors.com/index.php?module=documents&JAS_DocumentManager_op=downloadFile&JAS_File_id=157">
 *      Sensor datasheet </a>
 * See <a
 *      href="http://www.mindsensors.com/index.php?module=pagemaster&PAGE_user_op=view_page&PAGE_id=56">
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
public class MindsensorsCompass extends I2CSensor implements Calibrate {
  private final static byte COMMAND           = 0x41;
  private final static byte BEGIN_CALIBRATION = 0x43;
  private final static byte END_CALIBRATION   = 0x44;

  private byte[]            buf               = new byte[2];

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          I2C port for the compass
   * @param address
   *          The I2C address used by the sensor
   */
  public MindsensorsCompass(I2CPort port, int address) {
    super(port, address);
    init();
  }

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          I2C port for the compass
   */
  public MindsensorsCompass(I2CPort port) {
    super(port, DEFAULT_I2C_ADDRESS);
    init();
  }

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          Sensor port for the compass
   * @param address
   *          The I2C address used by the sensor
   */
  public MindsensorsCompass(Port port, int address) {
    super(port, address);
    init();
  }

  /**
   * Create a compass sensor object
   * 
   * @param port
   *          Sensor port for the compass
   */
  public MindsensorsCompass(Port port) {
    super(port);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new CompassMode(), new AngleMode() });
  }

  /**
   * <b>Mindsensors compass sensor, Compass mode</b><br>
   * Measures the bearing of the sensor.   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the bearing of the sensor
   * relative to north expressed in degrees. East being at 90 degrees.
   */
  public SensorMode getCompassMode() {
    return getMode(0);
  }

  private class CompassMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(0x42, buf, 1);

      // TODO: Could use integer mode for higher resolution
      sample[offset] = (255 - (buf[0] & 0xFF)) * 359f / 255f;
    }

    @Override
    public String getName() {
      return "Compass";
    }

  }

  /**
   * <b>MindSensors compass sensor, Angle mode</b><br>
   * Measures the bearing of the sensor.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the bearing of the sensor
   * relative to north expressed in degrees using a right hand coordinate system
   * in the range of (-180 to 180). West being at 90 degrees, east being at -90
   * degrees.
   */
  public SensorMode getAngleMode() {
    return getMode(0);
  }

  private class AngleMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(0x42, buf, 1);

      // TODO: Could use integer mode for higher resolution
      sample[offset] = (255 - (buf[0] & 0xFF)) * -359f / 255f;
    }

    @Override
    public String getName() {
      return "Angle";
    }

  }

  /**
   * Starts calibration for the compass. Must rotate *very* slowly, taking at
   * least 20 seconds per rotation.
   * 
   * Should make 1.5 to 2 full rotations. Must call stopCalibration() when done.
   */
  public void startCalibration() {
    buf[0] = BEGIN_CALIBRATION;
    sendData(COMMAND, buf, 1);
  }

  /**
   * Ends calibration sequence.
   *
   */
  public void stopCalibration() {
    buf[0] = END_CALIBRATION;
    sendData(COMMAND, buf, 1);
  }
}
