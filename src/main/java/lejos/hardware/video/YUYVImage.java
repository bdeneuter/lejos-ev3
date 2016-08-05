package lejos.hardware.video;

   

import lejos.hardware.lcd.GraphicsLCD;


 /**
  * Class to represent a YUV video image
  * @author Gabriel Ferrer, Andy
  *
  */
 public class YUYVImage {  
     private byte[] pix;  
     private int width, height;  

     /**
      * Create a YUYV image of the requested size using the byte array as the source of pixel information
      * @param pix pixels
      * @param width image width
      * @param height image height
      */
     public YUYVImage(byte[] pix, int width, int height) {  
         this.pix = pix;  
         this.width = width;  
         this.height = height;  
     }

     /**
      * Create a new YUYV image of the specified size
      * @param width 
      * @param height
      */
     public YUYVImage(int width, int height) {
         this.pix = new byte[width*height*2];
         this.width = width;
         this.height = height;
     }
     

     /**
      * return the number of pixels in the image
      * @return number of pixels
      */
     public int getNumPixels() {return pix.length / 2;}  

     /**
      * return the image width
      * @return width
      */
     public int getWidth() {return width;}  
       

     /**
      * return the image height
      * @return height
      */
     public int getHeight() {return height;}  
       

     /**
      * return the Y component of the specified pixel
      * @param x
      * @param y
      * @return Y component
      */
     public int getY(int x, int y) {  
         return pix[2 * (y * width + x)];  
     }  

     /**
      * return the U component of the specified pixel
      * @param x
      * @param y
      * @return U component
      */
     public int getU(int x, int y) {  
         return pix[getPairBase(x, y) + 1];  
     }  
       
     /**
      * return the V component of the specified pixel
      * @param x
      * @param y
      * @return V component
      */
     public int getV(int x, int y) {  
         return pix[getPairBase(x, y) + 3];  
     }  
       
     private int getPairBase(int x, int y) {  
         return 2 * (y * width + (x - x % 2));  
     }  
       
     /**
      * Return the mean of the Y components for the image (mean brightness)
      * @return mean Y value
      */
     public int getMeanY() {  
         int total = 0;  
         for (int i = 0; i < pix.length; i += 2) {  
             total += (int)pix[i] & 0xff;  
         }  
         return total / getNumPixels();  
     }  

     /**
      * Display the image at the specified location on the provided monochrome device or image. Pixels
      * with a Y value below threshold will be set, those above unset.
      * @param threshold
      */
     public void display(GraphicsLCD device, int xDest, int yDest, int threshold) {  
         for (int i = 0; i < pix.length; i += 2) {  
             int x = (i / 2) % width;  
             int y = (i / 2) / width;  
             device.setPixel(xDest+x, yDest+y, ((int)pix[i] & 0xff) < threshold ? 1 : 0);  
         }  
     }  
 }  
   