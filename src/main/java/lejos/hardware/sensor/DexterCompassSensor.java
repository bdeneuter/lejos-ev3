package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.utility.EndianTools;

/**
 * <b>Dexter Industries dCompass sensor</b><br>
 * A three axis magnetometer
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
 * <td>Magnetic</td>
 * <td>Measures the strength of the magnetic field over three axes</td>
 * <td></td>
 * <td> {@link #getMagneticMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * Range can be set Using getRanges() and setRange methods. Internal update
 * frequency of the sensor can be set using getRates and setRate methods.
 * 
 * <p>
 * 
 * See <a
 *      href="http://www.adafruit.com/datasheets/HMC5883L_3-Axis_Digital_Compass_IC.pdf">
 *      Sensor datasheet </a>
 * See <a href="http://www.dexterindustries.com/dCompass.html"> Sensor Product
 *      page </a>
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
public class DexterCompassSensor extends I2CSensor implements SensorModes{

    // sensor configuration
    static final int             MODE_NORMAL        = 0;
    static final int             MODE_POSITIVE_BIAS = 1;
    static final int             MODE_NEGATIVE_BIAS = 2;
    private final static float[] RATES              = { 0.75f, 1.5f, 3, 7.5f, 15, 30, 75 };
    private final static int[]   RANGEMULTIPLIER    = { 1370, 1090, 820, 660, 440, 390, 330, 230 };
    private final static float[] RANGES             = { 0.88f, 1.3f, 1.9f, 2.5f, 4, 4.7f, 5.6f, 8.1f };
    static final int             CONTINUOUS         = 0;
    static final int             SINGLE             = 1;
    static final int             IDLE               = 2;

    // default configuration
    int                          measurementMode    = MODE_NORMAL;
    int                          range              = 6;
    int                          rate               = 5;
    int                          operatingMode      = CONTINUOUS;

    // sensor register adresses
    private static final int     I2C_ADDRESS        = 0x3C;
    protected static final int   REG_CONFIG         = 0x00;
    protected static final int   REG_MAGNETO        = 0x03;
    protected static final int   REG_STATUS         = 0x09;

    // local variables for common use
    float[]                      raw                = new float[3];
    float[]                      dummy              = new float[3];
    byte[]                       buf                = new byte[6];
    private float                multiplier;

    /**
     * Constructor for the driver.
     * 
     * @param port
     */
    public DexterCompassSensor(I2CPort port) {
        super(port, I2C_ADDRESS);
        init();
    }

    public DexterCompassSensor(Port port) {
        super(port, I2C_ADDRESS, TYPE_LOWSPEED);
        init();
    }

    /**
     * <b>Dexter Industries dCompass sensor, magnetic mode</b><br>
     * Measures the strength of the magnetic field over three axes
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains 3 elements. Each element gives the strength of the
     * magnetic field (in Gueass). Axis order is X, Y, Z.
     * 
     * <p>
     * <b>Configuration</b><br>
     * By default the sensor is configured for a range of 5.6 Gauss and an
     * update frequency of 30 Hertz. <br>
     * The sensor can be tested using the test method.
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a
     *      href="http://www.adafruit.com/datasheets/HMC5883L_3-Axis_Digital_Compass_IC.pdf">
     *      Sensor datasheet </a>
     */
    public SampleProvider getMagneticMode() {
        return getMode(0);
    }

    protected void init() {
        setModes(new SensorMode[] { new MagneticMode() });
        configureSensor();
    }

    /**
     * Sets the configuration registers of the sensor according to the current
     * settings
     */
    private void configureSensor() {

        buf[0] = (byte) ((3 << 5) | (rate << 2) | measurementMode);
        buf[1] = (byte) (range << 5);
        buf[2] = (byte) (operatingMode);

        sendData(REG_CONFIG, buf, 3);

        Delay.msDelay(6);

        multiplier = 1.0f / RANGEMULTIPLIER[range];
        // first measurement after configuration is not yet configured properly;
        Delay.msDelay(6);
        getMode(0).fetchSample(dummy, 0);
    }

    /**
     * fetches measurement in single measurement mode
     * 
     * @param ret
     */
    private void fetchSingleMeasurementMode(float[] ret, int offset) {
        buf[0] = 0x01;
        sendData(0x02, buf[0]);
        Delay.msDelay(6);
        fetch(ret, offset);
    }

    /**
     * @return Returns the measurement mode of the sensor (normal, positive bias
     *         or negative bias).
     *         <p>
     *         positive and negative bias mode should only be used for testing
     *         the sensor.
     */
    protected int getMeasurementMode() {
        return measurementMode;
    }

    /**
     * @return The operating mode of the sensor (single measurement, continuous
     *         or Idle)
     */
    protected int getOperatingMode() {
        return operatingMode;
    }

    /**
     * @return The dynamic range of the sensor.
     */
    public float getMaximumRange() {
        return RANGES[range];
    }

    /**
     * Reads the new data ready bit of the status register of the sensor.
     * 
     * @return True if new data available
     */
    protected boolean newDataAvailable() {
        getData(REG_STATUS, buf, 1);
        return ((buf[0] & 0x01) != 0);
    }

    /**
     * @param measurementMode
     *            Sets the measurement mode of the sensor.
     */
    protected void setMeasurementMode(int measurementMode) {
        this.measurementMode = measurementMode;
        configureSensor();
    }

    /**
     * Sets the operating mode of the sensor
     * 
     * @param operatingMode
     *            Continuous is normal mode of operation
     *            <p>
     *            SingleMeasurement can be used to conserve energy or to
     *            increase maximum measurement rate
     *            <p>
     *            Idle is to stop the sensor and conserve energy
     */
    protected void setOperatingMode(int operatingMode) {
        this.operatingMode = operatingMode;
        configureSensor();
    }

    /**
     * Sets the dynamic range of the sensor (1.3 Gauss is default).
     * 
     * @param range
     */
    public void setRange(int range) {
        this.range = (byte) range;
        configureSensor();
    }

    /**
     * Self-test routine of the sensor.
     * 
     * @return An array of boolean values. A true indicates the sensor axis is
     *         working properly.
     */
    public boolean[] test() {
        boolean[] ret = new boolean[3];

        // store current settings;
        int currentMode = measurementMode;
        int currentRange = range;
        int currentOperatingMode = operatingMode;

        // modify settings for testing;
        measurementMode = MODE_POSITIVE_BIAS;
        range = 5;
        operatingMode = SINGLE;
        configureSensor();

        // get measurement
        buf[0] = 0x01;
        sendData(0x02, buf[0]);
        Delay.msDelay(6);
        fetch(dummy, 0);

        // test for limits;
        for (int axis = 0; axis < 3; axis++)
            if (dummy[axis] > 243 && dummy[axis] < 575)
                ret[axis] = true;
            else
                ret[axis] = false;

        // restore settings;
        measurementMode = currentMode;
        range = currentRange;
        operatingMode = currentOperatingMode;
        configureSensor();

        return ret;
    }

    public void setSampleRate(float rate) {
        for (int i = 0; i < RATES.length; i++)
            if (RATES[i] == rate)
                rate = i;
        configureSensor();
    }

    public float[] getSampleRates() {
        return RATES;
    }

    public void start() {
        this.setOperatingMode(CONTINUOUS);
    }

    public void stop() {
        this.setOperatingMode(IDLE);
    }

    public float getSampleRate() {
        return RATES[rate];
    }

    public void setRange(float range) {
        for (int i = 0; i < RANGES.length; i++)
            if (RANGES[i] == range)
                range = i;
        configureSensor();
    }

    public float[] getRanges() {
        return RANGES;
    }

    private void fetch(float[] ret, int offset) {
        // The order of data registers seems to be X,Z,Y. (Aswin).
        getData(REG_MAGNETO, buf, 6);
        ret[0 + offset] = EndianTools.decodeShortBE(buf, 0) * multiplier;
        ret[1 + offset] = EndianTools.decodeShortBE(buf, 4) * multiplier;
        ret[2 + offset] = EndianTools.decodeShortBE(buf, 2) * multiplier;
    }

    private class MagneticMode implements SensorMode {

        /**
         * Fills an array of floats with measurements from the sensor in the
         * specified unit.
         * <p>
         * The array order is X, Y, Z
         * <P>
         * When the sensor is idle zeros will be returned.
         */
        public void fetchSample(float[] sample, int offset) {
            // get raw data
            switch (operatingMode) {
                case (SINGLE):
                    fetchSingleMeasurementMode(sample, offset);
                    break;
                case (CONTINUOUS):
                    fetch(sample, offset);
                    break;
                default:
                    for (int axis = 0; axis < 3; axis++)
                        sample[axis + offset] = Float.NaN;
                    break;
            }
        }

        public int sampleSize() {
            return 3;
        }

        @Override
        public String getName() {
            return "Magnetic";
        }

    }

}
