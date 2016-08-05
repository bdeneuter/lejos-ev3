package lejos.robotics.navigation;

public interface RotateMoveController extends MoveController {
  /**
   * Rotates the NXT robot the specified number of degrees; direction determined by the sign of the parameter.
   * Method returns when rotation is done.
   * 
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   */
  public void rotate(double angle);  
  /**
   * Rotates the NXT robot the specified number of degrees; direction determined by the sign of the parameter.
   * Method returns immediately if immediateReturn flag is true,  otherwise returns when rotation is done.
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   * @param immediateReturn  If true, method returns immediately,  otherwise blocks until rotation is complete.
   */
  public void rotate(double angle, boolean immediateReturn);
  /**
   * sets the rotation speed of the robot (the angular velocity of the rotate()
   * methods)
   * @param speed in degrees per second
   */
  public void setAngularSpeed(double speed);
  
  /**
   * Returns the value of the rotation speed
   * @return the rotate speed in degrees per second
   */
  public double getAngularSpeed();

  /**
   * returns the maximum value of the rotation speed;
   * @return max rotation speed
   */
  public double getMaxAngularSpeed();
  
  /**
   * Sets the acceleration at which the robot will accelerate at the start of a move and decelerate at the end of a move.
   * Acceleration is measured in units/second^2. e.g. If wheel diameter is cm, then acceleration is cm/sec^2. <p>
   * If acceleration is set during a move it will not be in used for the current move, it will be in effect with the next move.
   * @param acceleration in chosen units/second^2
   */
  public void setAngularAcceleration(double acceleration);
  
  /** Returns the acceleration at which the robot accelerates at the start of a move and decelerates at the end of a move.
   * @return acceleration in chosen units/second^2
   */
  public double getAngularAcceleration();
  
public void rotateRight();
  
public void rotateLeft();

}
