package lejos.hardware.device;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;
import lejos.hardware.sensor.EV3SensorConstants;
import lejos.utility.Delay;

/**
 * This class provides low level access to a UART device.
 * @author andy
 *
 */
public class UART implements Closeable
{
    /**
     * internal class that provides an InputStream interface to
     * a UART
     * @author andy
     *
     */
    protected class UARTInputStream extends InputStream
    {
        protected byte[] buffer = new byte[1];

        @Override
        public int read() throws IOException
        {
            while(port.rawRead(buffer, 0, 1) == 0)
                Delay.msDelay(1);
            return buffer[0];
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
            int total = len;
            if (len == 0) return 0;
            for(;;)
            {
                int ret = port.rawRead(b, off, len);
                len -= ret;
                if (len <= 0)
                    return total;
                off += ret;
                Delay.msDelay(1);
            }
        }
        
    }
    
    /**
     * internal class that provides an OutputStream interface to
     * a UART
     * @author andy
     *
     */
    protected class UARTOutputStream extends OutputStream
    {
        protected byte[] buffer = new byte[1];

        @Override
        public void write(int arg0) throws IOException
        {
            buffer[0] = (byte)arg0;
            while(port.rawWrite(buffer, 0, 1) == 0)
                Delay.msDelay(1);            
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            if (len == 0)
                return;
            for(;;)
            {
                int written = port.rawWrite(b, off, len);
                len -= written;
                if (len <= 0)
                    return;
                off += written;
                Delay.msDelay(1);
            }
        }        
    }

    protected UARTPort port;
    protected InputStream inputStream;
    protected OutputStream outputStream;

    /**
     * Create a UART device attached to the specified port.
     * @param port
     */
    public UART(Port port)
    {
        this.port = port.open(UARTPort.class);
        this.port.setMode(UARTPort.UART_RAW_MODE);        
    }

    @Override
    public void close()
    {
        port.close();
    }

    /**
     * Set the bit rate to be used by the UART
     * @param rate
     */
    public void setBitRate(int rate)
    {
        port.setBitRate(rate);
    }

    /**
     * Read bytes from the device. The read is non blocking and will return
     * a count of 0 if no bytes are available to read.
     * @param buffer buffer to read the bytes into
     * @param offset first position that bytes will be read into
     * @param len maximum number of bytes to read
     * @return actual number of bytes read
     */
    public int read(byte []buffer, int offset, int len)
    {
        return port.rawRead(buffer, offset, len);
    }

    /**
     * Write bytes to the device. The write is non blocking and will return 0 if it
     * is not possible to write to the device.
     * @param buffer buffer to write from
     * @param offset offset of first byte to be written
     * @param len number of bytes to try and write
     * @return number of bytes actually written
     */
    public int write(byte []buffer, int offset, int len)
    {
        return port.rawWrite(buffer, offset, len);
    }

    /**
     * Return the InputStream associated with this device.
     * @return the InputStream that can be used to read from the UART
     */
    public synchronized InputStream getInputStream()
    {
        if (inputStream == null)
            inputStream = new UARTInputStream();
        return inputStream;
    }

    /**
     * Return the OutputStream associated with this device.
     * @return the OutputStream that can be used to write to the UART
     */
    public synchronized OutputStream getOutputStream()
    {
        if (outputStream == null)
            outputStream = new UARTOutputStream();
        return outputStream;
        
    }
}
