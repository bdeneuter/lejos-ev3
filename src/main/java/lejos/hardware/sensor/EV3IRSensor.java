package lejos.hardware.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;


/**
 * <b>EV3 Infra Red sensor</b><br>
 * The digital EV3 Infrared Seeking Sensor detects proximity to the robot and reads signals emitted by the EV3 Infrared Beacon. The sensor can alse be used as a receiver for a Lego Ev3 IR remote control.
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
 * <td>Distance</td>
 * <td>Measures the distance to an object in front of the sensor</td>
 * <td>Undefined</td>
 * <td> {@link #getDistanceMode() }</td>
 * </tr>
 * <tr>
 * <td>Seek</td>
 * <td>Locates up to four beacons</td>
 * <td>Undefined, undefined</td>
 * <td> {@link #getSeekMode() }</td>
 * </tr>
 * </table><p>
 * 
 * <b>EV3 Infra Red sensor</b><br>
 * 
 * The sensor can be used as a receiver for up to four Lego Ev3 IR remote controls using the {@link #getRemoteCommand} and {@link #getRemoteCommands} methods.
*  
 * 
 * See <a href="http://www.ev-3.net/en/archives/848"> Sensor Product page </a>
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author andy
 * 
 */
public class EV3IRSensor extends UARTSensor 
{
    protected final static int IR_PROX = 0;
    protected final static int IR_SEEK = 1;
    protected final static int IR_REMOTE = 2;
    
    protected final static int SWITCH_DELAY = 250;
    
    public final static int IR_CHANNELS = 4;
    
    protected byte [] remoteVals = new byte[IR_CHANNELS];
    
    protected void init()
    {
        setModes(new SensorMode[] {new DistanceMode(), new SeekMode()});
    }
    
    public EV3IRSensor(UARTPort port)
    {
        super(port, IR_PROX);
        init();
    }
    
    public EV3IRSensor(Port port)
    {
        super(port, IR_PROX);
        init();
    }
    
    
    /**
     * Return the current remote command from the specified channel. Remote commands
     * are a single numeric value  which represents which button on the Lego IR
     * remote is currently pressed (0 means no buttons pressed). Four channels are
     * supported (0-3) which correspond to 1-4 on the remote. The button values are:<br>
     * 1 TOP-LEFT<br>
     * 2 BOTTOM-LEFT<br>
     * 3 TOP-RIGHT<br>
     * 4 BOTTOM-RIGHT<br>
     * 5 TOP-LEFT + TOP-RIGHT<br>
     * 6 TOP-LEFT + BOTTOM-RIGHT<br>
     * 7 BOTTOM-LEFT + TOP-RIGHT<br>
     * 8 BOTTOM-LEFT + BOTTOM-RIGHT<br>
     * 9 CENTRE/BEACON<br>
     * 10 BOTTOM-LEFT + TOP-LEFT<br>
     * 11 TOP-RIGHT + BOTTOM-RIGHT<br>
     * @param chan channel to obtain the command for
     * @return the current command
     */
    public int getRemoteCommand(int chan)
    {
        switchMode(IR_REMOTE, SWITCH_DELAY);
        port.getBytes(remoteVals, 0, remoteVals.length);
        return remoteVals[chan] & 0xff;
    }

    /**
     * Obtain the commands associated with one or more channels. Each element of 
     * the array contains the command for the associated channel (0-3).
     * @param cmds the array to store the commands
     * @param offset the offset to start storing
     * @param len the number of commands to store.
     */
    public void getRemoteCommands(byte[] cmds, int offset, int len)
    {
        switchMode(IR_REMOTE, SWITCH_DELAY);
        port.getBytes(cmds, offset, len);
    }
    
    
    /**
     * <b>EV3 Infra Red sensor, Distance mode</b><br>
     * Measures the distance to an object in front of the sensor.
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element giving the distance to an object in front of the sensor. The distance provided is very roughly equivalent to meters
     * but needs conversion to give better distance. See product page for details. <br>
     * The effective range of the sensor in Distance mode  is about 5 to 50 centimeters. Outside this range a zero is returned
     * for low values and positive infinity for high values.
     * 
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a href="http://www.ev-3.net/en/archives/848"> Sensor Product page </a>
     */    public SensorMode getDistanceMode()
    {
        return getMode(0);
    }
    
    
    private class DistanceMode implements SensorMode {
        private static final float toSI = 1f;

        @Override
        public int sampleSize() 
        {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset) 
        {
            switchMode(IR_PROX, SWITCH_DELAY);
            int raw=((int)port.getByte() & 0xff);
            if (raw<5) sample[offset]=0;
            else if (raw>55) sample[offset]=Float.POSITIVE_INFINITY;
            else sample[offset]=raw*toSI;
        }

        @Override
        public String getName() 
        {
            return "Distance";
        }
        
    }

    
    /**
     * <b>EV3 Infra Red sensor, Seek mode</b><br>
     * In seek mode the sensor locates up to four beacons and provides bearing and distance of each beacon.
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains four pairs of elements in a single sample. Each pair gives bearing of  and distance to the beacon. 
     * The first pair of elements is associated with a beacon transmitting on channel 0, the second pair with a beacon transmitting on channel 1 etc.<br>
     * The bearing values range from -25 to +25 (with values increasing clockwise
     * when looking from behind the sensor). A bearing of 0 indicates the beacon is
     * directly in front of the sensor. <br>
     * Distance values are not to scale. Al increasing values indicate increasing distance. <br>
     * If no beacon is detected both bearing is set to zero, and distance to positive infinity.
     * 
     * <p>
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     * See <a href="http://www.ev-3.net/en/archives/848"> Sensor Product page </a>
     */    public SensorMode getSeekMode()
    {
        return getMode(1);
    }

    private class SeekMode implements SensorMode 
    {
        private static final float toSI = 1f;
        byte []seekVals = new byte[8];

        @Override
        public int sampleSize() 
        {
            return 8;
        }

        @Override
        public void fetchSample(float[] sample, int offset) 
        {
              switchMode(IR_SEEK, SWITCH_DELAY);
              port.getBytes(seekVals, 0, seekVals.length);
              for(int i = 0; i < seekVals.length; i += 2)
              {
                  int raw=(int)seekVals[i+1] & 0xff;
                  if (raw == 128) {
                      sample[offset++] = 0; 
                      sample[offset++] = Float.POSITIVE_INFINITY; 
                  }
                  else {
                  sample[offset++] = seekVals[i] * toSI;
                  sample[offset++] = (int)seekVals[i+1] & 0xff;
                  }
              }
        }

        @Override
        public String getName() 
        {
            return "Seek";
        }

    }
    
}
