package lejos.hardware.device;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Device;
import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.ConfigurationPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;
import lejos.hardware.sensor.EV3SensorConstants;
import lejos.hardware.sensor.I2CSensor;
import lejos.utility.Delay;

public class DeviceIdentifier extends Device implements EV3SensorConstants
{
    protected final static int ANALOG_ID_VAR = 50;
    protected final static long VALID_TIME = 2000;
    protected final static long MIN_TIME = 100;
    protected long openTime;
    protected Port port;
    protected ConfigurationPort configPort;
    protected static List<AbstractMap.SimpleEntry<Integer,String>> EV3AnalogMap = new ArrayList<AbstractMap.SimpleEntry<Integer,String>>();
    static 
    {
        EV3AnalogMap.add(new AbstractMap.SimpleEntry<Integer,String>(417, "EV3_TOUCH"));
    }

    
    /**
     * Create an instance of the device identifier for a particular port
     * @param port The port to operate with
     */
    public DeviceIdentifier(Port port)
    {
        this.port = port;
        openConfigPort();
    }
    
    public void close()
    {
        if (configPort != null)
            configPort.close();
        super.close();            
    }

    protected void openConfigPort()
    {
        configPort = port.open(ConfigurationPort.class);
        openTime = System.currentTimeMillis();        
    }
    
    /**
     * Wait until the identification data for this port is valid
     */
    protected void waitValid()
    {
        if (configPort == null)
            openConfigPort();
        long minDelay = (openTime + MIN_TIME) - System.currentTimeMillis();
        if (minDelay > 0)
            Delay.msDelay(minDelay);
        // allow time for detection to work
        while (System.currentTimeMillis() < openTime + VALID_TIME)
        {
            if (configPort.getPortType() != CONN_NONE) 
            {
                //System.out.println("detected after " + i);
                break;
            }
            Delay.msDelay(1);
        }
        
    }

    /**
     * Get the type classification for the port. If a sensor is attached to the port
     * this will identify the connection type (CONN_NONE, CONN_INPUT_UART etc.). See 
     * the class EV3SensorConstants for the actual values
     * @return The type of the port
     * 
     */
    public int getPortType()
    {
        waitValid();
        return configPort.getPortType();
    }

    /**
     * This method returns information on the sensor/motor that is attached to the
     * specified port. Note that only very basic sensor identification information
     * may be available for some sensor types. It may be necessary to actually open the
     * sensor to allow it to be identified in further detail.
     * @return the sensor type
     */
    public int getDeviceType()
    {
        waitValid();
        return configPort.getDeviceType();
    }
    
    /**
     * Returns the signature for a dumb NXT sensor
     * @return string identifying the device
     */
    protected String getNXTDumbSignature()
    {
        switch(getDeviceType())
        {
        case TYPE_NXT_TOUCH:
            return "NXT_TOUCH";
        case TYPE_NXT_LIGHT:
            return "NXT_LIGHT";
        case TYPE_NXT_SOUND:
            return "NXT_SOUND";
        default:
            return "UNKNOWN";
        }
    }

    /**
     * Returns the signature for a i2c sensor
     * @return string identifying the device
     */
    protected String getI2CSignature(boolean full)
    {
        configPort.close();
        configPort = null;
        String product = "";
        String vendor = "";
        String version = "";
        String address = "";
        I2CSensor i2c = null;
        try {
            // we need to try and read the device identification strings
            i2c = new I2CSensor(port);
            // search for the device on available addresses
            for(int i = 2; i < 255; i+= 2)
            {
                i2c.setAddress(i);
                product = i2c.getProductID();
                if (product.length() != 0)
                {
                    if (i > 2)
                        address = String.valueOf(i);
                    break;
                }
            }
            vendor = i2c.getVendorID();
            version = i2c.getVersion();
        }
        catch (Exception e)
        {
            // ignore any exceptions during detection.
        }
        finally
        {
            if (i2c != null)
                i2c.close();
            openConfigPort();
        }
        if (product.length() == 0)
            product = "unknown";
        if (vendor.length() == 0)
            vendor = "unknown";
        if (version.length() == 0)
            version = "unknown";
        if (address.length() != 0)
            address = "//" + address + "/";
        String ret = address + vendor + "/" + product;
        if (full) 
            ret += "/" + version;
        return ret;
    }
    
    /**
     * Returns the signature for a dumb EV3 sensor
     * @return string identifying the device
     */
    protected String getEV3DumbSignature()
    {
        // need to look at analog value on pin 1 to identify
        configPort.close();
        configPort = null;
        String product = "";
        AnalogPort ap = null;
        try {
            ap = port.open(AnalogPort.class);
            int p1mV = (int)(ap.getPin1()*1000);
            System.out.println("Pin 1 voltage is " + p1mV);
            // search for a matching sensor
            for(AbstractMap.SimpleEntry<Integer,String> sensor : EV3AnalogMap)
            {
                int key = sensor.getKey();
                if ((key - ANALOG_ID_VAR) < p1mV && (key + ANALOG_ID_VAR ) > p1mV)
                {
                    product = sensor.getValue();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // ignore any exceptions during detection.
        }
        finally
        {
            if (ap != null)
                ap.close();
            openConfigPort();
        }
        if (product.length() == 0)
            product = "unknown"; 
        return product;
    }
    
    /**
     * Returns the signature for a UART sensor
     * @return string identifying the device
     */
    protected String getUARTSignature()
    {
        configPort.close();
        configPort = null;
        String product = "";
        UARTPort uart = null;
        try {
            // we need to try and read the device identification strings
            uart = port.open(UARTPort.class);
            uart.setMode(0);
            product = uart.getModeName(0);
        }
        catch (Exception e)
        {
            // ignore any exceptions during detection.
        }
        finally
        {
            if (uart != null)
                uart.close();
            openConfigPort();
        }
        if (product.length() == 0)
            product = "unknown";
        return product;        
    }
    
    /**
     * Returns the signature for a motor/output device
     * @return string identifying the device
     */
    protected String getMotorSignature()
    {
        switch(getDeviceType())
        {
        case TYPE_TACHO:
            return "TACHO";
        case TYPE_MINITACHO:
            return "MINITACHO";
        case TYPE_NEWTACHO:
            return "NEWTACHO";
        default:
            return "UNKNOWN";
        }
    }
    
    
    /**
     * return the signature of the attached device. This signature can be used to identify
     * the actual device. Note that identification may require that the device is opened.
     * @param full true to return all available information, false for basic information
     * @return a string signature
     */
    public String getDeviceSignature(boolean full)
    {
        int portType = getPortType();
        switch(portType)
        {
        case CONN_NONE:
            return "NONE:NONE";
        case CONN_ERROR:
        case CONN_UNKNOWN:
            return "UNKNOWN:UNKNOWN";
        case CONN_NXT_COLOR:
            return "NXT_COLOR:NXT_COLOR";
        case CONN_NXT_DUMB:
            return "NXT_ANALOG:" + getNXTDumbSignature();
        case CONN_NXT_IIC:
            return "IIC:" + getI2CSignature(full);
        case CONN_INPUT_DUMB:
            return "EV3_ANALOG:" + getEV3DumbSignature();
        case CONN_INPUT_UART:
            return "UART:" + getUARTSignature();
        case CONN_OUTPUT_DUMB:
            // TODO: Does anyone have anything that is recognised as this?
            return "OUTPUT_DUMB:UNKNOWN";
        case CONN_OUTPUT_INTELLIGENT:
            // TODO: Same for this type
            return "OUTPUT_INTELLIGENT:UNKNOWN";
        case CONN_OUTPUT_TACHO:
            return "OUTPUT_TACHO:" + getMotorSignature();
        default:
            return "UNKNOWN:UNKNOWN";
        }
    }
    
}