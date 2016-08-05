package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;

/**
 * <b>HiTechnic NXT Acceleration / Tilt Sensor (NAC1040)</b><br>
 * The HiTechnic Accelerometer / Tilt Sensor measures acceleration in three
 * axes.
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
 * <td>Acceleration</td>
 * <td>Measures acceleration over three axes.</td>
 * <td>meter / second<sup>2</sup></td>
 * <td> {@link #getAccelerationMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a
 *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NAC1040">
 *      Sensor Product page </a> (Some details from HTAC-driver.h from
 *      http://botbench.com/blog/robotc-driver-suite/)
 * 
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Lawrie Griffiths
 * @author Michael Mirwaldt
 * 
 */
public class HiTechnicAccelerometer extends I2CSensor {
  private static final int   BASE_ACCEL = 0x42;
  private static final int   OFF_X_HIGH = 0x00;
  private static final int   OFF_Y_HIGH = 0x01;
  private static final int   OFF_Z_HIGH = 0x02;
  private static final int   OFF_2BITS  = 3;
  private static final float TO_SI      = 0.049033251f;

  private byte[]             buf        = new byte[6];

  /**
   * Creates a SampleProvider for the HiTechnic Acceleration Sensor
   * 
   * @param port
   *          the I2C port
   * @param address
   *          the I2C address of the sensor
   */
  public HiTechnicAccelerometer(I2CPort port, int address) {
    super(port, address);
    init();
  }

  /**
   * Creates a SampleProvider for the HiTechnic Acceleration Sensor
   * 
   * @param port
   *          the I2C port
   */
  public HiTechnicAccelerometer(I2CPort port) {
    this(port, DEFAULT_I2C_ADDRESS);
    init();
  }

  /**
   * Creates a SampleProvider for the HiTechnic Acceleration Sensor
   * 
   * @param port
   *          the sensor port
   * @param address
   *          the I2C address of the sensor
   */
  public HiTechnicAccelerometer(Port port, int address) {
    super(port, address, TYPE_LOWSPEED_9V);
    init();
  }

  /**
   * Creates a SampleProvider for the HiTechnic Acceleration Sensor
   * 
   * @param port
   *          the I2C port
   */
  public HiTechnicAccelerometer(Port port) {
    this(port, DEFAULT_I2C_ADDRESS);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new AccelMode() });
  }

  /**
   * <b>HiTechnic NXT Acceleration , Acceleration mode</b><br>
   * Measures acceleration over three axes.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains 3 elements, containing acceleration over the X, Y and Z
   * axis respectively. The range of the sensor is -2 to 2 G (1G = 9.81
   * m/s<sup>2</sup>).
   * 
   * <p>
   * 
   * @return A sampleProvider
   * See {@link lejos.robotics.SampleProvider leJOS conventions for
   *      SampleProviders}
   * See <a
   *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NAC1040">
   *      Sensor datasheet </a>
   */
  public SensorMode getAccelerationMode() {
    return getMode(0);
  }

  private class AccelMode implements SensorMode {

    @Override
    public int sampleSize() {
      return 3;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(BASE_ACCEL, buf, 0, 6);

      sample[offset + 0] = ((buf[OFF_X_HIGH] << 2) + (buf[OFF_X_HIGH
          + OFF_2BITS] & 0xFF))
          * TO_SI;
      sample[offset + 1] = ((buf[OFF_Y_HIGH] << 2) + (buf[OFF_Y_HIGH
          + OFF_2BITS] & 0xFF))
          * TO_SI;
      sample[offset + 2] = ((buf[OFF_Z_HIGH] << 2) + (buf[OFF_Z_HIGH
          + OFF_2BITS] & 0xFF))
          * TO_SI;
    }

    @Override
    public String getName() {
      return "Acceleration";
    }
  }
}
