package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lejos.utility.EndianTools;

/**
 * <b>Micro Infinity Cruizcore XG1300L</b><br>
 * The XG1300L is a fully self-contained digital MEMS gyroscope and
 * accelerometer.
 * <p style="color:red;">
 * The code for this sensor has not been tested. Please report test results to
 * the <A href="http://www.lejos.org/forum/"> leJOS forum</a>.
 * </p>
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
 * 
 * <td>Acceleration</td>
 * <td>Measures linear acceleration over three axes</td>
 * <td></td>
 * <td> {@link #getAccelerationMode() }</td>
 * </tr>
 * <tr>
 * Rate
 * <td>Rate</td>
 * <td>Measures rate of turn over the Z-axis</td>
 * <td></td>
 * <td> {@link #getRateMode() }</td>
 * </tr>
 * <tr>
 * Angle
 * <td></td>
 * <td>Measures angle over the Z-axis</td>
 * <td></td>
 * <td> {@link #getAngleMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * 
 * <p>
 * 
 * See <a
 *      href="http://www.minfinity.com/Manual/CruizCore_XG1300L_User_Manual.pdf">
 *      Sensor datasheet </a>
 * See <a href="http://www.minfinity.com/eng/page.php?Main=1&sub=1&tab=5">
 *      Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Daniele Benedettelli
 * 
 */
public class CruizcoreGyro extends I2CSensor {

    /*
     * Documentation can be obtained here:
     * http://www.minfinity.com/Manual/CruizCore_XG1300L_User_Manual.pdf The
     * documentation and the conversion in the NXC sample code indicate, that
     * 16bit signed little endian values are returned.
     */

    private byte[]            inBuf        = new byte[11];
    private static final byte GYRO_ADDRESS = 0x02;

    // values returned are signed short integers multiplied by 100
    private static final byte ANGLE        = 0x42;        // 0x43 (2 Bytes)

    private static final byte RATE         = 0x44;        // 0x45 (2 Bytes)

    private static final byte ACCEL_X      = 0x46;        // 0x47 (2 Bytes)
                                                           // private static
                                                           // final
                                                           // byte ACCEL_Y =
                                                           // 0x48;
                                                           // // 0x49 (2 Bytes)
                                                           // private static
                                                           // final
                                                           // byte ACCEL_Z =
                                                           // 0x4A;
                                                           // // 0x4B (2 Bytes)

    // the commands are issued by just reading these registers

    private static final byte RESET        = 0x60;

    private static final byte SELECT_SCALE = 0x61;

    private float             scale;

    /**
     * Instantiates a new Cruizcore Gyro sensor.
     * 
     * @param port
     *            the port the sensor is attached to
     */
    public CruizcoreGyro(I2CPort port) {
        super(port, GYRO_ADDRESS);
        init();
    }

    public CruizcoreGyro(Port port) {
        super(port, GYRO_ADDRESS);
        init();
    }

    protected void init() {
        setAccScale2G();
        setModes(new SensorMode[] { new AccelerationMode(), new RateMode(), new AngleMode() });
    }

    /**
     * Sets the acc scale.
     * 
     * @param sf
     *            the scale factor: 0 for +/- 2G, 1 for +/- 4G, 2 for +/- 8g
     * @throws IllegalArgumentException
     *            if the parameter is neither 0, 1, or 2.
     */
    public void setAccScale(int sf) {
    	if (sf < 0 || sf > 2)
    		throw new IllegalArgumentException();
    	// TODO we write one byte too many (the zero).
    	// The driver should perform a zero length write to register SELECT_SCALE + sf.
        sendData(SELECT_SCALE + sf, (byte) 0);
        scale = 0.00981f * (1 << sf);
    }

    /**
     * Sets the acceleration scale factor to 2G.
     */
    public void setAccScale2G() {
        setAccScale((byte) 0);
    }

    /**
     * Sets the acceleration scale factor to 4G.
     */
    public void setAccScale4G() {
        setAccScale((byte) 1);
    }

    /**
     * Sets the acceleration scale factor to 8G.
     */
    public void setAccScale8G() {
        setAccScale((byte) 2);
    }

    /**
     * Resets the accumulated angle (heading).
     * 
     */
    public void reset() {
        sendData(RESET, (byte) 0);
        Delay.msDelay(750);
    }

    /**
     * <b>Cruizcore XG1300L, Acceleration mode</b><br>
     * Measures linear acceleration over three axes
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains 3 elements. Each element gives linear acceleration
     * (in metres/second^2). Axis order in sample is X, Y, Z.
     * 
     * <p>
     * <b>Configuration</b><br>
     * The sensor can be configured for range using the setAccScale#G() methods
     * of the sensor class.
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a
     *      href="http://www.minfinity.com/Manual/CruizCore_XG1300L_User_Manual.pdf">
     *      Sensor datasheet </a>
     */
    public SensorMode getAccelerationMode() {
        return getMode(0);
    }

    private class AccelerationMode implements SensorMode {

        @Override
        public int sampleSize() {
            return 3;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(ACCEL_X, inBuf, 6);
            sample[0 + offset] = EndianTools.decodeShortLE(inBuf, 2) * scale;
            sample[1 + offset] = EndianTools.decodeShortLE(inBuf, 0) * scale;
            sample[2 + offset] = -EndianTools.decodeShortLE(inBuf, 4) * scale;
        }

        @Override
        public String getName() {
            return "Acceleration";
        }
    }

    /**
     * <b>Cruizcore XG1300L, Acceleration mode</b><br>
     * Measures rate of turn over the Z-axis
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element, the rate of turn (in degrees / second)
     * over the Z-axis.
     * 
     * <p>
     * <b>Configuration</b><br>
     * There are no configurable parameters.
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a
     *      href="http://www.minfinity.com/Manual/CruizCore_XG1300L_User_Manual.pdf">
     *      Sensor datasheet </a>
     */
    public SensorMode getRateMode() {
        return getMode(1);
    }

    private class RateMode implements SensorMode {
        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(RATE, inBuf, 2);
            sample[offset] = -EndianTools.decodeShortLE(inBuf, 0) / 100f;
        }

        @Override
        public String getName() {
            return "Rate";
        }
    }

    /**
     * <b>Cruizcore XG1300L, Angle mode</b><br>
     * Measures angle over the Z-axis
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element, the accumulated angle (in degrees). .
     * 
     * <p>
     * <b>Configuration</b><br>
     * The accumulated angle can be reset to zero using the reset() method of
     * the sensor class.
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a
     *      href="http://www.minfinity.com/Manual/CruizCore_XG1300L_User_Manual.pdf">
     *      Sensor datasheet </a>
     */
    public SensorMode getAngleMode() {
        return getMode(2);
    }

    private class AngleMode implements SensorMode {
        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(ANGLE, inBuf, 2);
            sample[offset] = 360 - EndianTools.decodeShortLE(inBuf, 0) / 100f;
        }

        @Override
        public String getName() {
            return "Angle";
        }
    }
}
