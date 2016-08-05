package lejos.internal.ev3;

import java.io.IOError;

import lejos.hardware.port.ConfigurationPort;
import lejos.hardware.sensor.EV3SensorConstants;
import lejos.internal.io.NativeDevice;
import lejos.utility.Delay;

public class EV3ConfigurationPort extends EV3IOPort  implements ConfigurationPort
{
    protected static NativeDevice dev;
    
    static {
        initDeviceIO();
    }

    /** {@inheritDoc}
     * Note that it can take up to two seconds for the identification data to be available
     * after the port has been opened.
     */    
    @Override
    public boolean open(int typ, int port, EV3Port ref)
    {
        if (!super.open(typ, port, ref))
            return false;
        // enable automatic detection on this port
        setPinMode(CMD_AUTOMATIC);
        return true;
    }


    /**
     * Get the type classification for the port. If a sensor is attached to the port
     * this will identify the connection type (UART/IIC/Dumb/Output etc.)
     * @return The type of the port. 
     */
    public int getPortType()
    {
        if (typ == EV3Port.MOTOR_PORT)
            return EV3AnalogPort.getMotorPortType(port);
        else
            return EV3AnalogPort.getPortType(port);
    }

    /**
     * This function returns information on the sensor/motor that is attached to the
     * specified port. Note that only very basic sensor identification information
     * may be available for some sensor types. It may be necessary to actually open the
     * sensor to allow it to be identified in further detail.
     * @return the sensor type
     */
    public int getDeviceType()
    {
        if (typ == EV3Port.MOTOR_PORT)
            return EV3AnalogPort.getMotorType(port);
        else
            return EV3AnalogPort.getAnalogSensorType(port);
    }


    /**
     * Set the basic operating mode of the various sensor pins to allow correct
     * operation of the attached sensor.
     * @param port port to set
     * @param mode the pin mode to use.
     */
    public static boolean setPortMode(int typ, int port, int mode)
    {
        //System.out.println("Set port mode " + port + " mode " + mode);
        byte [] cmd = new byte[2];
        cmd[0] = (byte)(typ == EV3Port.MOTOR_PORT ? port + PORTS : port);
        cmd[1] = (byte)mode;
        return dev.write(cmd, cmd.length) >= 0;
    }
    
    private static void initDeviceIO()
    {
        try {
            dev = new NativeDevice("/dev/lms_dcm");
        } catch(IOError e)
        {
            throw new UnsupportedOperationException("Unable to access EV3 hardware. Is this an EV3?", e);
        }
    }
}
