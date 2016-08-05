package lejos.robotics.chassis;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;
import lejos.utility.Matrix;


/** Represents the chassis of a wheeled robot. 
 * <p>The WheeledChassis provides a control system for driving a mobile robot with motorized wheels. 
 * Both differential and holonomic robots can be represented by the WheeledChassis class.</p>
 * 
 * <p><b>How to create a WheeledChassis object</b><br>
 * The constructor of the DifferentialChassis class accepts an array of Wheel objects. 
 * Each of the wheel objects describes one of the motorized wheels on the chassis. 
 * A Description of a wheel consists of its diameter, its position, its motor and the gear train between wheel and motor.
 * Wheel objects can be created using a modeler class. A modeler for traditional wheel can be obtained using the {@link #modelWheel} method.
 * A modeler for a holonomic wheel can be obtained using the {@link #modelHolonomicWheel} method. </p>.
 * <p>
 * This example creates a WheeledChassis for a differential robot.
 * <pre>
 * Wheel wheel1 = WheeledChassis.modelWheel(Motor.A, 81.6).offset(-70);
 * Wheel wheel2 = WheeledChassis.modelWheel(Motor.D, 81.6).offset(70);
 * Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
 * </pre>
 * </p>
 * <p>
 * This example creates a WheeledChassis for a holonomic robot.
 * <pre>
 *    Wheel wheel1 = WheeledChassis.modelHolonomicWheel(Motor.A, 48).polarPosition(0, 135).gearRatio(2);
 *    Wheel wheel2 = WheeledChassis.modelHolonomicWheel(Motor.B, 48).polarPosition(120, 135).gearRatio(2);
 *    Wheel wheel3 = WheeledChassis.modelHolonomicWheel(Motor.C, 48).polarPosition(240, 135).gearRatio(2);
 *    Chassis chassis = new WheeledChassis(new Wheel[]{wheel1, wheel2, wheel3}, WheeledChassis.TYPE_HOLONOMIC);
 * </pre>
 * </p>
 * <p>Please note that a Chassis can have an unlimited number of motorized wheels but a differential robot 
 * needs at least two wheels and a holonomic robot needs at least three wheels.</p>
 * 
 * <p>See also the {@link Chassis} interface.</p>*  
 * @author Aswin Bouwmeester
 *
 */
public class WheeledChassis implements Chassis {
  protected static final int TACHOCOUNT=0;
  protected static final int MAXSPEED=1;
  protected static final int ROTATIONSPEED=2;
  
  public static final int TYPE_DIFFERENTIAL = 2;
  public static final int TYPE_HOLONOMIC = 3;
  final int              nWheels;
  /**
   * The program adds a dummy wheel to a differential chassis. 
   * This dummy wheel makes it possible to use linear algebra on three elements (x, y, angle) 
   * for the differential chassis. The y value is always zero for a differential chassis.
   */
  protected final int dummyWheels;
  protected final RegulatedMotor[] motor;
  protected double linearSpeed, angularSpeed, linearAcceleration, angularAcceleration;

  final protected Matrix forward;
  final protected Matrix reverse;
  protected RegulatedMotor master;
  protected Matrix tachoAtMoveStart;
  
  final protected Matrix forwardAbs;
  final protected Matrix reverseAbs;
  protected Odometer odometer;


  
  public WheeledChassis(Wheel[] wheels, int dim) {
    nWheels = wheels.length;
    if (nWheels < dim ) throw new  IllegalArgumentException(String.format("The chassis must have at least %d motorized wheels", dim));
    if (dim == TYPE_DIFFERENTIAL) dummyWheels =1;
    else dummyWheels =0;

    // register the motors 
    motor = new RegulatedMotor[nWheels];
    for (int i = 0; i < nWheels; i++) {
      motor[i] = wheels[i].getMotor();
    }
    // Make the wheels synchronized
    this.master = motor[0];
    RegulatedMotor[] sync = new RegulatedMotor[nWheels - 1];
    System.arraycopy(motor, 1, sync, 0, nWheels - 1);
    master.synchronizeWith(sync);

    // create the forward forward matrix
    forward = new Matrix(nWheels + dummyWheels, 3);
    for (int row = 0; row < nWheels; row++) {
      forward.setMatrix(row, row, 0, 2, wheels[row].getFactors());
    }
    if (dummyWheels==1) {
      forward.set(nWheels,0,0);
      forward.set(nWheels,1,1);
      forward.set(nWheels,2,0);
    }
    // create the reverse matrix
    try {
      reverse = forward.inverse();
    }
    catch(RuntimeException e) {
      throw new RuntimeException("Invalid wheel setup, this robot is not controlable. Check position of the wheels.");
    }
    
    // create absolute versions of forward and reverse matrices (used for speed and acceleration that both are always positive).
    forwardAbs = this.copyAbsolute(forward);
    reverseAbs = this.copyAbsolute(reverse);

    // Give speed and acceleration some default values
    double s = this.getMaxLinearSpeed();
    double a = this.getMaxAngularSpeed();
    setSpeed(s/2, a/2);
    setAcceleration(s/2, a/2);
    
    // store position of tacho's
    tachoAtMoveStart = getAttribute(TACHOCOUNT);

  }
  
  
  @Override
  public double getLinearSpeed() {
    return linearSpeed;
  }


  @Override
  public void setLinearSpeed(double linearSpeed) {
    this.linearSpeed = linearSpeed;
  }


  @Override
  public double getAngularSpeed() {
    return angularSpeed;
  }


  @Override
  public void setAngularSpeed(double angularSpeed) {
    this.angularSpeed = angularSpeed;
  }


  @Override
  public double getLinearAcceleration() {
    return linearAcceleration;
  }


  @Override
  public void setLinearAcceleration(double linearAcceleration) {
    this.linearAcceleration = linearAcceleration;
  }


  @Override
  public double getAngularAcceleration() {
    return angularAcceleration;
  }


  @Override
  public void setAngularAcceleration(double angularAcceleration) {
    this.angularAcceleration = angularAcceleration;
  }


  public Matrix getForward() {
    return forward.copy();
  }


  public Matrix getReverse() {
    return reverse.copy();
  }


  @Override
  public void setSpeed(double linearSpeed, double angularSpeed) {
    if (linearSpeed <=0 || angularSpeed <=0) throw new  IllegalArgumentException("Speed must be greater than 0");
    this.linearSpeed=linearSpeed;
    this.angularSpeed = angularSpeed;
  }

  @Override
  public void setAcceleration(double linearAcceleration, double angularAcceleration) {
    if (linearAcceleration <=0 || angularAcceleration <=0) throw new  IllegalArgumentException("Acceleration must be greater than 0");
    this.linearAcceleration=linearAcceleration;
    this.angularAcceleration = angularAcceleration;
  }
  
 
  // State
  
  @Override
  public boolean isMoving() {
    for (RegulatedMotor wheel : motor) {
      if (wheel.isMoving()) {
        return true;
      }
    }
    return false;
  }


  @Override
  public void waitComplete() {
    for (RegulatedMotor wheel : motor)
      wheel.waitComplete();
  }

  @Override
  public boolean isStalled() {
    for (RegulatedMotor wheel : motor) {
      if (wheel.isStalled())
        return true;
    }
    return false;
  }

  @Override
  public double getMinRadius() {
    return 0;
  }
  
  @Override
  public void stop() {
    setVelocity(0, 0, 0);
  }

  @Override
  public void setVelocity(double linearSpeed, double angularSpeed) {
    setVelocity(linearSpeed, 0, angularSpeed);
  }
  
  public void travelCartesian(double xSpeed, double ySpeed, double angularSpeed) {
    setVelocity(Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed), Math.atan2(ySpeed, xSpeed), angularSpeed);
  }

  public synchronized void setVelocity(double linearSpeed, double direction, double angularSpeed) {
    if (dummyWheels ==1 && (direction % 180 != 0) ) throw new RuntimeException("Invalid direction for differential a robot."); 
    // create matrices with speed and acceleration components using direction;
    Matrix motorSpeed = forward.times(toCartesianMatrix(linearSpeed, Math.toRadians(direction), angularSpeed));
    Matrix motorAcceleration = forwardAbs.times(copyAbsolute(toCartesianMatrix(linearAcceleration, Math.toRadians(direction), angularAcceleration)));
    Matrix currentMotorSpeed = (getAttribute(ROTATIONSPEED));

    // calculate acceleration for each of the wheels. 
    // The goal is that all wheels take an even amount of time to reach final speed
    
    // Calculate difference between final speed and current speed
    Matrix dif = motorSpeed.minus(currentMotorSpeed); 
    // Calculate how much time each wheel needs to reach final speed;
    Matrix time = dif.arrayRightDivide(motorAcceleration); 
    // Find the longest time
    double longestTime = getMax(time); 
    if (longestTime == 0) return; // Aha, no speed differences. Do nothing.
    // Devide speed difference by the longest time to get acceleration for each wheel
    dif = dif.timesEquals(1 / longestTime); 
    // Set the dynamics and execute motion
    master.startSynchronization();
    for (int i = 0; i < nWheels; i++) {
      motor[i].setAcceleration((int) dif.get(i, 0));
      motor[i].setSpeed((int) Math.abs(motorSpeed.get(i, 0)));
      switch((int)Math.signum(motorSpeed.get(i, 0))) {
        case -1: motor[i].backward(); break;
        case 0: motor[i].stop(); break;
        case 1: motor[i].forward(); break;
      }
    }
    master.endSynchronization();
  }
  
  @Override
  public  void travel(double linear) {
    if (Double.isInfinite(linear) ) {
      setVelocity(Math.signum(linear) * linearSpeed,0);
    }
    else {
      Matrix motorDelta = forward.times(toMatrix(linear, 0, 0));
      Matrix motorSpeed = forwardAbs.times(toMatrix(linearSpeed, 0, 0 ));
      Matrix motorAcceleration = forwardAbs.times(toMatrix(linearAcceleration, 0, 0 ));
      setMotors( motorDelta, motorSpeed, motorAcceleration);
    }
  }

  @Override
  public  void rotate(double angular) {
    if (Double.isInfinite(angular) ) {
      setVelocity(0, Math.signum(angular) * angularSpeed);
    }
    else {
      Matrix motorDelta = forward.times(toMatrix(0, 0, angular));
      Matrix motorSpeed = forwardAbs.times(toMatrix(0, 0, angularSpeed ));
      Matrix motorAcceleration = forwardAbs.times(toMatrix(0, 0,  angularAcceleration ));
      setMotors( motorDelta, motorSpeed, motorAcceleration);
    }
  }


  @Override
  public void arc (double radius, double angle) {
    if (angle == 0) return;
    // ratio between linear and angular speed that corresponds with the radius
    double ratio =  Math.abs(Math.PI * radius / 180 );
  
    if (Double.isInfinite(angle)) {
      // Decrease one of both speed components so that they have the calculated ratio and call travel method
      if (ratio>1) 
        setVelocity(Math.signum(angle) * linearSpeed, 0, Math.signum(radius) * linearSpeed/ratio);
      else
        setVelocity(Math.signum(angle) * angularSpeed * ratio, 0, Math.signum(radius) * angularSpeed);
    }
    else if (radius == 0) {
      rotate(angle);
      return;
    }
    else if (Double.isInfinite(radius)) {
      if (angle < 0) 
        travel(Double.POSITIVE_INFINITY);
      else 
        travel(Double.NEGATIVE_INFINITY);
    }
    else {
      // Matrix holding linear and angular distance matching the specified arc
      Matrix displacement  = toMatrix(Math.signum(angle) * 2 * Math.PI * Math.abs(radius) * Math.abs(angle) / 360 , 0, Math.signum(radius) * angle);
      
      Matrix tSpeed;
      Matrix tAcceleration;
      // Decrease one of both speed and acceleration components so that they have the calculated ratio
      if (ratio > 1) {
        tSpeed=toMatrix(linearSpeed, 0, linearSpeed / ratio);
        tAcceleration=toMatrix(linearAcceleration, 0, linearAcceleration / ratio);
      }
      else {
        tSpeed=toMatrix(angularSpeed * ratio, 0, angularSpeed );
        tAcceleration=toMatrix(angularAcceleration * ratio, 0, angularAcceleration );
      }
      // calculate the displacement of the motors from robot displacement
      Matrix motorDelta = forward.times(displacement);
      // calculate the ratio between motor displacements when the largest displacement is set to 1;
      Matrix mRatio = motorDelta.times(1 / this.getMax(motorDelta));
      // Calculate the speed of the fasted moving robot and from this 
      // calculated the speed of the other motors using the motor displacement ratio
      Matrix motorSpeed = mRatio.times(getMax(forwardAbs.times(tSpeed)));
      // repeat for acceleration
      Matrix motorAcceleration = mRatio.times(getMax(forwardAbs.times(tAcceleration)));
      setMotors( motorDelta, motorSpeed, motorAcceleration);
    }
  }
  
  /** Utility method to set distance, speed and acceleration for each motor
   * @param motorDelta
   * @param motorSpeed
   * @param motorAcceleration
   */
  protected synchronized void setMotors(Matrix motorDelta, Matrix motorSpeed, Matrix motorAcceleration) {
    master.startSynchronization();
    for (int i = 0; i < nWheels; i++) {
      motor[i].setAcceleration((int) motorAcceleration.get(i, 0));
      motor[i].setSpeed((int) motorSpeed.get(i, 0));
      motor[i].rotate((int) motorDelta.get(i, 0));
    }
    master.endSynchronization();
  }


  // Dynamics
  @Override
  public double getMaxLinearSpeed() {
    Matrix motorSpeed = getAttribute(MAXSPEED);
    
    Matrix wheelSpeed = reverseAbs.times(motorSpeed);
    return Math.sqrt(wheelSpeed.get(0, 0) * wheelSpeed.get(0, 0) + wheelSpeed.get(1, 0) * wheelSpeed.get(1, 0));
  }

  @Override
  public double getMaxAngularSpeed() {
    Matrix motorSpeed = getAttribute(MAXSPEED);
    Matrix wheelSpeed = reverseAbs.times(motorSpeed);
    return wheelSpeed.get(2, 0);
  }
  
  @Override
  public Matrix getCurrentSpeed() {
    Matrix motorSpeed = getAttribute(ROTATIONSPEED);
    Matrix wheelSpeed = reverse.times(motorSpeed);
    return toPolar(wheelSpeed.get(0, 0),  wheelSpeed.get(1, 0),  wheelSpeed.get(2, 0));
  }
  
  @Override
  public double getLinearVelocity() {
    return getCurrentSpeed().get(0,0);
  }

  @Override
  public double getLinearDirection() {
    return getCurrentSpeed().get(1,0);
  }
  
  @Override
  public double getAngularVelocity() {
    return getCurrentSpeed().get(2,0);
  }



  // Support for move reconstruction for move based pilots
  
  public void moveStart() {
    tachoAtMoveStart = getAttribute(TACHOCOUNT);
  }
  
  @Override
  public Move getDisplacement(Move move) {
    Matrix currentTacho = getAttribute(TACHOCOUNT);
    Matrix delta = currentTacho.minus(tachoAtMoveStart);

    delta = reverse.times(delta);
    double distance = Math.sqrt(delta.get(0, 0) * delta.get(0, 0) + delta.get(1, 0) * delta.get(1, 0));
    double rotation = delta.get(2, 0);
    if (distance == 0 && rotation == 0  )
      move.setValues(Move.MoveType.STOP, (float) distance, (float) rotation, isMoving());
    else if (Math.abs(rotation) < 1 )
      move.setValues(Move.MoveType.TRAVEL, (float) distance, (float) rotation, isMoving());
    else if (Math.abs(distance) < 1)
      move.setValues(Move.MoveType.ROTATE, (float) distance, (float) rotation, isMoving());
    else move.setValues(Move.MoveType.ARC, (float) distance, (float) rotation, isMoving());
    return move;
  }
  
 
  /** Provides a modeler object to model a Holonomic motorized wheel on the chassis
   * @param motor
   * The regulated motor that drives the wheel
   * @param diameter
   * The diameter of the wheel in a unit of choice.
   * @return the modeler
   */
  public static HolonomicModeler modelHolonomicWheel(RegulatedMotor motor, double diameter) {
    return new HolonomicModeler(motor, diameter);
  }
  
  /** The Modeler class helps to model a wheel. Wheel attributes can be modeled using methods.
   * <ul>
   * <li>polarPosition() specifies the location of the wheel</li>
   * <li>gearRatio() specifies the gear ratio of the gear train between motor and wheel</li>
   * <li>invert() inverts the direction of the motor. Equivalent to a negative gearing</li>
   * </ul>
   * <p>
   * @author Aswin Bouwmeester
   *
   */
  public static class HolonomicModeler implements Wheel {
    protected RegulatedMotor motor;
    protected double         diameter;
    protected double         gearRatio = 1;
    protected double         offset  = 0;
    protected Pose           pose = new Pose(0,0,0);
    protected boolean        invert = false;

    /**
     * Creates a modeler object to model a robot wheel
     * 
     * @param motor
     *          The regulated motor that drives the wheel
     * @param diameter
     *          The diameter of the wheel (Lego wheels have the diameter printed
     *          on the side)
     */
    public HolonomicModeler(RegulatedMotor motor, double diameter) {
      this.motor = motor;
      this.diameter = diameter;
    }

    /** Specifies the location and orientation of the wheel using polar coordinates<p>
     * Use this method only when the wheel axis points to the center of the robot.
     * @param angle
     * Angle between wheel axis and the robots x-axis
     * @param radius
     * Distance between center of the wheel and center of the robot
     * @return the modeler
     */
    public HolonomicModeler polarPosition(double angle, double radius) {
      pose = new Pose((float)(radius * Math.cos(Math.toRadians(angle))), (float) (radius  * Math.sin(Math.toRadians(angle))), (float)(angle) );
      pose.rotateUpdate(90);
      return this;
    }
    
    /** Specifies the location and orientation of the wheel using a cartesian coordinates<p>
     * @param x
     * X-position of the center of the wheel in respect to the robots origin
     * @param y
     * Y-position of the center of the wheel in respect to the robots origin
     * @param angle
     * Angle between the driving direction of the wheel and the robots x-axis
     * @return the modeler
     */
    public HolonomicModeler cartesianPosition(double x, double y, double angle) {
      pose = new Pose((float)x, (float)y, (float)angle);
      return this;
    }

    /**
     * Defines the gear train between motor and wheel.
     * 
     * @param val
     *          The ratio between wheel speed and motor speed. A ratio greater than 1 means the wheel turns faster than the motor.
     * @return the modeler
     */
    public HolonomicModeler gearRatio(double val) {
      this.gearRatio = val;
      return this;
    }

    /**
     * Inverts the motor direction
     * 
     * @param val
     * @return the modeler
     */
    public HolonomicModeler invert(boolean val) {
      invert = val; 
      return this;
    }

    public Matrix getFactors() {
      // TODO: correct angular component when the wheel axis doesn't go through the origin of the robot

      Matrix factors = new Matrix(1, 3);
      factors.set(0, 0, Math.cos(Math.toRadians(pose.getHeading())) * (360 ) / (diameter * Math.PI * gearRatio * (invert ? -1 : 1)));
      factors.set(0, 1, Math.sin(Math.toRadians(pose.getHeading())) * (360 ) / (diameter * Math.PI * gearRatio * (invert ? -1 : 1)));
      factors.set(0, 2, (( 2 * pose.getLocation().length() ) / (diameter * gearRatio * (invert ? -1 : 1))));
      return factors;
    }

    public RegulatedMotor getMotor() {
      return motor;
    }

  }  
  


  
  /** Provides a modeler object to model a Holonomic motorized wheel on the chassis
   * @param motor
   * The regulated motor that drives the wheel
   * @param diameter
   * The diameter of the wheel in a unit of choice.
   * @return the modeler
   */
  public static Modeler modelWheel(RegulatedMotor motor, double diameter) {
    return new Modeler(motor, diameter);
  }

  /** The Modeler class helps to model a wheel. Wheel attributes can be modeled using methods.
   * <ul>
   * <li>offset() specifes the location of the wheel along the y-axis</li>
   * <li>gearRatio() specifes the gear ratio of the gear train between motor and wheel</li>
   * <li>invert() inverts the direction of the motor. Equivalent to a negative gearing</li>
   * </ul>
   * <p>
   * @author Aswin Bouwmeester
   *
   */
  public static class Modeler implements Wheel {
    protected RegulatedMotor motor;
    protected double         diameter;
    protected double         gearRatio = 1;
    protected double         offset  = 0;
    protected double         angle   = 0;
    protected boolean        invert = false;

    /**
     * Creates a modeler object to model a robot wheel
     * 
     * @param motor
     *          The regulated motor that drives the wheel
     * @param diameter
     *          The diameter of the wheel (Lego wheels have the diameter printed
     *          on the side)
     */
    public Modeler(RegulatedMotor motor, double diameter) {
      this.motor = motor;
      this.diameter = diameter;
    }

    /**
     * Defines the offset off the wheel
     * 
     * @param val
     *          The distance between the robots yPose-axis and the center of the
     *          wheel
     * @return the modeler
     */
    public Modeler offset(double val) {
      this.offset = val;
      return this;
    }

    /**
     * Defines the gear train between motor and wheel.
     * 
     * @param val
     *          The ratio between wheel speed and motor speed
     * @return the modeler
     */
    public Modeler gearRatio(double val) {
      this.gearRatio = val;
      return this;
    }

    /**
     * Inverts the motor direction
     * 
     * @param val
     * @return the modeler
     */
    public Modeler invert(boolean val) {
      invert = val;
      return this;
    }

    public Matrix getFactors() {
      Matrix factors = new Matrix(1, 3);
      factors.set(0, 0, (360 * gearRatio * (invert ? -1 : 1)) / (diameter * Math.PI));
      factors.set(0, 1, 0);
      factors.set(0, 2, -((2.0 * offset * gearRatio * (invert ? -1 : 1)) / diameter));
      return factors;
    }

    public RegulatedMotor getMotor() {
      return motor;
    }

  }

  
  
  @Override
  public PoseProvider getPoseProvider() {
    if (odometer == null) odometer = new Odometer();
    return  odometer;
  }
  
  /** The odometer keeps track of the robot pose based on odometry using the encoders of the regulated motors of the wheels.
   * @author Aswin Bouwmeester
   *
   */
  private class Odometer implements PoseProvider {
    Matrix lastTacho;
    double xPose, yPose, aPose;

    int    time = 64;

    private Odometer() {
      lastTacho = getAttribute(TACHOCOUNT);
      PoseTracker tracker = new PoseTracker();
      tracker.setDaemon(true);
      tracker.start();
    }

    @Override
    public Pose getPose() {
      return new Pose((float) xPose, (float) yPose, (float) aPose);
    }

    @Override
    public synchronized void setPose(Pose pose) {
      xPose = pose.getX();
      yPose = pose.getY();
      aPose = pose.getHeading();
    }

    private synchronized void updatePose() {
      Matrix currentTacho = getAttribute(TACHOCOUNT);
      Matrix delta = currentTacho.minus(lastTacho);

      int max = (int) getMax(delta);

      delta = reverse.times(delta);
      double sin = Math.sin(Math.toRadians(aPose));
      double cos = Math.cos(Math.toRadians(aPose));
      double x = delta.get(0, 0);
      double y = delta.get(1, 0);
      
      xPose += cos * x - sin * y;
      yPose += sin * x + cos * y;
      aPose += delta.get(2, 0);
      while (aPose < 180)
        aPose += 360;
      while (aPose > 180)
        aPose -= 360;

      // adjust loop speed (between 4 and 64 msec);
      if (max > 10) time=time / 2;
      if (max < 10) time=time * 2;
      time = Math.max(Math.min(time, 64), 4);
      lastTacho = currentTacho;
    }

    private class PoseTracker extends Thread {
      public void run() {
        while (true) {
          updatePose();
          Delay.msDelay(time);
        }
      }
    }
  }
  
  // Matrix utilities

/**
 * Create a Matrix to store linear and angular components
 * 
 * @param x
 * @param y
 * @param angular
 * @return the modeler
 */
protected Matrix toMatrix(double x, double y, double angular) {
  Matrix m = new Matrix(3, 1);
  m.set(0, 0, x);
  m.set(1, 0, y);
  m.set(2, 0, angular);
  return m;
}

protected Matrix toCartesianMatrix ( double radius, double direction, double angular) {
  Matrix m = new Matrix(3, 1);
  m.set(0, 0, Math.cos(direction) * radius);
  m.set(1, 0, Math.sin(direction) * radius);
  m.set(2, 0, angular);
  return m;  
}

protected Matrix toPolar(double x, double y, double angular) {
  Matrix m = new Matrix(3, 1);
  m.set(0, 0, Math.sqrt(x * x + y * y));
  m.set(1, 0, Math.toDegrees(Math.atan2(y,x)));
  m.set(2, 0, angular);
  return m;  
}

/**
 * Helper method to get some dynamic attributes from each motor
 * 
 * @param attribute
 * @return the mmatrix
 */
protected synchronized Matrix getAttribute(int attribute) {
  Matrix x = new Matrix(nWheels+dummyWheels, 1);
  master.startSynchronization();
  for (int i = 0; i < nWheels; i++) {
    switch (attribute) {
    case TACHOCOUNT:
      x.set(i, 0, motor[i].getTachoCount());
      break;
    case MAXSPEED:
      x.set(i, 0, motor[i].getMaxSpeed());
      break;
    case ROTATIONSPEED:
      x.set(i, 0, motor[i].getRotationSpeed());
      break;
    }
  }
  if (dummyWheels==1) x.set(nWheels, 0, 0);
  master.endSynchronization();
  return x;
}

/**
 * Gets the biggest value from a matrix
 * 
 * @param a
 */
protected double getMax(Matrix a) {
  double max = Double.NEGATIVE_INFINITY;
  for (int m = 0; m < a.getRowDimension(); m++) {
    for (int n = 0; n < a.getColumnDimension(); n++) {
      if (!Double.isNaN(a.get(m, n)))
        max = Math.max(Math.abs(a.get(m, n)), max);
    }
  }
  return max;
}

/**
 * Make a copy of the source matrix, each of its element being the absolute
 * value of the elements of the source matrix
 * 
 * @param in
 * @return the matrix
 */
protected Matrix copyAbsolute(Matrix in) {
  Matrix a = in.copy();
  for (int m = 0; m < a.getRowDimension(); m++) {
    for (int n = 0; n < a.getColumnDimension(); n++) {
      a.set(m, n, Math.abs(a.get(m, n)));
    }
  }
  return a;
}



}

/* 
 * Some remarks regarding the design of the WheeledChassis. 
 * These originate from a discussion between leJOS developers about the code of this class
 * 
 * The decision to use the same source for both differential and holonomic drive
 * systems into one source is deliberate and well thought over. Let me explain
 * starting with some facts. 1. Functionality both a holonomic and a
 * differential chassis should be the same. 2. Except that the differential
 * chassis has a constraint that the the holonomic chassis does not have. A
 * differential chassis can only go forward and backward and not sideways. 3.
 * This constraint means that you can leave the y-component out of the equation
 * when implementing a differential chassis. As the y-component must always be
 * zero.
 * 
 * When implementing the two chassis I considered three design options. The
 * first was to implement a holonomic chassis on top of (as an extension of) a
 * differential chassis. This is impossible when the differential implementation
 * ignores the y-component and thus "hard codes" the differential constraint.
 * The second option was to implement both chassis independently. This has a
 * serious drawback. You need to maintain two sources. The adverse effects of
 * this we see in the numerous pilots we now have. Implementing two chassis from
 * scratch (including the PoseProvider) would have taken a few hundred lines of
 * code extra. The third option was to implement the differential chassis on top
 * of the holonomic chassis by adding the constraint of the differential chassis
 * in some way. This was easy. I added a dummy (holonomic) wheel to the
 * differential chassis. This wheel is oriented sideways and cannot rotate. This
 * results in the constraint of y = 0, the differential constraint! With just
 * two lines of code I was able to turn a holonomic chassis into a differential
 * chassis. To understand the code it helps to always think in terms of a
 * holonomic chassis.
 * 
 * Technically it is the inclusion of an y-component that complicates the source
 * for a differential chassis. Matrix language is not to blame for that. Matrix
 * language does complicates the code but not for this reason. It does
 * complicate the code as it requires a different way of thinking. But there is
 * a big advantage to linear algebra. It enables you to write an unlimited
 * number of similar equations as a single equation or to implement this as a
 * single line of code. Linear algebra gives compact code where the logic isn't
 * diluted with endless numbers of for-loops. The advantages of linear algebra
 * only become apparent when the number of equations (wheels) is more then two.
 * So for a differential chassis you can do without and I would have done so if
 * I hadn't implemented it as an extension of the holonomic chassis. But for a
 * holonomic chassis the advantages are apparent. However now that it is used
 * you no longer are constraint to two wheels only, you can have as much wheels
 * on your robot as you like.
 * 
 * Then there is the argument of efficiency. Not ignoring the y-component and
 * using linear algebra make the code for a differential chassis less efficient.
 * This is true. But as long as this is not a bottleneck I see no reason to
 * value this argument heavily. It is outweighed by the argument of improved
 * maintain ability. I have not jet heard of the performance of the
 * WheeledChassis being a bottleneck but only practice will tell.
 */
