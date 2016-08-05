package lejos.hardware.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;
import lejos.robotics.SampleProvider;

/**
 * <b>EV3 Gyro sensor</b><br>
 * The digital EV3 Gyro Sensor measures the sensors rotational motion and changes in its orientation. 
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
 * <td>Angle</td>
 * <td>Measures the orientation of the sensor</td>
 * <td>Degrees</td>
 * <td> {@link #getAngleMode() }</td>
 * </tr>
 * <tr>
 * <td>Rate</td>
 * <td>Measures the angular velocity of the sensor</td>
 * <td>Degrees / second</td>
 * <td> {@link #getRateMode() }</td>
 * </tr>
 * <tr>
 * <td>Rate and Angle</td>
 * <td>Measures both angle and angular velocity</td>
 * <td>Degrees, Degrees / second</td>
 * <td> {@link #getAngleAndRateMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * Use {@link #reset()} to recalibrate the sensor and to reset accumulated angle to zero. Keep the sensor motionless during a reset. 
 * The sensor shuld also be motionless during initialization.
 * 
 * <p>
 * 
 * See <a href="http://www.ev-3.net/en/archives/849"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Andy, Aswin Bouwmeester
 * 
 */
public class EV3GyroSensor extends UARTSensor {
  private static final long SWITCHDELAY = 200;
  private short[] raw=new short[2];

  public EV3GyroSensor(Port port) {
    super(port, 3);
    setModes(new SensorMode[] { new RateMode(), new AngleMode(), new RateAndAngleMode() });

  }

  public EV3GyroSensor(UARTPort port) {
    super(port, 3);
    setModes(new SensorMode[] { new RateMode(), new AngleMode(), new RateAndAngleMode() });
  }


  /**
   * <b>EV3 Gyro sensor, Angle mode</b><br>
   * Measures the orientation of the sensor in respect to its start orientation. 
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one elements representing the orientation (in Degrees) of the sensor in respect to its start position. 
   * 
   * <p>
   * <b>Configuration</b><br>
   * The start position can be set to the current position using the reset method of the sensor.
   * 
   * @return A sampleProvider
   * See {@link lejos.robotics.SampleProvider leJOS conventions for
   *      SampleProviders}
   */
  public SampleProvider getAngleMode() {
    return getMode(1);
  }

 
  /**
   * <b>EV3 Gyro sensor, Rate mode</b><br>
   * Measures angular velocity of the sensor. 
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one elements representing the angular velocity (in Degrees / second) of the sensor. 
   * 
   * <p>
   * <b>Configuration</b><br>
   * The sensor can be recalibrated using the reset method of the sensor.
   * 
   * @return A sampleProvider
   * See {@link lejos.robotics.SampleProvider leJOS conventions for
   *      SampleProviders}
   */
  public SampleProvider getRateMode() {
    return getMode(0);
  }


  /**
   * <b>EV3 Gyro sensor, Rate mode</b><br>
   * Measures both angle and angular velocity of the sensor. 
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains two elements. The first element contains angular velocity (in degrees / second). The second element contain angle (in degrees).  
   * 
   * <p>
   * <b>Configuration</b><br>
   * The sensor can be recalibrated using the reset method of the sensor.
   * 
   * @return A sampleProvider
   * See {@link lejos.robotics.SampleProvider leJOS conventions for
   *      SampleProviders}
   */
  public SampleProvider getAngleAndRateMode() {
    return getMode(2);
  }

  
  /**
   * Hardware calibration of the Gyro sensor and reset off accumulated angle to zero. <br>
   * The sensor should be motionless during calibration.
   */
  public void reset() {
    // Reset mode (4) is not used here as it behaves eratically. Instead the reset is done implicitly by going to mode 1.
    switchMode(1, SWITCHDELAY);
    // And back to 3 to prevent another reset when fetching the next sample
    switchMode(3, SWITCHDELAY);
  }

  private class AngleMode implements SampleProvider, SensorMode {
    private static final int   MODE = 3;
    private static final float toSI = -1;

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      switchMode(MODE, SWITCHDELAY);
      port.getShorts(raw, 0, raw.length);
      sample[offset] = raw[0] * toSI;
    }

    @Override
    public String getName() {
      return "Angle";
    }

  }

  private class RateMode implements SampleProvider, SensorMode {
    private static final int   MODE = 3;
    private static final float toSI = -1;

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      switchMode(MODE, SWITCHDELAY);
      port.getShorts(raw, 0, raw.length);
      sample[offset] = raw[1] * toSI;
    }

    @Override
    public String getName() {
      return "Rate";
    }

  }

  private class RateAndAngleMode implements SampleProvider, SensorMode {
    private static final int   MODE = 3;
    private static final float toSI = -1;

    @Override
    public int sampleSize() {
      return 2;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      switchMode(MODE, SWITCHDELAY);
      port.getShorts(raw, 0, raw.length);
      for (int i=0;i<raw.length;i++) {
        sample[offset+i] = raw[i] * toSI;
      }
    }

    @Override
    public String getName() {
      return "Angle and Rate";
    }

  }

  
}
