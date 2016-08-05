package lejos.hardware.sensor;

import lejos.hardware.port.Port;
import lejos.robotics.*;

/**
 * <b>LEGO RCX light Sensor</b><br>
 * The RCX light sensor measures light levels of reflected or ambient light.
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
 * <td>Red</td>
 * <td>Measures the light value when illuminated with a red light source.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getRedMode() }</td>
 * </tr>
 * <tr>
 * <td>Ambient</td>
 * <td>Measures the light value of ambient light.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getAmbientMode() }</td>
 * </tr>
 * </table>
 * 
 * <p>
 * 
 */
public class RCXLightSensor extends AnalogSensor implements SensorConstants,
    LampController {
  private static final int SWITCH_DELAY = 10;
  private boolean          floodlight   = false;

  /**
   * Create an RCX light sensor object attached to the specified port. The
   * sensor will be activated, i.e. the LED will be turned on.
   * 
   * @param port
   *          port, e.g. Port.S1
   */
  public RCXLightSensor(Port port) {
    super(port);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new RedMode(), new AmbientMode() });
    port.setTypeAndMode(TYPE_REFLECTION, MODE_RAW);
    setFloodlight(true);
  }

  public int getFloodlight() {
    if (this.floodlight == true)
      return Color.RED;
    else
      return Color.NONE;
  }

  public boolean isFloodlightOn() {
    return floodlight;
  }

  public void setFloodlight(boolean floodlight) {
    this.floodlight = floodlight;
    if (floodlight == true)
      switchType(TYPE_REFLECTION, SWITCH_DELAY);
    else
      switchType(TYPE_CUSTOM, SWITCH_DELAY);
  }

  public boolean setFloodlight(int color) {
    if (color == Color.RED) {
      setFloodlight(true);
      return true;
    } else if (color == Color.NONE) {
      setFloodlight(false);
      return true;
    } else
      return false;
  }

  /**
   * get a sample provider the returns the light value when illuminated with a
   * Red light source.
   * 
   * @return the sample provider
   */
  public SensorMode getRedMode() {
    return getMode(0);
  }

  /**
   * get a sample provider the returns the light value when illuminated without
   * a light source.
   * 
   * @return the sample provider
   */
  public SensorMode getAmbientMode() {
    return getMode(1);
  }

  private class RedMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      setFloodlight(true);
      sample[offset] = 1.0f - normalize(port.getPin1());
    }

    @Override
    public String getName() {
      return "Red";
    }
  }

  private class AmbientMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      setFloodlight(false);
      sample[offset] = 1.0f - normalize(port.getPin1());
    }

    @Override
    public String getName() {
      return "Ambient";
    }
  }
}
