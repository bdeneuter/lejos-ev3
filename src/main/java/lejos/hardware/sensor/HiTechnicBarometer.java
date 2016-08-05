package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.utility.EndianTools;

/**
 * <b>Hitechnic Barometric sensor</b><br>
 * The sensor measures both atmospheric pressure and temperature.
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
 * <td>Pressure</td>
 * <td>Measures atmospheric pressure</td>
 * <td>Pascal</td>
 * <td> {@link #getPressureMode() }</td>
 * </tr>
 * <tr>
 * <td>Temperature</td>
 * <td>Measures temperature</td>
 * <td>Degree Celcius</td>
 * <td> {@link #getTemperatureMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * The sensor can be calibrated for pressure using the calibrate method.
 * <p>
 * 
 * See <a
 *      href="http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NBR1036">
 *      Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class HiTechnicBarometer extends I2CSensor {

  private static final int REG_TEMPERATURE          = 0x42;
  private static final int REG_PRESSURE             = 0x44;
  private static final int REG_PRESSURE_CALIBRATION = 0x46;
  // Wikipedia: 1 inHg at 0 °C = 3386.389 Pa
  private static final float MINHG_TO_PA                   = 3.38638866667f;
  // Wikipedia: Standard Atmosphere is 101325 Pa
  private static final float STANDARD_ATMOSPHERIC_PRESSURE = 101325f;

  private final byte[]     buffer                          = new byte[2];

  /**
   * Constructor.
   * 
   * @param port
   *          the {@link I2CPort} the sensor is connected to.
   */
  public HiTechnicBarometer(final I2CPort port) {
    super(port, DEFAULT_I2C_ADDRESS);
  }

  /**
   * Constructor.
   * 
   * @param port
   *          the {@link I2CPort} the sensor is connected to.
   * @param address
   *          the address
   */
  public HiTechnicBarometer(final I2CPort port, final int address) {
    super(port, address);
    init();
  }

  public HiTechnicBarometer(final Port port, final int address) {
    super(port, address, TYPE_LOWSPEED);
    init();
  }

  public HiTechnicBarometer(final Port port) {
    this(port, DEFAULT_I2C_ADDRESS);
    init();
  }

  protected void init() {
    setModes(new SensorMode[] { new PressureMode(), new TemperatureMode() });
    if (getCalibrationMetric() == 0)
      calibrate(STANDARD_ATMOSPHERIC_PRESSURE);
  }

  /**
   * Re-calibrates the sensor.
   * 
   * @param pascals
   *          the calibration value in pascals
   */
  public void calibrate(float pascals) {
	// compute calibration value in 1/1000 inHg
    int calibrationImperial = (int)(0.5f + pascals / MINHG_TO_PA);
    EndianTools.encodeShortBE(calibrationImperial, buffer, 0);
    sendData(REG_PRESSURE_CALIBRATION, buffer, 2);
  }
  
  /**
   * @return the present calibration value in pascals. Will be 0 in case no
   *         explicit calibration has been performed.
   */
  public float getCalibrationMetric() {
	// get calibration value in 1/1000 inHg
    getData(REG_PRESSURE_CALIBRATION, buffer, 2);
    int result = EndianTools.decodeUShortBE(buffer, 0);
    return result * MINHG_TO_PA;
  }

  /**
   * <b>HiTechnic Barometer, Pressure mode</b><br>
   * Measures the atmospheric pressure of the air.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the atmospheric pressure (in Pascal) of the air.
   */  
  public SensorMode getPressureMode() {
    return getMode(0);
  }

  private class PressureMode implements SensorMode {

    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public String getName() {
      return "Pressure";
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      // get pressure in 1/1000 inHg
      getData(REG_PRESSURE, buffer, 2);
      int result = EndianTools.decodeUShortBE(buffer, 0);
      sample[0] = result * MINHG_TO_PA;
    }

  }

  /**
   * <b>HiTechnic Barometer, Temperature mode</b><br>
   * Measures the temperature of the air.
   * 
   * <p>
   * <b>Size and content of the sample</b><br>
   * The sample contains one element containing the air temperature (in degree celcius).
   */    
  public SensorMode getTemperatureMode() {
    return getMode(1);
  }

  private class TemperatureMode implements SensorMode {
    @Override
    public int sampleSize() {
      return 1;
    }

    @Override
    public void fetchSample(float[] sample, int offset) {
      // get temperature in 1/10 °C
      getData(REG_TEMPERATURE, buffer, 2);
      int result = EndianTools.decodeShortBE(buffer, 0);
      sample[offset] = result / 10f + 273.15f;
    }

    @Override
    public String getName() {
      return "Temperature";
    }
  }
}