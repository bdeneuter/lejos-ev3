package lejos.internal.ev3;

import lejos.hardware.port.BasicSensorPort;
import lejos.hardware.port.IOPort;
import lejos.hardware.sensor.EV3SensorConstants;

/**
 * This class provides the base operations for local EV3 sensor ports.
 * @author andy
 *
 */
public abstract class EV3IOPort implements IOPort, BasicSensorPort, EV3SensorConstants
{
    protected int port = -1;
    protected int typ = -1;
    protected EV3Port ref;
    protected int currentMode = 0;
    protected static EV3IOPort [][] openPorts = new EV3IOPort[EV3Port.MOTOR_PORT+1][PORTS];
   

    /** {@inheritDoc}
     */    
    @Override
    public String getName()
    {
        return ref.getName();
    }
    
    /** {@inheritDoc}
     */    
    @Override
    public int getMode()
    {
        return currentMode;
    }

    /** {@inheritDoc}
     */    
    @Override
    public int getType()
    {
        return 0;
    }

    /** {@inheritDoc}
     */    
    @Override
    public boolean setMode(int mode)
    {
        currentMode = mode;
        return true;
    }

    /** {@inheritDoc}
     */    
    @Override
    public boolean setType(int type)
    {
        throw new UnsupportedOperationException("This operation is not supported by this sensor");

    }

    /** {@inheritDoc}
     */    
    @Override
    public boolean setTypeAndMode(int type, int mode)
    {
        setType(type);
        setMode(mode);
        return true;
    }

    /**
     * Open the sensor port. Ensure that the port is only opened once.
     * @param typ The type of port motor/sensor
     * @param port the port number
     * @param ref the Port ref for this port
     * @return
     */
    public boolean open(int typ, int port, EV3Port ref)
    {
        synchronized (openPorts)
        {
            if (openPorts[typ][port] == null)
            {
                // Set into connected state and disable auto detection
                this.port = port;
                this.typ = typ;
                if (!setPinMode(CMD_CONNECTED))
                {
                    this.port = -1;
                    return false;
                }
                openPorts[typ][port] = this;
                this.ref = ref;
                if (typ == EV3Port.SENSOR_PORT)
                {
                    // set sane pin states, automatic detection may have changed them. 
                    setPinMode(CMD_FLOAT);
                }
                return true;
            }
            return false;
        }
    }
   
    /** {@inheritDoc}
     */    
    @Override
    public void close()
    {
        if (port == -1)
            return;
        synchronized (openPorts)
        {
            setPinMode(CMD_DISCONNECTED);
            openPorts[typ][port] = null;
            port = -1;
        }
    }
    
   /**
     * Set the port pins up ready for use.
     * @param mode The EV3 pin mode
     */
    public boolean setPinMode(int mode)
    {
        //System.out.println("Set Pin mode port " + port + " value " + mode);
        return EV3ConfigurationPort.setPortMode(typ, port, mode);
    }

    /**
     * Close all open ports
     */
    public static void closeAll()
    {
        for(int typ = 0; typ < openPorts.length; typ++)
            for(int prt = 0; prt < openPorts[typ].length; prt++)
                if (openPorts[typ][prt] != null)
                    openPorts[typ][prt].close();
    }

}
