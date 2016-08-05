package lejos.internal.ev3;

import java.io.IOError;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lejos.hardware.port.I2CException;
import lejos.hardware.port.I2CPort;
import lejos.internal.io.NativeDevice;
import lejos.utility.Delay;

/**
    Provide access to EV3 I2C sensors.<BR>
    NOTE: The EV3 iic kernel module provides the capability to make an i2c sensor 
    have a similar interface to that used for uart based sensors. In particular it
    provides a mechanism to have the kernel poll the sensor. However this mode seems
    to be of limited use because most i2c sensors provide multiple data values etc.
    Because of this we only implement the basic i2c interface.
 */
public class EV3I2CPort extends EV3IOPort implements I2CPort
{
    protected static NativeDevice i2c;
    static {
        initDeviceIO();
    }
    protected static final int IIC_CONNECT = 0xc0036907;
    protected static final int IIC_DISCONNECT = 0xc0036908;
    protected static final int IIC_IO = 0xc0036909;
        
    protected static final int IO_TIMEOUT = 2000;
    
    protected static final byte SPEED_10KHZ = 0;
    protected static final byte SPEED_100KHZ = 1;

   
    /** Maximum read/write request length */
    public static final int MAX_IO = IIC_DATA_LENGTH;
    
    protected byte[] cmd = new byte[IIC_DATA_LENGTH + 5];
    protected byte speed = SPEED_10KHZ;

    protected boolean initSensor()
    {
        cmd[0] = (byte)port;
        i2c.ioctl(IIC_CONNECT, cmd); 
        return true;
    }
    
    /**
     * allow access to the specified port
     * @param p port number to open
     */
    public boolean open(int t, int p, EV3Port r)
    {
        if (!super.open(t, p, r))
            return false;
        // Set pin state to a sane default
        setPinMode(CMD_FLOAT);
        return true;
    }

    /** {@inheritDoc}
     */    
    @Override
    public void close()
    {
        cmd[0] = (byte)port;
        i2c.ioctl(IIC_DISCONNECT, cmd);        
        super.close();
    }
    

    @Override
    public boolean setType(int type)
    {
        //System.out.println("Set type " + type);
        speed = SPEED_10KHZ;
        switch(type)
        {
        case TYPE_HIGHSPEED:
            speed = SPEED_100KHZ;
            // fall through
        case TYPE_LOWSPEED:
            setPinMode(CMD_SET|CMD_PIN5);
            break;
        case TYPE_HIGHSPEED_9V:
            speed = SPEED_100KHZ;
            // fall through
        case TYPE_LOWSPEED_9V:
            setPinMode(CMD_SET|CMD_PIN1|CMD_PIN5);
            break;
        default:
            return false;
        }
        return initSensor();
    }

    
    /**
     * High level i2c interface. Perform a complete i2c transaction and return
     * the results. Writes the specified data to the device and then reads the
     * requested bytes from it.
     * @param deviceAddress The I2C device address.
     * @param writeBuf The buffer containing data to be written to the device.
     * @param writeOffset The offset of the data within the write buffer
     * @param writeLen The number of bytes to write.
     * @param readBuf The buffer to use for the transaction results
     * @param readOffset Location to write the results to
     * @param readLen The length of the read
     */
    public synchronized void i2cTransaction(int deviceAddress, byte[]writeBuf,
            int writeOffset, int writeLen, byte[] readBuf, int readOffset,
            int readLen)
    {
        //long st = System.currentTimeMillis();
        cmd[0] = (byte)port;
        cmd[1] = speed;
        cmd[2] = (byte) readLen;
        cmd[3] = (byte) writeLen;
        cmd[4] = (byte) (deviceAddress >> 1);
        if (writeLen > 0) System.arraycopy(writeBuf, writeOffset, cmd, 5, writeLen);
        i2c.ioctl(IIC_IO, cmd);
        int result = (int) cmd[1];
        if (result == STATUS_FAIL)
            throw new I2CException("I2C I/O error");
        if (result == STATUS_BUSY)
            throw new I2CException("I2C Bus busy");            
        if (result == STATUS_OK)
        {
            //System.out.println("iic time " + (System.currentTimeMillis() - st));
            if (readLen > 0)
                System.arraycopy(cmd, 5, readBuf, readOffset,  readLen);
            return;
        }
        //System.out.println("i2c error res is " + result);
        throw new I2CException("I2C Unexpected error " + result);
    }
    

    private static void initDeviceIO()
    {
        try {
            i2c = new NativeDevice("/dev/lms_iic");
        } catch (IOError e)
        {
            throw new UnsupportedOperationException("Unable to access EV3 hardware. Is this an EV3?", e);
        }
    }
    
}
