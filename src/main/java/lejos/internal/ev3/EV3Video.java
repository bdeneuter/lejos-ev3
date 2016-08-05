package lejos.internal.ev3;

import lejos.hardware.video.Video;

/**
 * Class to provide access to a Webcam attached to the EV3 
 * @author Gabriel Ferrer, Andy
 *
 */
public class EV3Video implements Video {  
     // Causes the native library to be loaded from the system.       
     static {System.loadLibrary("ev3video");}  
   
     // Three primary methods for use by clients:  
   
     /**
      * Open the device and make it available for use, specify the desired frame size. Note that the actual
      * frame size may be adjusted to conform to the capabilities of the device.
      * @param w the desired frame width
      * @param h the desired frame height
      * @param format desired pixel format
      * @param field desired field layout
      * @throws java.io.IOException
      */
     public void open(int w, int h, int format, int field, int fps) throws java.io.IOException {  
         width = w;  
         height = h;
         this.format = format;
         this.field = field;
         this.fps = fps;
         bufferSize = setup();  
     }  

     /**
      * Open the device and make it available for use, specify the desired frame size. Note that the actual
      * frame size may be adjusted to conform to the capabilities of the device. The format of the pixel
      * will be YUYV and the fields will be interlaced.
      * @param w the desired frame width
      * @param h the desired frame height
      * @throws java.io.IOException
      */
     public void open(int w, int h) throws java.io.IOException {  
         open(w, h, PIX_FMT_YUYV, FIELD_INTERLACED, 0);
     }  

     /**
      * Grab a single frame from the device and store the image into the supplied array
      * @param frame array to store the frame
      * @throws java.io.IOException
      */
     public int grabFrame(byte[] frame) throws java.io.IOException {  
         return grab(frame);  
     }  
   
     /**
      * Close the webcam, the device will not be availabe after this call  
      * @throws java.io.IOException
      */
     public void close() throws java.io.IOException {  
         dispose();  
     }  
       
     // Three native method stubs, all private:  
   
     // Called by start() before any frames are grabbed.  
     private native int setup() throws java.io.IOException;  
   
     // Called by grabFrame(), which creates a new buffer each time.  
     private native int grab(byte[] img) throws java.io.IOException;  
   
     // Called by end() to clean things up.  
     private native void dispose() throws java.io.IOException;  
   
     // Specified by the user, and retained for later reference.  
     private int width, height;
     private int format, field;
     private int bufferSize;
     private int fps;
   
     // Utility methods  
     private int getBufferSize() {return bufferSize;}  

     /**
      * Create a byte array suitable for holding a single video frame
      * @return the frame array
      */
     public byte[] createFrame() {return new byte[getBufferSize()];}  
   
     /**
      * Return the frame width
      * @return width in pixels
      */
     public int getWidth() {return width;}  

     /**
      * return the frame height
      * @return height in pixels
      */
     public int getHeight() {return height;}  
     

 }  