package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.robotics.Color;
import lejos.robotics.ColorIdentifier;

/**
 * HiTechnic color sensor.<br>
 * www.hitechnic.com
 * 
 * This class does support HiTechnic Color Sensor V2.
 * 
 * See http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NCO1038
 *
 *@author BB extended by A.T.Brask, converted for EV3 by Lawrie Griffiths
 *
 */

/**
 * <b>HiTechnic color sensor</b><br>
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
 * <td>Color ID</td>
 * <td>Measures the color ID of a surface</td>
 * <td>Color ID</td>
 * <td> {@link #getColorIDMode() }</td>
 * </tr>
 * <tr>
 * <td>RGB</td>
 * <td>Measures the RGB color of a surface</td>
 * <td>N/A, Normalized to (0-1)</td>
 * <td> {@link #getRGBMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a
 *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NCO1038">
 *      Sensor Product page </a>
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
public class HiTechnicColorSensor extends I2CSensor implements ColorIdentifier {

  private byte[] buf      = new byte[3];

  // TODO: Problem: The following table ignores pastels and other subtle colors
  // HiTechnic can detect.
  // Converting to limited JSE color set means this generic interface isn't as
  // rich at describing color
  // ID as it could be.
  private int[]  colorMap = { Color.BLACK, Color.MAGENTA, Color.MAGENTA,
      Color.BLUE, Color.GREEN, Color.YELLOW, Color.YELLOW, Color.ORANGE,
      Color.RED, Color.RED, Color.MAGENTA, Color.MAGENTA, Color.YELLOW,
      Color.PINK, Color.PINK, Color.PINK, Color.MAGENTA, Color.WHITE };

  public HiTechnicColorSensor(I2CPort port) {
    super(port);
    init();
  }

  public HiTechnicColorSensor(Port port) {
    super(port);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new ColorIDMode(), new RGBMode() });
  }

  // INDEX VALUES

  /**
   * Returns the color index detected by the sensor.
   * 
   * @return Color index.<br>
   *         <li>0 = red <li>1 = green <li>2 = blue <li>3 = yellow <li>4 =
   *         magenta <li>5 = orange <li>6 = white <li>7 = black <li>8 = pink <li>
   *         9 = gray <li>10 = light gray <li>11 = dark gray <li>12 = cyan
   */
  @Override
  public int getColorID() {
    getData(0x42, buf, 1);
    int HT_val = (0xFF & buf[0]);
    return colorMap[HT_val];
  }

  /**
   * <b>HiTechnic color sensor, Color ID mode</b><br>
   * Measures the color ID of a surface.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing a color ID.
   *         <li>0 = red <li>1 = green <li>2 = blue <li>3 = yellow <li>4 =
   *         magenta <li>5 = orange <li>6 = white <li>7 = black <li>8 = pink <li>
   *         9 = gray <li>10 = light gray <li>11 = dark gray <li>12 = cyan
   */  
  public SensorMode getColorIDMode() {
    return getMode(0);
  }

  /**
   * <b>HiTechnic color sensor, Color ID mode</b><br>
   * Measures the color of a surface.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains three elements containing the color expressed in RGB values (0-255) of the measured surface.
   */  public SensorMode getRGBMode() {
    return getMode(1);
  }

  private class ColorIDMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      sample[offset] = (float) getColorID();
    }

    @Override
    public String getName() {
      return "ColorID";
    }
  }

   public class RGBMode implements SensorMode {

    @Override
    public int sampleSize() {
      return 3;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      getData(0x43, buf, 3);
      for (int i = 0; i < 3; i++)
        sample[offset + i] = ((float) (0xFF & buf[i])) / 256f;
    }

    @Override
    public String getName() {
      return "RGB";
    }
  }

}
