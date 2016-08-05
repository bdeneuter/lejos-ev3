package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;

/**
 * <b>HiTechnic NXT IRSeeker</b><br>
 * The NXT IRSeeker is a multi-element infrared detector that
 * detects infrared signals from sources such as the HiTechnic IRBall soccer
 * ball, infrared remote controls and sunlight.
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
 * <td>Unmodulated</td>
 * <td>Measures the angle to a source of unmodulated infrared light</td>
 * <td>Degrees</td>
 * <td> {@link #getUnmodulatedMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a
 *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NSK1042">
 *      Sensor Product page </a>
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
 */
public class HiTechnicIRSeeker extends I2CSensor {
  byte[] buf = new byte[1];

  public HiTechnicIRSeeker(I2CPort port) {
    super(port);
    init();
  }

  public HiTechnicIRSeeker(Port port) {
    super(port);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new UnmodulatedMode() });
  }

  /**
   * <b>HiTechnic IR seeker, Unmodulated mode</b><br>
   * Measures the angle to a source of unmodulated infrared light
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the angle to the infrared source. The angle is expressed in degrees following the right hand rule. 
   */

  public SensorMode getUnmodulatedMode() {
    return getMode(0);
  }

  /**
   * Measures angle with zero forward, anti-clockwise positive
   */
  private class UnmodulatedMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(0x42, buf, 1);
      float angle = Float.NaN;
      if (buf[0] > 0) {
        angle = -(buf[0] * 30 - 150);
      }
      sample[offset] = angle;
    }

    @Override
    public String getName() {
      return "Unmodulated";
    }
  }
}
