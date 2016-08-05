package lejos.hardware.sensor;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;
import lejos.utility.EndianTools;

/**
 * <b>Dexter dGPS sensor</b><br>
 * Sends GPS coordinates to your robot and calculates navigation information
 * <p style="color:red;">
 * The code for this sensor has not been tested. Please report test results to the <A href="http://www.lejos.org/forum/"> leJOS forum</a>.
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
 * <td>Position</td>
 * <td>Gets the coordinates of the sensor</td>
 * <td>latitude and longitude</td>
 * <td> {@link #getPositionMode() }</td>
 * </tr>
 * <tr>
 * <td>Angle</td>
 * <td>Gets the heading of the sensor</td>
 * <td>degrees</td>
 * <td> {@link #getAngleMode() }</td>
 * </tr>
 * <tr>
 * <td>Velocity</td>
 * <td>Gets the velocity (speed) of the sensor</td>
 * <td>metres/second</td>
 * <td> {@link #getVelocityMode() }</td>
 * </tr>
 * <tr>
 * <td>Time</td>
 * <td>gets the time</td>
 * <td>UTC (hhmmss)</td>
 * <td> {@link #getTimeMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * <b>Sensor configuration</b><br>
 * There are no configurable parameters.<br>
 * The status of the sensor can be checked using the {@link #linkStatus} method.
 * 
 * <p>
 * 
 * See <a href="http://www.dexterindustries.com/manual/dgps-2/"> Sensor datasheet </a>
 * See <a href="http://www.dexterindustries.com/dGPS.html"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Mark Crosbie <mark@mastincrosbie.com>
 * 
 */
public class DexterGPSSensor extends I2CSensor {

    public static final byte DGPS_I2C_ADDR   = 0x06;       /* !< Barometric sensor device address */
    public static final byte DGPS_CMD_UTC    = 0x00;       /* !< Fetch UTC */
    public static final byte DGPS_CMD_STATUS = 0x01;       /* !< Status of satellite link: 0 no link, 1 link */
    public static final byte DGPS_CMD_LAT    = 0x02;       /* !< Fetch Latitude */
    public static final byte DGPS_CMD_LONG   = 0x04;       /* !< Fetch Longitude */
    public static final byte DGPS_CMD_VELO   = 0x06;       /* !< Fetch velocity in cm/s */
    public static final byte DGPS_CMD_HEAD   = 0x07;       /* !< Fetch heading in degrees */
    public static final byte DGPS_CMD_DIST   = 0x08;       /* !< Fetch distance to destination */
    public static final byte DGPS_CMD_ANGD   = 0x09;       /* !< Fetch angle to destination */
    public static final byte DGPS_CMD_ANGR   = 0x0A;       /* !< Fetch angle travelled since last request */
    public static final byte DGPS_CMD_SLAT   = 0x0B;       /* !< Set latitude of destination */
    public static final byte DGPS_CMD_SLONG  = 0x0C;       /* !< Set longitude of destination */

    private byte             reply[]         = new byte[4];

    /**
     * Constructor
     * 
     * @param i2cPort
     *            the i2c port the sensor is connected to
     */
    public DexterGPSSensor(I2CPort i2cPort) {
        super(i2cPort, DGPS_I2C_ADDR);
        init();
    }

    /**
     * Constructor
     * 
     * @param sensorPort
     *            the sensor port the sensor is connected to
     */
    public DexterGPSSensor(Port sensorPort) {
        super(sensorPort, DGPS_I2C_ADDR);
        init();
    }

    protected void init() {
        setModes(new SensorMode[] { new PositionMode(), new AngleMode(), new VelocityMode(), new TimeMode() });
    }

    /**
     * Return status of link to the GPS satellites LED on dGPS should light if satellite lock acquired
     * 
     * @return true if GPS link is up, else false
     */
    public boolean linkStatus() {
        this.getData(DGPS_CMD_STATUS, reply, 0, 1);
        return (reply[0] == 1);
    }

    /**
     * <b>Dexter dGPS sensor, Position mode</b><br>
     * Gets the coordinates of the sensor
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains 2 elements. The first element is latitude, the second longitude. <br>
     * The sensor uses an integer-based representation of latitude and longitude values. Assume that you want to convert the value of 77 degrees, 2 minutes and 54.79 seconds to the integer-based
     * representation. The integer value is computed as follows: <code>R = 1000000 * (D + M / 60 + S / 3600)</code> where <code>D=77</code>, <code>M=2</code>, and <code>S=54.79</code>. For the given
     * values, the formula yields the integer value 77048553. Basically, this is equivalent to decimal degrees times a million.
     * 
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for SampleProviders}
     * See <a href="http://www.dexterindustries.com/manual/dgps-2/"> Sensor datasheet </a>
     */
    public SampleProvider getPositionMode() {
        return getMode(0);
    }

    private class PositionMode implements SensorMode {

        @Override
        public int sampleSize() {
            return 2;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(DGPS_CMD_LAT, reply, 0, 4);
            sample[0 + offset] = EndianTools.decodeIntBE(reply, 0) / 1000000f;

            getData(DGPS_CMD_LONG, reply, 0, 4);
            sample[1 + offset] = EndianTools.decodeIntBE(reply, 0) / 1000000f;

        }

        @Override
        public String getName() {
            return "Position";
        }

    }

    /**
     * <b>Dexter dGPS sensor, Angle mode</b><br>
     * Gets the heading of the sensor
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element representing the heading (in degrees) of the sensor. Accurate heading information can only be given when the sensor is in motion.
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for SampleProviders}
     * See <a href="http://www.dexterindustries.com/manual/dgps-2/"> Sensor datasheet </a>
     */
    public SampleProvider getAngleMode() {
        return getMode(1);
    }

    private class AngleMode implements SensorMode {

        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(DGPS_CMD_HEAD, reply, 0, 2);
            sample[offset] = -EndianTools.decodeUShortBE(reply, 0);
        }

        @Override
        public String getName() {
            return "Angle";
        }

    }

    /**
     * <b>Dexter dGPS sensor, Velocity mode</b><br>
     * Gets the velocity (speed) of the sensor
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one elements giving the speed of the sensor (in metres/second).
     * 
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for SampleProviders}
     * See <a href="http://www.dexterindustries.com/manual/dgps-2/"> Sensor datasheet </a>
     */
    public SampleProvider getVelocityMode() {
        return getMode(2);
    }

    private class VelocityMode implements SensorMode {

        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(DGPS_CMD_VELO, reply, 1, 3);
            reply[0] = 0;
            sample[offset] = EndianTools.decodeIntBE(reply, 0) / 100f;
        }

        @Override
        public String getName() {
            return "Velocity";
        }

    }

    /**
     * <b>Dexter dGPS sensor, Time mode</b><br>
     * gets the UTC time from the sensor
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one elements representing the UTC time (hhmmss) .
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for SampleProviders}
     * See <a href="http://www.dexterindustries.com/manual/dgps-2/"> Sensor datasheet </a>
     */
    public SampleProvider getTimeMode() {
        return getMode(3);
    }

    private class TimeMode implements SensorMode {

        @Override
        public int sampleSize() {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) {
            getData(DGPS_CMD_UTC, reply, 0, 4);
            sample[offset] = EndianTools.decodeIntBE(reply, 0);
        }

        @Override
        public String getName() {
            return "Time";
        }

    }

}
