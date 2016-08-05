package lejos.hardware.sensor;

import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.Port;
import lejos.robotics.*;

/**
 * <b>LEGO NXT Color Sensor</b><br>
 * allows the reading of
 * raw color values. The sensor has a tri-color LED and this can
 * be set to output red/green/blue or off. It also has a full mode in which
 * four samples are read (off/red/green/blue) very quickly. These samples can
 * then be combined using the calibration data provided by the device to
 * determine the "LEGO" color currently being viewed.
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
 * <td>Measures the color ID</td>
 * <td>Color ID</td>
 * <td> {@link #getColorIDMode() }</td>
 * </tr>
 * <tr>
 * <td>Red</td>
 * <td>Measures the light value when illuminated with a red light source.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getRedMode() }</td>
 * </tr>
 * <tr>
 * <td>Green</td>
 * <td>Measures the light value when illuminated with a green light source.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getGreenMode() }</td>
 * </tr>
 * <tr>
 * <td>Blue</td>
 * <td>Measures the light value when illuminated with a blue light source.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getBlueMode() }</td>
 * </tr>
 * <tr>
 * <td>RGB</td>
 * <td>Measures the light value when illuminated with a white light source.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getRGBMode}</td>
 * </tr>
 * <tr>
 * <td>Ambient</td>
 * <td>Measures the light value of ambient light.</td>
 * <td>N/A, normalized</td>
 * <td> {@link #getAmbientMode() }</td>
 * </tr>
 * </table>
 * 
 * 
 * <p>
 * 
 * See <a href="http://sourceforge.net/p/lejos/wiki/Sensor%20Framework/"> The
 *      leJOS sensor framework</a>
 * See {@link lejos.robotics.SampleProvider leJOS conventions for
 *      SampleProviders}
 * 
 *      <p>
 * 
 * 
 * @author Andy
 * 
 */

public class NXTColorSensor extends AnalogSensor implements SensorConstants,  LampController, ColorIdentifier
{
    protected static final long SWITCH_DELAY = 10;
    protected static int[] colorMap =
    {
        -1, Color.BLACK, Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED, Color.WHITE
    };
    private float[] ADRaw = new float[5];

    private class ModeProvider implements SensorMode
    {
        final String name;
        final int type;
        final int sampleSize;
        final int startOffset;
        
        ModeProvider(String name, int typ, int sz, int offset)
        {
            this.name = name;
            type = typ;
            sampleSize = sz;
            startOffset = offset;
        }

        @Override
        public int sampleSize()
        {
            return sampleSize;
        }

        @Override
        public void fetchSample(float[] sample, int offset)
        {
          switchType(type, SWITCH_DELAY);
          readRaw();
          // return normalized values
          //TODO: Is this the correct way to normalize this output?
          // Normalize to 3.3 the ref voltage on pin 6 on the NXT
          for(int i = 0; i < sampleSize; i++)
              sample[offset+i] = (float)ADRaw[startOffset+i]/3.3f; 
        }

        @Override
        public String getName()
        {
            return name;
        }
        
    }
    
    protected void init()
    {
        setModes(new SensorMode[]{new ColorIDMode(), 
            new ModeProvider("Red", TYPE_COLORRED, 1, 0), 
            new ModeProvider("Green", TYPE_COLORGREEN, 1, 1), 
            new ModeProvider("Blue", TYPE_COLORBLUE, 1, 2), 
            new ModeProvider("RGB", TYPE_COLORFULL, 4, 0), 
            new ModeProvider("None", TYPE_COLORNONE, 1, 3) });        
        setFloodlight(Color.WHITE);
    }
    
    
    /**
     * Create a new Color Sensor instance and bind it to a port.
     * @param port Port to use for the sensor.
     */
    public NXTColorSensor(AnalogPort port)
    {
        super(port);
        init();
    }

    /**
     * Create a new Color Sensor instance and bind it to a port.
     * @param port Port to use for the sensor.
     */
    public NXTColorSensor(Port port)
    {
        super(port);
        init();
    }


    /**
     * get a sample provider in color ID mode
     * @return the sample provider
     */
    public SensorMode getColorIDMode()
    {
        return getMode(0);
    }

    /**
     * get a sample provider the returns the light value when illuminated with a
     * Red light source.
     * @return the sample provider
     */
    public SensorMode getRedMode()
    {
      return getMode(1);
    }

    /**
     * get a sample provider the returns the light value when illuminated with a
     * Green light source.
     * @return the sample provider
     */
    public SensorMode getGreenMode()
    {
      return getMode(2);
    }

    /**
     * get a sample provider the returns the light value when illuminated with a
     * Blue light source.
     * @return the sample provider
     */
    public SensorMode getBlueMode()
    {
      return getMode(3);
    }

    /**
     * get a sample provider the returns the light values (RGB + ambient) when illuminated by a
     * white light source.
     * @return the sample provider
     */
    public SensorMode getRGBMode()
    {
      return getMode(4);
    }

    /**
     * get a sample provider the returns the light value when illuminated without a
     * light source.
     * @return the sample provider
     */
    public SensorMode getAmbientMode()
    {
      return getMode(5);
    }
    
    protected void readRaw()
    {
        port.getFloats(ADRaw, 0, ADRaw.length-1);
    }

    protected void readFull()
    {
        port.getFloats(ADRaw, 0, ADRaw.length);
    }

    public void setFloodlight(boolean floodlight)
    {
        setFloodlight(floodlight ? Color.RED : Color.NONE);
    }


    public int getFloodlight()
    {
        switch(currentType)
        {
        case TYPE_COLORFULL:
            return Color.WHITE;
        case TYPE_COLORRED:
            return Color.RED;
        case TYPE_COLORGREEN:
            return Color.GREEN;
        case TYPE_COLORBLUE:
            return Color.BLUE;
        case TYPE_COLORNONE:
            return Color.NONE;
        default:
            throw new IllegalStateException("Unknown color type" + currentType);
        }
    }

    public boolean isFloodlightOn()
    {
        return (getFloodlight() != Color.NONE);
    }

    public boolean setFloodlight(int color)
    {
        switch (color)
        {
            case Color.RED:
                switchType(NXTColorSensor.TYPE_COLORRED, SWITCH_DELAY);
                break;
            case Color.BLUE:
                switchType(NXTColorSensor.TYPE_COLORBLUE, SWITCH_DELAY);
                break;
            case Color.GREEN:
                switchType(NXTColorSensor.TYPE_COLORGREEN, SWITCH_DELAY);
                break;
            case Color.NONE:
                switchType(NXTColorSensor.TYPE_COLORNONE, SWITCH_DELAY);
                break;
            case Color.WHITE:
                switchType(NXTColorSensor.TYPE_COLORFULL, SWITCH_DELAY);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Read the current color and return an enumeration constant. This is usually only accurate at a distance
     * of about 1 cm.
     * @return The color id under the sensor.
     */
    public int getColorID()
    {
        switchType(TYPE_COLORFULL, SWITCH_DELAY);
        readFull();
        return colorMap[(int)ADRaw[BLANK_INDEX+1]];
    }

    private class ColorIDMode implements SensorMode{
      @Override
      public int sampleSize()
      {
          return 1;
      }
  
      @Override
      public void fetchSample(float[] sample, int offset)
      {
          sample[offset] = (float) getColorID();
      }
  
  
      @Override
      public String getName()
      {
          return "ColorID";
      }
    }
}
