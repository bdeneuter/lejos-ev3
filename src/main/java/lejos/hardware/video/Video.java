package lejos.hardware.video;

public interface Video {
    // We only provide the most common formats and definitions
    public static final int PIX_FMT_MJPEG = VideoUtils.fourcc('M', 'J', 'P', 'G'); /* Motion-JPEG   */
    public static final int PIX_FMT_JPEG  = VideoUtils.fourcc('J', 'P', 'E', 'G'); /* JFIF JPEG     */
    public static final int PIX_FMT_YUYV  = VideoUtils.fourcc('Y', 'U', 'Y', 'V'); /* 16  YUV 4:2:2 */
    
    public static final int FIELD_ANY = 0;
    public static final int FIELD_NONE = 1;
    public static final int FIELD_INTERLACED = 4;
    /**
     * Open the device and make it available for use, specify the desired frame size. Note that the actual
     * frame size may be adjusted to conform to the capabilities of the device.
     * @param w the desired frame width
     * @param h the desired frame height
     * @param format desired pixel format
     * @param field desired field layout
     * @param fps the desired frame rate
     * @throws java.io.IOException
     */
    public void open(int w, int h, int format, int field, int fps) throws java.io.IOException;

    /**
     * Open the device and make it available for use, specify the desired frame size. Note that the actual
     * frame size may be adjusted to conform to the capabilities of the device.
     * @param w the desired frame width
     * @param h the desired frame height
     * @throws java.io.IOException
     */
    public void open(int w, int h) throws java.io.IOException;

    /**
     * Grab a single frame from the device and store the image into the supplied array
     * @param frame array to store the frame
     * @return the size of the frame grabbed (in bytes)
     * @throws java.io.IOException
     */
    public int grabFrame(byte[] frame) throws java.io.IOException;
  
    /**
     * Close the webcam, the device will not be available after this call  
     * @throws java.io.IOException
     */
    public void close() throws java.io.IOException;
      
  

    /**
     * Create a byte array suitable for holding a single video frame
     * @return the frame array
     */
    public byte[] createFrame();
  
    /**
     * Return the frame width
     * @return width in pixels
     */
    public int getWidth();

    /**
     * return the frame height
     * @return height in pixels
     */
    public int getHeight();
    
}  