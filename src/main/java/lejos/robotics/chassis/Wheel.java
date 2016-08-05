package lejos.robotics.chassis;

import lejos.robotics.RegulatedMotor;
import lejos.utility.Matrix;

public interface Wheel {

  /** Returns the x,y and r factors to calculate motor speed from wheel linear and angular speed. <p>
   *  The factors form the row of a forward matrix 
   * @return the factors  as a matrix
   */
  public Matrix getFactors();  
  
  public RegulatedMotor getMotor();
  

 
  
}