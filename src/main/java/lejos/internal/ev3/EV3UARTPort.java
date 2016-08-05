package lejos.internal.ev3;

import java.io.IOError;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lejos.hardware.DeviceException;
import lejos.hardware.port.UARTPort;
import lejos.internal.io.NativeDevice;
import lejos.utility.Delay;

/**
 * Provide access to EV3 sensor ports operating in UART mode.<p>
 * A mechanism is provided to allow access to the RAW UART device
 * allowing simple read/write operations. This RAW mode disables 
 * the normal Lego protocol used to communicate with Lego UART
 * sensors<p>
 * NOTE: This code is not pretty! The interface uses a number of structures mapped
 * into memory from the device. I am not aware of any clean way to implement this
 * interface in Java. So for now multiple pointers to bytes/ints array etc. are used this
 * means that the actual offsets of the start of the C arrays needs to be obtained
 * and these (along with various sizes) are currently hard-coded as "OFF" values below.
 * I'm sure there must be a better way! Also note that there seem to be a large
 * number of potential race conditions in the device initialisation stage hence the
 * various loops needed to retry operations.
 * @author andy
 *
 */
public class EV3UARTPort extends EV3IOPort implements UARTPort
{
    protected static NativeDevice uart;
    protected static Pointer pDev;
    protected static ByteBuffer devStatus;
    protected static ByteBuffer raw;
    protected static ByteBuffer actual;
    protected static final int DEV_SIZE = 42744;
    protected static final int DEV_STATUS_OFF = 42608;
    protected static final int DEV_RAW_OFF = 4192;
    protected static final int DEV_RAW_SIZE1 = 9600;
    protected static final int DEV_RAW_SIZE2 = 32;
    protected static final int DEV_ACTUAL_OFF = 42592;
    
    protected static final int UART_CONNECT = 0xc0037507;
    protected static final int UART_DISCONNECT = 0xc0037508;
    protected static final int UART_SETMODE = 0xc0037509;
    protected static final int UART_SET_CONN = 0xc00c7500;
    protected static final int UART_READ_MODE_INFO = 0xc03c7501;
    protected static final int UART_NACK_MODE_INFO = 0xc03c7502;
    protected static final int UART_CLEAR_CHANGED = 0xc03c7503;
    protected static final int UART_SET_CONFIG = 0xc0087504;
    protected static final int UART_RAW_READ = 0xc0047505;
    protected static final int UART_RAW_WRITE = 0xc0047506;
            
    
    protected static final byte UART_PORT_CHANGED = 1;
    protected static final byte UART_DATA_READY = 8;
    protected static final byte UART_PORT_ERROR = (byte)0x80;
    
    protected static final int TIMEOUT_DELTA = 1;
    protected static final int TIMEOUT = 4000;
    protected static final int INIT_DELAY = 5;
    protected static final int INIT_RETRY = 100;
    protected static final int OPEN_RETRY = 5;
    
    protected static final int RAW_BUFFER_SIZE = 255;
    
    static {
        initDeviceIO();
    }
    
    /**
     * The following class maps directly to a C structure containing device information.
     * @author andy
     *
     */
    public static class TYPES extends Structure
    {
        public byte Name[] = new byte[12];
        public byte Type;
        public byte Connection;
        public byte Mode;
        public byte DataSets;
        public byte Format;
        public byte Figures;
        public byte Decimals;
        public byte Views;
        public float RawMin;
        public float RawMax;
        public float PctMin;
        public float PctMax;
        public float SiMin;
        public float SiMax;
        public short InvalidTime;
        public short IdValue;
        public byte  Pins;
        public byte[] Symbol = new byte[5];
        public short Align;
        @Override
        protected List getFieldOrder()
        {
            // TODO Auto-generated method stub
            return Arrays.asList(new String[] {"Name",
            "Type",
            "Connection",
            "Mode",
            "DataSets",
            "Format",
            "Figures",
            "Decimals",
            "Views",
            "RawMin",
            "RawMax",
            "PctMin",
            "PctMax",
            "SiMin",
            "SiMax",
            "InvalidTime",
            "IdValue",
            "Pins",
            "Symbol",
            "Align"});
        }

        /*
        public TYPES()
        {
            this.setAlignType(Structure.ALIGN_DEFAULT);
        }*/
    }
    
    public static class UARTCTL extends Structure
    {
        public TYPES TypeData;
        public byte Port;
        public byte Mode;
        
        public UARTCTL()
        {
            //this.setAlignType(Structure.ALIGN_DEFAULT);
            //System.out.println("size is " + size());
        }

        @Override
        protected List getFieldOrder()
        {
            // TODO Auto-generated method stub
            return Arrays.asList(new String[] {"TypeData",
            "Port",
            "Mode"});
        }

    }
    
    public static class UARTCONFIG extends Structure
    {
        public int Port;
        public int BitRate;
        @Override
        protected List getFieldOrder()
        {
            // TODO Auto-generated method stub
            return Arrays.asList(new String[] {
            "Port",
            "BitRate"});
        }
    }
    
    protected TYPES[] modeInfo = new TYPES[UART_MAX_MODES];
    protected int modeCnt = 0;
    protected byte[] rawInput;
    protected byte[] rawOutput;
    protected byte[] cmd = new byte[3];

    /**
     * return the current status of the port
     * @return status
     */
    protected byte getStatus()
    {
        synchronized(devStatus)
        {
            return devStatus.get(port);
        }
    }
    
    protected void setStatus(int newStatus)
    {
        devStatus.put(port, (byte)newStatus);
    }

    /**
     * Wait for the port status to become non zero, or for the operation to timeout
     * @param timeout timeout period in ms
     * @return port status or 0 if the operation timed out
     */
    protected byte waitNonZeroStatus(int timeout)
    {
        int cnt = timeout/TIMEOUT_DELTA;
        byte status = getStatus();
        while (cnt-- > 0)
        {
            if (status != 0)
                return status;
            Delay.msDelay(TIMEOUT_DELTA);
            status = getStatus();
        }
        //System.out.println("NZS Timeout");
        return status;       
    }

    /**
     * Wait for the port status to become zero
     * @param timeout timeout period in ms
     * @return zero if successful or the current status if timed out
     */
    protected byte waitZeroStatus(int timeout)
    {
        int cnt = timeout/TIMEOUT_DELTA;
        byte status = getStatus();
        while (cnt-- > 0)
        {
            if (status == 0)
                return status;
            Delay.msDelay(TIMEOUT_DELTA);
            status = getStatus();
        }
        //System.out.println("ZS Timeout");
        return status;       
    }

    /**
     * Connect to the device
     */
    protected void connect()
    {
        cmd[0] = (byte)port;
        uart.ioctl(UART_CONNECT, cmd);        
    }

    /**
     * Disconnect to the device
     */
    protected void disconnect()
    {
        cmd[0] = (byte)port;
        uart.ioctl(UART_DISCONNECT, cmd);        
    }

    /**
     * reset the port and device
     */
    protected void reset()
    {
        // Force the device to reset
        disconnect();
        connect();
    }

    /**
     * Set the current operating mode
     * @param mode
     */
    protected void setOperatingMode(int mode)
    {
        cmd[0] = (byte)port;
        cmd[2] = (byte)mode;
        uart.ioctl(UART_SETMODE, cmd);
    }

    /**
     * Read the mode information for the specified operating mode.
     * @param mode mode number to read
     * @param uc control structure to read the data into
     * @return
     */
    protected boolean getModeInfo(int mode, UARTCTL uc)
    {
        uc.Port = (byte)port;
        uc.Mode = (byte)mode;
        uc.write();
        //System.out.println("size is " + uc.size() + " TYPES " + uc.TypeData.size() + " ptr " + uc.getPointer().SIZE);
        uart.ioctl(UART_READ_MODE_INFO, uc.getPointer());
        uc.read();
        //System.out.println("name[0]" + uc.TypeData.Name[0]);
        return uc.TypeData.Name[0] != 0;
    }
    
    /**
     * Clear the flag that indicates the mode info has been cached. This
     * allows us to read the same infomration again later without having to
     * reset the device.
     * @param mode mode number to read
     * @param uc control structure to read the data into
     * @return
     */
    protected void clearModeCache(int mode, UARTCTL uc)
    {
        uc.Port = (byte)port;
        uc.Mode = (byte)mode;
        uc.write();
        //System.out.println("size is " + uc.size() + " TYPES " + uc.TypeData.size() + " ptr " + uc.getPointer().SIZE);
        uart.ioctl(UART_NACK_MODE_INFO, uc.getPointer());
    }

    /**
     * Clear the port changed flag for the current port.
     */
    protected void clearPortChanged(UARTCTL uc)
    {
        //System.out.printf("Clear changed\n");
        uc.Port = (byte)port;
        uc.write();
        uart.ioctl(UART_CLEAR_CHANGED, uc.getPointer());
        devStatus.put(port, (byte)(devStatus.get(port) & ~UART_PORT_CHANGED));        
    }

    /**
     * Read the mode information from the port. return true 
     * @return
     */
    protected boolean readModeInfo()
    {
        //long base = System.currentTimeMillis();
        modeCnt = 0;
        for(int i = 0; i < UART_MAX_MODES; i++)
        {
            UARTCTL uc = new UARTCTL();
            if (getModeInfo(i, uc))
            {
                clearModeCache(i, uc);
                modeInfo[i] = uc.TypeData;
                modeCnt++;
            }
            else
                modeInfo[i] = null;
        }
        //System.out.println("Got " + modeCnt + " entries time " + (System.currentTimeMillis() - base));
        return modeCnt > 0;

    }
    /**
     * Attempt to initialise the sensor ready for use.
     * @param mode initial operating mode
     * @return -1 no uart, 0 failed to initialise, 1 sensor initialised
     */
    protected int initSensor(int mode)
    {
        byte status;
        int retryCnt = 0;
        //System.out.println("Initial status is " + getStatus() + " type is " + ldm.getPortType(port));
        //long base = System.currentTimeMillis();
        UARTCTL uc = new UARTCTL();
        // now try and configure as a UART
        setOperatingMode(mode);
        status = waitNonZeroStatus(TIMEOUT);
        //System.out.println("Time is " + (System.currentTimeMillis() - base));
        if ((status & UART_PORT_ERROR) != 0) return -1;
        while((status & UART_PORT_CHANGED) != 0 && retryCnt++ < INIT_RETRY)
        {
            // something change wait for it to become ready
            clearPortChanged(uc);
            Delay.msDelay(INIT_DELAY);
            status = waitNonZeroStatus(TIMEOUT);
            //System.out.println("Time2 is " + (System.currentTimeMillis() - base));
            if ((status & UART_DATA_READY) != 0 && (status & UART_PORT_CHANGED) == 0) 
            {
                // device ready make sure it is now in the correct mode
                setOperatingMode(mode);
                status = waitNonZeroStatus(TIMEOUT);
                //System.out.println("Time3 is " + (System.currentTimeMillis() - base));
            }
        }
        //System.out.println("Init complete retry " + retryCnt + " time " + (System.currentTimeMillis() - base));
        if ((status & UART_DATA_READY) != 0 && (status & UART_PORT_CHANGED) == 0)
            return 1;
        else
            return 0;
    }

    /** {@inheritDoc}
     */    
    @Override
    public boolean initialiseSensor(int mode)
    {
        connect();
        for(int i = 0; i < OPEN_RETRY; i++)
        {
            // initialise the sensor, if we have no mode data
            // then read it, otherwise use what we have
            int res = initSensor(mode);
            if (res < 0) break;
            if (res > 0 && (modeCnt > 0 || readModeInfo()))
            {
                //System.out.println("reset cnt " + i);
                return super.setMode(mode);
            }
            resetSensor();
        }
        disconnect();
        return false;
    }
    
    /** {@inheritDoc}
     */    
    @Override
    public void resetSensor()
    {
        reset();
        waitZeroStatus(TIMEOUT);
    }


    
    /** {@inheritDoc}
     */    
    @Override
    public boolean open(int typ, int port, EV3Port ref)
    {
        if (!super.open(typ, port, ref))
            return false;
        // clear mode data cache
        modeCnt = 0;
        return true;
    }

    /** {@inheritDoc}
     */    
    @Override
    public void close()
    {
        disconnect();
        super.close();
    }
    
    /**
     * Set the current operating mode
     * @param mode new mode to set
     * @return true if the mode is set, false if the operation failed
     */
    public boolean setMode(int mode)
    {
        //System.out.println("Set mode " + mode);
        // are we initialised ?
        if (modeCnt <= 0)
            return initialiseSensor(mode);
        if (modeInfo[mode] == null)
            return false;
        //long s = System.currentTimeMillis();
        //System.out.println("Mode is " + getModeName(mode));
        setOperatingMode(mode);
        //System.out.println("status is " + getStatus());
        int status = waitNonZeroStatus(TIMEOUT);
        boolean ret;
        if ((status & UART_DATA_READY) != 0 && (status & UART_PORT_CHANGED) == 0)
            ret = super.setMode(mode);
        else
        {
            // Sensor may have reset try and initialise it in the new mode.
            ret =  initialiseSensor(mode);
            //System.out.println("reset");
        }
        if (ret)
        {
            //ret = waitDataUpdate(TIMEOUT);
            //System.out.println("time " + (System.currentTimeMillis() - s));
        }
        return ret;
    }

    /**
     * The RAW data is held in a circular buffer with 32 bytes of data per entry
     * and 300 entries per port. This method calculates the byte offset of the
     * latest data value read into the buffer.
     * @return offset of the current data
     */
    private int calcRawOffset()
    {
        synchronized (actual)
        {
            int ret = port*DEV_RAW_SIZE1 + actual.getShort(port*2)*DEV_RAW_SIZE2;
            return ret;
        }
    }
    

    /**
     * Wait for a new data point to be added to the data set. Return true if
     * new data is available, false if not
     * @param timeout
     * @return true if updated
     */
    protected boolean waitDataUpdate(int timeout)
    {
        int cnt = timeout/TIMEOUT_DELTA;
        int offset = calcRawOffset();
        //System.out.println("offset1 " + actual.getShort(port*2));
        while (cnt-- > 0)
        {
            if (calcRawOffset() != offset)
            {
                //System.out.println("offset " + actual.getShort(port*2));
                return true;
            }
            Delay.msDelay(TIMEOUT_DELTA);
        }
        return false;       
    }

    /**
     * Check the sensor status, and if possible recover any from any error.
     * If everything fails throw an exception
     */
    protected void checkSensor()
    {
        if ((getStatus() & UART_PORT_CHANGED) != 0)
        {
            //System.out.println("port " + port + " Changed ");
            // try and reinitialze it
            if (!initialiseSensor(getMode()))
                throw new DeviceException("Sensor changed unable to reset");
                
        }
        
    }
    /**
     * read a single byte from the device
     * @return the byte value
     */
    public byte getByte()
    {
        checkSensor();
        return raw.get(calcRawOffset());
    }

    /**
     * read a number of bytes from the device
     * @param vals byte array to accept the data
     * @param offset offset at which to store the data
     * @param len number of bytes to read
     */
    public void getBytes(byte [] vals, int offset, int len)
    {
        checkSensor();
        int loc = calcRawOffset();
        for(int i = 0; i < len; i++)
            vals[i+offset] = raw.get(loc + i);
    }

    /**
     * read a single short from the device.
     * @return the short value
     */
    public int getShort()
    {
        checkSensor();
        return raw.getShort(calcRawOffset());
    }
    
    /**
     * read a number of shorts from the device
     * @param vals short array to accept the data
     * @param offset offset at which to store the data
     * @param len number of shorts to read
     */
    public void getShorts(short [] vals, int offset, int len)
    {
        checkSensor();
        int loc = calcRawOffset();
        for(int i = 0; i < len; i++)
            vals[i+offset] = raw.getShort(loc + i*2);
    }

    /**
     * Get the string name of the specified mode.<p><p>
     * TODO: Make other mode data available.
     * @param mode mode to lookup
     * @return String version of the mode name
     */
    public String getModeName(int mode)
    {
        if (modeInfo[mode] != null)
        {
            byte[] name = modeInfo[mode].Name;
            // find the length of the possibly null terminated ascii string
            int len = name.length;
            for(int i = 0; i < name.length; i++)
                if (name[i] == 0)
                    len = i;
            return new String(name, 0, len, Charset.forName("US-ASCII")).trim();
        }
        else 
            return "Unknown";
    }


    /**
     * Write bytes to the sensor
     * @param buffer bytes to be written
     * @param offset offset to the start of the write
     * @param len length of the write
     * @return number of bytes written
     */
    public int write(byte[] buffer, int offset, int len) {
        byte[] command = new byte[len + 1];
        command[0] = (byte) port;
        System.arraycopy(buffer, offset, command, 1, len);
        int ret = uart.write(command, command.length);
        if (ret > 0) ret--;
        return ret;
    }
    
    /**
     * Return the current sensor reading to a string. 
     */
    public String toString()
    {
        float divTable[] = {1f, 10f, 100f, 1000f, 10000f, 100000f};
        TYPES info = modeInfo[currentMode];
        float val;
        switch(info.Format)
        {
        case 0:
            if (info.RawMin >= 0)
                val = getByte() & 0xff;
            else
                val = getByte();
            break;
        case 1:
            if (info.RawMin >= 0)
                val = getShort() & 0xffff;
            else
                val = getShort();
            break;
        // TODO: Sort out other formats
        default:
            val = 0.0f;
        }
        val = val/divTable[info.Decimals];
        String format = "%" + info.Figures + "." + info.Decimals + "f" + new String(info.Symbol);
        return String.format(format, val);        
    }

    /**
     * Set the bit rate of the port when operating in RAW mode.
     * @param bitRate The new bit rate
     */
    public void setBitRate(int bitRate)
    {
        UARTCONFIG uc = new UARTCONFIG();
        uc.Port = port;
        uc.BitRate = bitRate;
        uc.write();
        uart.ioctl(UART_SET_CONFIG, uc.getPointer());        
    }

    /**
     * Read bytes from the uart port. If no bytes are available return 0.<p>
     * Note: The port must have been set into RAW mode to use this method.
     * @param buffer The buffer to store the read bytes
     * @param offset The offset at which to start storing the bytes
     * @param len The maximum number of bytes to read
     * @return The actual number of bytes read
     */
    public int rawRead(byte[] buffer, int offset, int len)
    {
        if (rawInput == null)
            rawInput = new byte[RAW_BUFFER_SIZE+2];
        rawInput[0] = (byte)port;
        if (len > RAW_BUFFER_SIZE)
            len = RAW_BUFFER_SIZE;
        rawInput[1] = (byte)len;
        len = uart.ioctl(UART_RAW_READ, rawInput);
        System.arraycopy(rawInput, 2, buffer, offset, len);
        return len;
    }

    /**
     * Attempt to write a series of bytes to the uart port. This call
     * is non-blocking if there is no space in the write buffer a count
     * of 0 is returned.<p>
     * Note: The port must have been set into RAW mode before attempting
     * to use the method.
     * @param buffer The buffer containing the bytes to write
     * @param offset The offset of the first byte
     * @param len The number of bytes to attempt to write
     * @return The actual number of bytes written
     */
    public int rawWrite(byte[] buffer, int offset, int len)
    {
        if (rawInput == null)
            rawOutput = new byte[RAW_BUFFER_SIZE+2];
        rawOutput[0] = (byte)port;
        if (len > RAW_BUFFER_SIZE)
            len = RAW_BUFFER_SIZE;
        rawOutput[1] = (byte)len;
        System.arraycopy(buffer, offset, rawOutput, 2, len);
        len = uart.ioctl(UART_RAW_WRITE, rawOutput);
        return len;
    }
    


    private static void initDeviceIO()
    {
        try {
            uart = new NativeDevice("/dev/lms_uart");
            pDev = uart.mmap(DEV_SIZE);
        }
        catch (IOError e)
        {
            throw new UnsupportedOperationException("Unable to access EV3 hardware. Is this an EV3?", e);
        }
        devStatus = pDev.getByteBuffer(DEV_STATUS_OFF, PORTS);
        actual = pDev.getByteBuffer(DEV_ACTUAL_OFF, PORTS*2);
        raw = pDev.getByteBuffer(DEV_RAW_OFF, PORTS*DEV_RAW_SIZE1);
    }
}
