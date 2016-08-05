package lejos.robotics.navigation;
import java.util.ArrayList;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;

/**
 * The Pilot class is a software abstraction of the Pilot mechanism
 * of a robot. It contains methods to control robot movements: travel forward or
 * backward in a straight line or a circular path or rotate to a new direction.<br>
 * This class will work with any chassis. Some types of chassis might not support all the 
 * movements this pilot support.  
 * An object of this class assumes that it has exclusive control of
 * its motors. If any other object makes calls to its motors, the results are
 * unpredictable. <br>
 * It automatically updates a
 * {@link lejos.robotics.localization.OdometryPoseProvider} which has called the
 * <code>addMoveListener</code> method on this object.<br>
 * Some methods optionally return immediately so the thread that called it can
 * do things while the robot is moving, such as monitor sensors and call
 * {@link #stop()}.<br>
 * Handling stalls: If a stall is detected, <code>isStalled()</code> returns
 * <code>
 * true </code>, <code>isMoving()</code> returns <code>false</code>,
 * <code>moveStopped()
 * </code> is called, and, if a blocking method is executing, that method exits.
 * The units of measure for travel distance, speed and acceleration are the
 * units used in specifying the wheel diameter in the
 * chassis. <br>
 * In all the methods that cause the robot to change its heading (the angle
 * relative to the X axis in which the robot is facing) the angle parameter
 * specifies the change in heading. A positive angle causes a turn to the left
 * (anti-clockwise) to increase the heading, and a negative angle causes a turn
 * to the right (clockwise). <br>
 * Example of use of come common methods:
 * <p>
 * <code><pre>
 * Wheel wheel1 = DifferentialChassis.modelWheel(Motor.A, 43.2).offset(-72);
 * Wheel wheel2 = DifferentialChassis.modelWheel(Motor.D, 43.2).offset(72);
 * Chassis chassis = new DifferentialChassis(new Wheel[]{wheel1, wheel2}); 
 * MovePilot pilot = new MovePilot(chassis);
 * pilot.setRobotSpeed(30);  // cm per second
 * pilot.travel(50);         // cm
 * pilot.rotate(-90);        // degree clockwise
 * pilot.travel(-50,true);  //  move backward for 50 cm
 * while(pilot.isMoving())Thread.yield();
 * pilot.rotate(-90);
 * pilot.rotateTo(270);
 * pilot.stop();
 * </pre></code>
 * </p>
 * 
 * 
 **/
public class MovePilot implements ArcRotateMoveController {
  private double                  minRadius   = 0;      
  final private Chassis           chassis;
  private ArrayList<MoveListener> _listeners  = new ArrayList<MoveListener>();
  private double                  linearSpeed;
  private double                  linearAcceleration;
  private double                  angularAcceleration;
  private double                  angularSpeed;
  private Monitor                 _monitor;
  private boolean                 _moveActive = false;
  private Move                    move = null;
  private boolean                 _replaceMove = false;

  /**
   * Allocates a Pilot object, and sets the physical parameters of
   * the robot.<br>
   * Assumes Motor.forward() causes the robot to move forward.
   * 
   * @param wheelDiameter
   *          Diameter of the tire, in any convenient units (diameter in mm is
   *          usually printed on the tire).
   * @param trackWidth
   *          Distance between center of right tire and center of left tire, in
   *          same units as wheelDiameter.
   * @param leftMotor
   *          The left Motor (e.g., Motor.C).
   * @param rightMotor
   *          The right Motor (e.g., Motor.A).
   */
  @Deprecated
  public MovePilot(final double wheelDiameter, final double trackWidth, final RegulatedMotor leftMotor,
      final RegulatedMotor rightMotor) {
    this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
  }

  /**
   * Allocates a Pilot object, and sets the physical parameters of
   * the robot.<br>
   * 
   * @param wheelDiameter
   *          Diameter of the tire, in any convenient units (diameter in mm is
   *          usually printed on the tire).
   * @param trackWidth
   *          Distance between center of right tire and center of left tire, in
   *          same units as wheelDiameter.
   * @param leftMotor
   *          The left Motor (e.g., Motor.C).
   * @param rightMotor
   *          The right Motor (e.g., Motor.A).
   * @param reverse
   *          If true, the NXT robot moves forward when the motors are running
   *          backward.
   */
  @Deprecated
  public MovePilot(final double wheelDiameter, final double trackWidth, final RegulatedMotor leftMotor,
      final RegulatedMotor rightMotor, final boolean reverse) {
    this(wheelDiameter, wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
  }

  /**
   * Allocates a Pilot object, and sets the physical parameters of
   * the robot.<br>
   * 
   * @param leftWheelDiameter
   *          Diameter of the left wheel, in any convenient units (diameter in
   *          mm is usually printed on the tire).
   * @param rightWheelDiameter
   *          Diameter of the right wheel. You can actually fit intentionally
   *          wheels with different size to your robot. If you fitted wheels
   *          with the same size, but your robot is not going straight, try
   *          swapping the wheels and see if it deviates into the other
   *          direction. That would indicate a small difference in wheel size.
   *          Adjust wheel size accordingly. The minimum change in wheel size
   *          which will actually have an effect is given by minChange =
   *          A*wheelDiameter*wheelDiameter/(1-(A*wheelDiameter) where A =
   *          PI/(moveSpeed*360). Thus for a moveSpeed of 25 cm/second and a
   *          wheelDiameter of 5,5 cm the minChange is about 0,01058 cm. The
   *          reason for this is, that different while sizes will result in
   *          different motor speed. And that is given as an integer in degree
   *          per second.
   * @param trackWidth
   *          Distance between center of right tire and center of left tire, in
   *          same units as wheelDiameter.
   * @param leftMotor
   *          The left Motor (e.g., Motor.C).
   * @param rightMotor
   *          The right Motor (e.g., Motor.A).
   * @param reverse
   *          If true, the NXT robot moves forward when the motors are running
   *          backward.
   */
  @Deprecated 
  public MovePilot(final double leftWheelDiameter, final double rightWheelDiameter, final double trackWidth,
      final RegulatedMotor leftMotor, final RegulatedMotor rightMotor, final boolean reverse) {
    this(new WheeledChassis(new Wheel[] { 
        WheeledChassis.modelWheel(leftMotor, leftWheelDiameter).offset(trackWidth / 2).invert(reverse),
        WheeledChassis.modelWheel(rightMotor, rightWheelDiameter).offset(-trackWidth / 2).invert(reverse) }, WheeledChassis.TYPE_DIFFERENTIAL));
  }

  /**
   * Allocates a Pilot object.<br>
   * 
   * @param chassis
   *          A Chassis object describing the physical parameters of the robot.
   */
  public MovePilot(Chassis chassis) {
    this.chassis = chassis;
    linearSpeed = chassis.getMaxLinearSpeed() * 0.8;
    angularSpeed = chassis.getMaxAngularSpeed() * 0.8;
    chassis.setSpeed(linearSpeed, this.angularSpeed);
    linearAcceleration = getLinearSpeed() * 4;
    angularAcceleration = getAngularSpeed() * 4;
    chassis.setAcceleration(linearAcceleration, angularAcceleration);
    minRadius = chassis.getMinRadius();
    _monitor = new Monitor();
    _monitor.start();

  }

  // Getters and setters of dynamics

  @Override
  public void setLinearAcceleration(double acceleration) {
    linearAcceleration = acceleration;
    chassis.setAcceleration(linearAcceleration, angularAcceleration);
  }

  @Override
  public double getLinearAcceleration() {
    return linearAcceleration;
  }
  
  @Override
  public void setAngularAcceleration(double acceleration) {
    angularAcceleration = acceleration;
    chassis.setAcceleration(linearAcceleration, angularAcceleration);
  }

  @Override
  public double getAngularAcceleration() {
    return angularAcceleration;
  }

  @Override
  public void setLinearSpeed(double speed) {
    linearSpeed = speed;
    chassis.setSpeed(linearSpeed, angularSpeed);
   }

  @Override
  public double getLinearSpeed() {
    return linearSpeed;
  }

  @Override
  public double getMaxLinearSpeed() {
    return chassis.getMaxLinearSpeed();
  }

  @Override
  public void setAngularSpeed(double speed) {
    angularSpeed = speed;
    chassis.setSpeed(linearSpeed, angularSpeed);
  }

  @Override
  public double getAngularSpeed() {
    return angularSpeed;
  }

  @Override
  public double getMaxAngularSpeed() {
    return chassis.getMaxAngularSpeed();
  }

  @Override
  public double getMinRadius() {
    return minRadius;
  }

  @Override
  public void setMinRadius(double radius) {
    minRadius = radius;
  }

  // Moves of the travel family

  @Override
  public void forward() {
    travel(Double.POSITIVE_INFINITY, true);

  }

  @Override
  public void backward() {
    travel(Double.NEGATIVE_INFINITY, true);
  }

  @Override
  public void travel(double distance) {
    travel(distance, false);

  }

  @Override
  public void travel(double distance, boolean immediateReturn) {
    if (chassis.isMoving())
      stop();
    move = new Move(Move.MoveType.TRAVEL, (float) distance, 0, (float) linearSpeed, (float) angularSpeed, chassis.isMoving());
    chassis.moveStart();
    chassis.travel(distance);
    movementStart(immediateReturn);
  }

  // Moves of the Arc family

  @Override
  public void arcForward(double radius) {
    arc(radius, Double.POSITIVE_INFINITY, true);
  }

  @Override
  public void arcBackward(double radius) {
    arc(radius, Double.NEGATIVE_INFINITY, true);
  }

  @Override
  public void arc(double radius, double angle) {
    arc(radius, angle, false);
  }

  @Override
  public void travelArc(double radius, double distance) {
    travelArc(radius, distance, false);
  }

  @Override
  public void travelArc(double radius, double distance, boolean immediateReturn) {
    arc(radius,  distance / (2 * Math.PI), immediateReturn);
  }

  @Override
  public void rotate(double angle) {
    rotate(angle, false);
  }

  @Override
  public void rotate(double angle, boolean immediateReturn) {
    arc(0, angle, immediateReturn);
  }

  public void rotateLeft() {
    rotate(Double.POSITIVE_INFINITY, true);
  }

  public void rotateRight() {
    rotate(Double.NEGATIVE_INFINITY, true);
  }


  @Override
  public void arc(double radius, double angle, boolean immediateReturn) {
    if (Math.abs(radius) < minRadius) {
      throw new RuntimeException("Turn radius too small.");
    }
    if (_moveActive) {
      stop();
    }
    if (radius == 0) {
      move = new Move(Move.MoveType.ROTATE, 0, (float) angle, (float) linearSpeed, (float) angularSpeed, chassis.isMoving());
    } else {
      move = new Move(Move.MoveType.ARC, (float) (Math.toRadians(angle) * radius), (float) angle, (float) linearSpeed, (float) angularSpeed,
          chassis.isMoving());
    }
    chassis.moveStart();
    chassis.arc(radius, angle);
    movementStart(immediateReturn);
  }

  // Stops. Stops must be blocking!

  @Override
  public void stop() {
    // This method must be blocking
    chassis.stop();
    while (_moveActive) Thread.yield();
  }
  
  // State
  @Override
  public boolean isMoving() {
    return chassis.isMoving();
  }



  // Methods dealing the start and end of a move
  private void movementStart(boolean immediateReturn) {
    for (MoveListener ml : _listeners)
      ml.moveStarted(move, this);
    _moveActive = true;
    synchronized (_monitor) {
      _monitor.notifyAll();
    }
    if (immediateReturn) return;
    while (_moveActive) Thread.yield();
  }

  private void movementStop() {
    if ( ! _listeners.isEmpty()) {
      chassis.getDisplacement(move);
      for (MoveListener ml : _listeners)
        ml.moveStopped(move, this);
    }
    _moveActive = false;
  }

  @Override
  public Move getMovement() {
    if (_moveActive) {
    return chassis.getDisplacement(move);
    }
    else {
      return new Move(Move.MoveType.STOP, 0, 0, false);
    }
  }

  @Override
  public void addMoveListener(MoveListener listener) {
    _listeners.add(listener);

  }

  /**
   * The monitor class detects end-of-move situations when non blocking move
   * call were made and makes sure these are dealt with.
   *
   */
  private class Monitor extends Thread {
    public boolean more = true;

    public Monitor() {
      setDaemon(true);
    }

    public synchronized void run() {
      while (more) {
        if (_moveActive) {
          if (chassis.isStalled())
            MovePilot.this.stop();
          if (!chassis.isMoving() || _replaceMove) {
            movementStop();
            _moveActive = false;
            _replaceMove = false;
          }
        }
        // wait for an event
        try {
          wait(_moveActive ? 1 : 100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }


}
