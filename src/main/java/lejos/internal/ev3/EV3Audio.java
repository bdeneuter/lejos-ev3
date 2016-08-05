package lejos.internal.ev3;


import java.io.*;

import lejos.hardware.Audio;
import lejos.internal.io.NativeDevice;
import lejos.internal.io.SystemSettings;
import lejos.utility.Delay;

/**
 * EV3 sound routines.
 *
 */
public class EV3Audio implements Audio
{

    private static final int RIFF_HDR_SIZE = 44;
    private static final int RIFF_RIFF_SIG = 0x52494646;
    private static final int RIFF_WAVE_SIG = 0x57415645;
    private static final int RIFF_FMT_SIG = 0x666d7420;
    private static final short RIFF_FMT_PCM = 0x0100;
    private static final short RIFF_FMT_1CHAN = 0x0100;
    private static final short RIFF_FMT_8BITS = 0x0800;
    private static final short RIFF_FMT_16BITS = 0x1000;
    private static final int RIFF_DATA_SIG = 0x64617461;
    
    private static final int PCM_BUFFER_SIZE = 8*1024;
    
    public static final String VOL_SETTING = "lejos.volume";
    
    private static int masterVolume = 0;
    
    private static NativeDevice dev = new NativeDevice("/dev/lms_sound");
    private static final byte OP_BREAK = 0;
    private static final byte OP_TONE = 1;
    private static final byte OP_PLAY = 2;
    private static final byte OP_REPEAT = 3;
    private static final byte OP_SERVICE = 4;
    private static final EV3Audio singleton = new EV3Audio();
    private int PCMSampleSize = 0;
    private byte[] PCMBuffer;
    
    /**
     * Static constructor to force loading of system settings
     */
    static {
        singleton.loadSettings();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {singleton.endPCMPlayback();}
        });
    }

    private EV3Audio()
    {
    }

    public static Audio getAudio()
    {
        return singleton;
    }
    
    private static int C2 = 523;
    
    /**
     * Play a system sound.
     * <TABLE BORDER=1>
     * <TR><TH>aCode</TH><TH>Resulting Sound</TH></TR>
     * <TR><TD>0</TD><TD>short beep</TD></TR>
     * <TR><TD>1</TD><TD>double beep</TD></TR>
     * <TR><TD>2</TD><TD>descending arpeggio</TD></TR>
     * <TR><TD>3</TD><TD>ascending  arpeggio</TD></TR>
     * <TR><TD>4</TD><TD>long, low buzz</TD></TR>
     * </TABLE>
     */
    public void systemSound(int aCode)
    {
        if (aCode == BEEP)
            playTone(600, 200);
        else if (aCode == DOUBLE_BEEP)
        {
            playTone(600, 150);
            Delay.msDelay(50);
            playTone(600, 150);
        }
        else if (aCode == ASCENDING)// C major arpeggio
            for (int i = 4; i < 8; i++)
            {
                playTone(C2 * i / 4, 100);
            }
        else if (aCode == DESCENDING)
            for (int i = 7; i > 3; i--)
            {
                playTone(C2 * i / 4, 100);
            }
        else if (aCode == BUZZ)
        {
            playTone(100, 500);
        }
    }

    
    /**
     * Plays a tone, given its frequency and duration. 
     * @param aFrequency The frequency of the tone in Hertz (Hz).
     * @param aDuration The duration of the tone, in milliseconds.
     * @param aVolume The volume of the playback 100 corresponds to 100%
     */
    static void playFreq(int aFrequency, int aDuration, int aVolume)
    {
        if (aVolume >= 0)
            aVolume = (aVolume*masterVolume)/100;
        else
            aVolume = -aVolume;
        byte[] cmd = new byte[6];
        cmd[0] = OP_TONE;
        cmd[1] = (byte) aVolume;
        cmd[2] = (byte) aFrequency;
        cmd[3] = (byte) (aFrequency >> 8);
        cmd[4] = (byte) aDuration;
        cmd[5] = (byte) (aDuration >> 8);
        dev.write(cmd, cmd.length);        
    }


    /**
     * Plays a tone, given its frequency and duration. 
     * @param aFrequency The frequency of the tone in Hertz (Hz).
     * @param aDuration The duration of the tone, in milliseconds.
     * @param aVolume The volume of the playback 100 corresponds to 100%
     */
    public void playTone(int aFrequency, int aDuration, int aVolume)
    {
        playFreq(aFrequency, aDuration, aVolume);
        Delay.msDelay(aDuration);
    }
    

    public void playTone(int freq, int duration)
    {
        playTone(freq, duration, VOL_MAX);
    }


    
    /**
     * Read an LSB format
     * @param d stream to read from
     * @return the read int
     * @throws java.io.IOException
     */
    private int readLSBInt(DataInputStream d) throws IOException
    {
        int val = d.readByte() & 0xff;
        val |= (d.readByte() & 0xff) << 8;
        val |= (d.readByte() & 0xff) << 16;
        val |= (d.readByte() & 0xff) << 24;
        return val;
    }

    /**
     * Prepare the sound device to play PCM samples. 
     * @param sampleSize number of bits per sample
     * @param sampleRate number of samples per second
     * @param vol playback volume
     */
    public synchronized void startPCMPlayback(int sampleSize, int sampleRate, int vol)
    {
        if (sampleSize != 8 && sampleSize != 16)
            throw new UnsupportedOperationException("Only 8bit and 16bit samples supported size is " + sampleSize);
        if (sampleRate < 8000 || sampleRate > 48000)
            throw new UnsupportedOperationException("Sample rate must be between 8KHz and 48KHz");
        // save sample size for later
        PCMSampleSize = sampleSize;
        PCMBuffer = new byte[PCM_BUFFER_SIZE+1];
        if (vol >= 0)
            vol = (vol*masterVolume)/100;
        else
            vol = -vol;
        // get ready to play, set the volume
        byte [] buf = new byte[6];
        buf[0] = OP_PLAY;
        buf[1] = (byte)vol;
        buf[2] = (byte) sampleRate;
        buf[3] = (byte) (sampleRate >> 8);
        buf[4] = (byte) (sampleRate >> 16);
        buf[5] = (byte) (sampleRate >> 24);
        dev.write(buf, 6);
    }

    /**
     * Cease the playing of PCM samples
     */
    public synchronized void endPCMPlayback()
    {
        // are we playing?
        if (PCMSampleSize == 0)
            return;
        PCMSampleSize = 0;         
        byte [] buf = new byte[6];
        buf[0] = OP_BREAK;
        dev.write(buf, 1);
        PCMBuffer = null;
    }

    /**
     * Helper method write the PCM data to the sound device. It is assumed that the buffer contains
     * extra space for the command byte.
     * @param buffer
     * @param cnt
     */
    private synchronized int writePCMBuffer(byte[] buf, int dataLen)
    {
        // check for playback aborted
        if (PCMSampleSize == 0) return -1;
        int offset = 0;
        while (offset < dataLen)
        {
            buf[offset] = OP_SERVICE;
            int len = dataLen - offset;
            int written = dev.write(buf, offset, len + 1);
            if (written < 0) return -1;
            if (written < (len + 1))
            {
                Delay.msDelay(5);
            }
            offset += written;
        }
        return dataLen;
    }
    
    public void writePCMSamples(byte[] data, int offset, int dataLength)
    {
        if (PCMSampleSize == 0)
            throw new UnsupportedOperationException("Sample size not set did you call startPCMPlayback");
        if (PCMSampleSize == 8)
        {
            // non native sample size need to convert
            while (offset < dataLength)
            {
                int len = dataLength - offset;
                if (len > PCM_BUFFER_SIZE/2)
                   len = PCM_BUFFER_SIZE/2;
                int outOffset = 1;
                for(int i = 0; i < len; i++)
                {
                    // 8 bit data is unsigned with a 128 offset, need to convert to 16 bit signed
                    int sample = (((int)data[i] & 0xff) - 128) << 8;
                    PCMBuffer[outOffset++] = (byte)sample;
                    PCMBuffer[outOffset++] = (byte)(sample >> 8);
                }
                if (writePCMBuffer(PCMBuffer, len*2) < 0) return;
                offset += len;
            }
        }
        else
        {
            // native format
            while (offset < dataLength)
            {
                int len = dataLength - offset;
                if (len > PCM_BUFFER_SIZE)
                   len = PCM_BUFFER_SIZE;
                System.arraycopy(data, offset, PCMBuffer, 1, len);
                if (writePCMBuffer(PCMBuffer, len) < 0) return;
                offset += len;
            }
            
        }
            
    }
    
    
    /**
     * Play a wav file
     * @param file the 8/16-bit PWM (WAV) sample file
     * @param vol the volume percentage 0 - 100
     * @return 0 if the sample has been played or < 0 if
     *         there is an error.
     * @throws FileNotFoundException 
     */
    public int playSample(File file, int vol)
    {
        // First check that we have a wave file. File must be at least 44 bytes
        // in size to contain a RIFF header.
        if (file.length() < RIFF_HDR_SIZE)
            return -9;
        // Now check for a RIFF header
        FileInputStream f = null; 
        DataInputStream d = null;
        boolean playing = false;
        try
        {
        	f = new FileInputStream(file);
        	d = new DataInputStream(new BufferedInputStream(f));

            if (d.readInt() != RIFF_RIFF_SIG)
                return -1;
            // Skip chunk size
            d.readInt();
            // Check we have a wave file
            if (d.readInt() != RIFF_WAVE_SIG)
                return -2;
            if (d.readInt() != RIFF_FMT_SIG)
                return -3;
            // Now check that the format is PCM, Mono 8 bits. Note that these
            // values are stored little endian.
            int sz = readLSBInt(d);
            if (d.readShort() != RIFF_FMT_PCM)
                return -4;
            if (d.readShort() != RIFF_FMT_1CHAN)
                return -5;
            int sampleRate = readLSBInt(d);
            if (sampleRate > 48000)
                return -7;
            d.readInt();
            d.readShort();
            short sampleSize = d.readShort();
            if (sampleSize != RIFF_FMT_16BITS && sampleSize != RIFF_FMT_8BITS)
                return -6;
            playing = true;
            // Skip any data in this chunk after the 16 bytes above
            sz -= 16;
            while (sz-- > 0)
                d.readByte();
            startPCMPlayback(sampleSize == RIFF_FMT_16BITS ? 16 : 8, sampleRate, vol);
            playing = true;
            int dataLen;
            // Skip/process chunks until we  hit eof!
            for(;;)
            {
                int chunk = d.readInt();
                dataLen = readLSBInt(d); 
                if (chunk == RIFF_DATA_SIG)
                {
                    int read;
                    if (sampleSize == RIFF_FMT_16BITS)
                    {
                        // optimized case for native format
                        while(dataLen > 0 && (read = d.read(PCMBuffer, 1, (PCMBuffer.length - 1 < dataLen ? PCMBuffer.length -1 : dataLen))) > 0)
                        {
                            writePCMBuffer(PCMBuffer, read);
                            dataLen -= read;
                        }
                    }
                    else
                    {
                        // need to handle data conversion
                        byte[] data = new byte[PCM_BUFFER_SIZE/2];
                        while(dataLen > 0 && (read = d.read(data, 0, (data.length < dataLen ? data.length : dataLen))) > 0)
                        {
                            writePCMSamples(data, 0, read);
                            dataLen -= read;
                        }                        
                    }
                }
                else
                {
                    // Skip to the start of the next chunk
                    while(dataLen-- > 0)
                        d.readByte();
                }
            }
        }
        catch (EOFException e)
        {
            return 0;
        }
        catch (IOException e)
        {
            return -1;
        }
        finally
        {
            try {
                if (d != null)
                    d.close();
                if (f != null)
                    f.close();
                if (playing)
                    endPCMPlayback();
            }
            catch (IOException e)
            {
                return -1;
            }                
        }
    }


    /**
     * Play a wav file
     * @param file the 8-bit PWM (WAV) sample file
     * @return The number of milliseconds the sample will play for or < 0 if
     *         there is an error.
     * @throws FileNotFoundException 
     */
    public int playSample(File file)
    {
        return playSample(file, VOL_MAX);
    }


    /**
     * Queue a series of PCM samples to play at the
     * specified volume and sample rate.
     * 
     * @param data Buffer containing the samples
     * @param offset Offset of the first sample in the buffer
     * @param len Number of bytes to queue
     * @param sampleRate Sample rate
     * @param vol playback volume
     * @return Number of bytes actually queued
     */
    public int playSample(byte [] data, int offset, int len, int sampleRate, int vol)
    {
        startPCMPlayback(8, sampleRate, vol);
        writePCMSamples(data, offset, len);
        endPCMPlayback();
        return len;
    }
        


    private int waitUntil(int t)
    {
        int t2;
        while ((t2 = (int) System.currentTimeMillis()) < t)
            Thread.yield();
        return t2;
    }

    /**
     * Play a note with attack, decay, sustain and release shape. This function
     * allows the playing of a more musical sounding note. It uses a set of
     * supplied "instrument" parameters to define the shape of the notes 
     * envelope.
     * @param inst Instrument definition
     * @param freq The note to play (in Hz)
     * @param len  The duration of the note (in ms)
     */
    public void playNote(int[] inst, int freq, int len)
    {
        // Generate an envelope controlled note. The instrument array contains
        // the shape of the envelope. The attack period, the decay period the
        // level to decay to and the level to decay to during the sustain part
        // of the note finally the length of the actual release period. All
        // periods are given in 2000th of a second units. This is because the
        // generation of a tone using the playTone function will often take
        // more than 1ms to execute. This means that the minimum tone segment
        // that we can reliably generate is 2ms and so we use this unit as the
        // basis of note generation.
        int segLen = inst[0];
        // All volume settings are scaled by 100.
        int step = 9000 / segLen;
        int vol = 100;
        int oldVol = 0;
        //System.out.println("Start playing");
        // We do not really have fine grained enough timing so try and keep
        // things aligned as closely as possible to a tick by waiting here
        // before we start for the next tick.
        int t = waitUntil((int) System.currentTimeMillis() + 1);
        // Generate the attack profile from 20 to full volume
        playFreq(freq, len+2000, vol / 100);
        len /= 2;
        for (int i = 0; i < segLen; i++)
        {
            vol += step;
            if (oldVol != vol/100)
            {
                playFreq(freq, 100, vol / 100);
                oldVol = vol/100;
            }
            t = waitUntil(t + 2);
        }
        len -= segLen;
        // Now do the decay
        segLen = inst[1];
        if (segLen > 0)
        {
            step = inst[2] / segLen;
            for (int i = 0; i < segLen; i++)
            {
                vol -= step;
                if (oldVol != vol/100)
                {
                    playFreq(freq, 100, vol / 100);
                    oldVol = vol/100;
                }
                t = waitUntil(t + 2);
            }
            len -= segLen;
        }
        segLen = inst[4];
        len -= segLen;
        // adjust length of the sustain and possibly the release to match the
        // requested note length
        if (len > 0)
        {
            step = inst[3] / len;
            for (int i = 0; i < len; i++)
            {
                vol -= step;
                if (oldVol != vol/100)
                {
                    playFreq(freq, 100, vol / 100);
                    oldVol = vol/100;
                }
                t = waitUntil(t + 2);
            }
        }
        else
            segLen += len;
        // Finally do the release
        if (segLen > 0)
        {
            //System.out.println("RVol: " +vol);
            step = (vol - 100) / segLen;
            for (int i = 0; i < segLen; i++)
            {
                vol -= step;
                if (oldVol != vol/100)
                {
                    playFreq(freq, 100, vol / 100);
                    oldVol = vol/100;
                }
                t = waitUntil(t + 2);
            }
        }
        byte []cmd = new byte[1];
        cmd[0] = OP_BREAK;
        dev.write(cmd, 1);
    }

    /**
     * Set the master volume level
     * @param vol 0-100
     */
    public void setVolume(int vol)
    {
        if (vol > VOL_MAX) vol = VOL_MAX;
        if (vol < 0) vol = 0;
        masterVolume = vol;
    }

    /**
     * Get the current master volume level
     * @return the current master volume 0-100
     */
    public int getVolume()
    {
        return masterVolume;
    }
    
    /**
     * Load the current system settings associated with this class. Called
     * automatically to initialize the class. May be called if it is required
     * to reload any settings.
     */
    public void loadSettings()
    {
        masterVolume = SystemSettings.getIntSetting(VOL_SETTING, 80);
    }
}
