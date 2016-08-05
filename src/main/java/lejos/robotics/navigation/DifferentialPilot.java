package lejos.robotics.navigation;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

import java.util.ArrayList;

/**
 * The DifferentialPilot class is a software abstraction of the Pilot mechanism
 * of a NXT robot. It contains methods to control robot movements: travel
 * forward or backward in a straight line or a circular path or rotate to a new
 * direction.<br>
 * This class will only work with two independently controlled motors to steer
 * differentially, so it can rotate within its own footprint (i.e. turn on one
 * spot). It registers as a {@link lejos.robotics.RegulatedMotorListener} with
 * each of its motors. An object of this class assumes that it has exclusive
 * control of its motors. If any other object makes calls to its motors, the
 * results are unpredictable. <br>
 * This class can be used with robots that have reversed motor design: the robot
 * moves in the direction opposite to the the direction of motor rotation. .<br>
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
 * units used in specifying the wheel diameter and track width in the
 * constructor. <br>
 * In all the methods that cause the robot to change its heading (the angle
 * relative to the X axis in which the robot is facing) the angle parameter
 * specifies the change in heading. A positive angle causes a turn to the left
 * (anti-clockwise) to increase the heading, and a negative angle causes a turn
 * to the right (clockwise). <br>
 * Example of use of come common methods:
 * <p>
 * <code><pre>
 * DifferentialPilot pilot = new DifferentialPilot(2.1f, 4.4f, Motor.A, Motor.C, true);  // parameters in inches
 * pilot.setRobotSpeed(30);  // cm per second
 * pilot.travel(50);         // cm
 * pilot.rotate(-90);        // degree clockwise
 * pilot.travel(-50,true);  //  move backward for 50 cm
 * while(pilot.isMoving())Thread.yield();
 * pilot.rotate(-90);
 * pilot.rotateTo(270);
 * pilot.steer(-50,180,true); // turn 180 degrees to the right
 * waitComplete();            // returns when previous method is complete
 * pilot.steer(100);          // turns with left wheel stationary
 * Delay.msDelay(1000;
 * pilot.stop();
 * </pre></code>
 * </p>
 * <p>
 * Note: A DifferentialPilot robot can simulate a SteeringPilot robot by calling
 * DifferentialPilot.setMinRadius() and setting the value to something greater
 * than zero (perhaps 15 cm).
 * </p>
 * @deprecated use {@link MovePilot} instead. 
 **/
@Deprecated 
public class DifferentialPilot implements  LineFollowingMoveController {
	/**
	 * Allocates a DifferentialPilot object, and sets the physical parameters of
	 * the NXT robot.<br>
	 * Assumes Motor.forward() causes the robot to move forward.
	 * 
	 * @param wheelDiameter
	 *            Diameter of the tire, in any convenient units (diameter in mm
	 *            is usually printed on the tire).
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 */
	public DifferentialPilot(final double wheelDiameter,
			final double trackWidth, final RegulatedMotor leftMotor,
			final RegulatedMotor rightMotor) {
		this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}

	/**
	 * Allocates a DifferentialPilot object, and sets the physical parameters of
	 * the NXT robot.<br>
	 * 
	 * @param wheelDiameter
	 *            Diameter of the tire, in any convenient units (diameter in mm
	 *            is usually printed on the tire).
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 * @param reverse
	 *            If true, the NXT robot moves forward when the motors are
	 *            running backward.
	 */
	public DifferentialPilot(final double wheelDiameter,
			final double trackWidth, final RegulatedMotor leftMotor,
			final RegulatedMotor rightMotor, final boolean reverse) {
		this(wheelDiameter, wheelDiameter, trackWidth, leftMotor, rightMotor,
				reverse);
	}

	/**
	 * Allocates a DifferentialPilot object, and sets the physical parameters of
	 * the NXT robot.<br>
	 * 
	 * @param leftWheelDiameter
	 *            Diameter of the left wheel, in any convenient units (diameter
	 *            in mm is usually printed on the tire).
	 * @param rightWheelDiameter
	 *            Diameter of the right wheel. You can actually fit
	 *            intentionally wheels with different size to your robot. If you
	 *            fitted wheels with the same size, but your robot is not going
	 *            straight, try swapping the wheels and see if it deviates into
	 *            the other direction. That would indicate a small difference in
	 *            wheel size. Adjust wheel size accordingly. The minimum change
	 *            in wheel size which will actually have an effect is given by
	 *            minChange = A*wheelDiameter*wheelDiameter/(1-(A*wheelDiameter)
	 *            where A = PI/(moveSpeed*360). Thus for a moveSpeed of 25
	 *            cm/second and a wheelDiameter of 5,5 cm the minChange is about
	 *            0,01058 cm. The reason for this is, that different while sizes
	 *            will result in different motor speed. And that is given as an
	 *            integer in degree per second.
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 * @param reverse
	 *            If true, the NXT robot moves forward when the motors are
	 *            running backward.
	 */
	public DifferentialPilot(final double leftWheelDiameter,
			final double rightWheelDiameter, final double trackWidth,
			final RegulatedMotor leftMotor, final RegulatedMotor rightMotor,
			final boolean reverse) {
		_left = leftMotor;
		_leftWheelDiameter = (float) leftWheelDiameter;
		_leftTurnRatio = (float) (trackWidth / leftWheelDiameter);
		_leftDegPerDistance = (float) (360 / (Math.PI * leftWheelDiameter));
		// right
		_right = rightMotor;
		_rightWheelDiameter = (float) rightWheelDiameter;
		_rightTurnRatio = (float) (trackWidth / rightWheelDiameter);
		_rightDegPerDistance = (float) (360 / (Math.PI * rightWheelDiameter));
		// both
		_trackWidth = (float) trackWidth;
		_parity = (byte) (reverse ? -1 : 1);
		setLinearSpeed(.8f * getMaxLinearSpeed());
		setAngularSpeed(.8f * getMaxRotateSpeed());
		setLinearAcceleration((int) (_robotTravelSpeed * 4));
		_monitor = new Monitor();
		_monitor.start();
		_left.synchronizeWith(new RegulatedMotor[]{_right});
	}

	/**
	 * Returns the tachoCount of the left motor
	 * 
	 * @return tachoCount of left motor. Positive value means motor has moved
	 *         the robot forward.
	 */
	private int getLeftCount() {
		return _parity * _left.getTachoCount();
	}

	/**
	 * Returns the tachoCount of the right motor
	 * 
	 * @return tachoCount of the right motor. Positive value means motor has
	 *         moved the robot forward.
	 */
	private int getRightCount() {
		return _parity * _right.getTachoCount();
	}

	/*
	 * public int getRightActualSpeed() { return _right.getRotationSpeed(); }
	 */
	private void setSpeed(final int leftSpeed, final int rightSpeed) {
	    _left.startSynchronization();
		_left.setSpeed(leftSpeed);
		_right.setSpeed(rightSpeed);
		_left.endSynchronization();
	}

	/**
	 * set travel speed in wheel diameter units per second
	 * 
	 * @param travelSpeed
	 *            : speed in distance (wheel diameter)units/sec
	 */
	public void setLinearSpeed(final double travelSpeed) {
		if (!isMoving()) {
			_robotTravelSpeed = (float) travelSpeed;
			setSpeed((int) Math.round(travelSpeed * _leftDegPerDistance),
					(int) Math.round(travelSpeed * _rightDegPerDistance));
		} else {
			float speedRatio = (float) travelSpeed / _robotTravelSpeed;
			_left.startSynchronization();
			_left.setSpeed((int) Math.round(_left.getSpeed() * speedRatio));
			_right.setSpeed((int) Math.round(_right.getSpeed() * speedRatio));
			_left.endSynchronization();
			_robotTravelSpeed = (float) travelSpeed;
		}
	}

	public double getLinearSpeed() {
		return _robotTravelSpeed;
	}

	@Override
	public void setLinearAcceleration(double acceleration) {
		_acceleration = (int)acceleration;
		setMotorAccel(_acceleration);
	}
	
  @Override
	public double getLinearAcceleration() {
	  return _acceleration;
	}

	/**
	 * helper method for setAcceleration and quickStop
	 * 
	 * @param acceleration
	 */
	private void setMotorAccel(int acceleration) {
	    _left.startSynchronization();
		_left.setAcceleration((int) (acceleration * _leftDegPerDistance));
		_right.setAcceleration((int) (acceleration * _rightDegPerDistance));
		_left.endSynchronization();
	}

	public double getMaxLinearSpeed() {
		return Math.min(_left.getMaxSpeed(), _right.getMaxSpeed())
				/ Math.max(_leftDegPerDistance, _rightDegPerDistance);
		// max degree/second divided by degree/unit = unit/second
	}

	/**
	 * sets the rotation speed of the vehicle, degrees per second
	 * 
	 * @param rotateSpeed
	 */
	public void setAngularSpeed(double rotateSpeed) {
		_robotRotateSpeed = (float) rotateSpeed;
		setSpeed((int) Math.round(rotateSpeed * _leftTurnRatio),
				(int) Math.round(rotateSpeed * _rightTurnRatio));
	}

	public double getAngularSpeed() {
		return _robotRotateSpeed;
	}

	public float getMaxRotateSpeed() {
		return Math.min(_left.getMaxSpeed(), _right.getMaxSpeed())
				/ Math.max(_leftTurnRatio, _rightTurnRatio);
		// max degree/second divided by degree/unit = unit/second
	}

	public double getMaxAngularSpeed() {
		return getMaxRotateSpeed();
	}

	/**
	 * Starts the NXT robot moving forward.
	 */
	public void forward() {
		waitForActiveMove();
		_type = Move.MoveType.TRAVEL;
		_angle = 0;
		_distance = Double.POSITIVE_INFINITY;
		_leftDirection = 1;
		_rightDirection = 1;
		movementStart();
		setSpeed(Math.round(_robotTravelSpeed * _leftDegPerDistance),
				Math.round(_robotTravelSpeed * _rightDegPerDistance));
		if (_parity == 1) {
			fwd();
		} else {
			bak();
		}
	}

	/**
	 * Starts the NXT robot moving backward.
	 */
	public void backward() {
		waitForActiveMove();
		_type = Move.MoveType.TRAVEL;
		_distance = Double.NEGATIVE_INFINITY;
		_angle = 0;
		_leftDirection = -1;
		_rightDirection = -1;
		movementStart();
		setSpeed(Math.round(_robotTravelSpeed * _leftDegPerDistance),
				Math.round(_robotTravelSpeed * _rightDegPerDistance));
		if (_parity == 1) {
			bak();
		} else {
			fwd();
		}
	}

	/**
	 * Motors backward. This is called by forward() and backward(), depending on
	 * parity.
	 */
	private void bak() {
	    _left.startSynchronization();
		_left.backward();
		_right.backward();
		_left.endSynchronization();
		movementActive();
	}

	/**
	 * Motors forward. This is called by forward() and backward() depending on
	 * parity.
	 */
	private void fwd() {
	    _left.startSynchronization();
		_left.forward();
		_right.forward();
		_left.endSynchronization();
		movementActive();
	}

	public void rotateLeft() {
		waitForActiveMove();
		_type = Move.MoveType.ROTATE;
		_distance = 0;
		_angle = Double.POSITIVE_INFINITY;
		movementStart();
		_left.startSynchronization();
		setMotorAccel(_acceleration);
		if (_parity > 0) {
			_right.forward();
			_left.backward();
		} else {
			_left.forward();
			_right.backward();
		}
		_left.endSynchronization();
		movementActive();
	}

	public void rotateRight() {
		waitForActiveMove();
		_type = Move.MoveType.ROTATE;
		_distance = 0;
		_angle = Double.NEGATIVE_INFINITY;
		movementStart();
		_left.startSynchronization();
		setMotorAccel(_acceleration);
		if (_parity > 0) {
			_left.forward();
			_right.backward();
		} else {
			_right.forward();
			_left.backward();
		}
		_left.endSynchronization();
	}

	
	/**
	 * Rotates the NXT robot through a specific angle. Returns when angle is
	 * reached. Wheels turn in opposite directions producing a zero radius turn.<br>
	 * Note: Requires correct values for wheel diameter and track width. calls
	 * rotate(angle,false)
	 * 
	 * @param angle
	 *            The wanted angle of rotation in degrees. Positive angle rotate
	 *            left (anti-clockwise), negative right.
	 */
	public void rotate(final double angle) {
		rotate(angle, false);
	}

	/**
	 * Rotates the NXT robot through a specific angle. Returns when angle is
	 * reached. Wheels turn in opposite directions producing a zero radius turn.<br>
	 * Note: Requires correct values for wheel diameter and track width. Side
	 * effect: inform listeners
	 * 
	 * @param angle
	 *            The wanted angle of rotation in degrees. Positive angle rotate
	 *            left (anti-clockwise), negative right.
	 * @param immediateReturn
	 *            If true this method returns immediately.
	 */
	public void rotate(final double angle, final boolean immediateReturn) {
		waitForActiveMove();
		_type = Move.MoveType.ROTATE;
		_distance = 0;
		_angle = angle;
		int rotateAngleLeft = _parity * (int) (angle * _leftTurnRatio);
		int rotateAngleRight = _parity * (int) (angle * _rightTurnRatio);
		movementStart();
		_left.startSynchronization();
		setMotorAccel(_acceleration);
		setSpeed(Math.round(_robotRotateSpeed * _leftTurnRatio),
				Math.round(_robotRotateSpeed * _rightTurnRatio));
		_left.rotate(-rotateAngleLeft, true);
		_leftDirection = (byte) Math.signum(-rotateAngleLeft);
		_right.rotate(rotateAngleRight, true);
		_rightDirection = (byte) Math.signum(rotateAngleRight);
		_left.endSynchronization();
		movementActive();
		if (!immediateReturn)
		    waitComplete();
	}

	/**
	 * Stops the NXT robot. side effect: inform listeners of end of movement
	 */
	public void stop() {
		
//		System.out.println("stop called");
	    _left.startSynchronization();
		_left.stop(true);
		_right.stop(true);
		_left.endSynchronization();
		waitComplete();
		synchronized(_monitor)
		{
		    _monitor.notifyAll();
		}		
		setMotorAccel(_acceleration);

	}

	/**
	 * Stops the robot almost immediately. Use this method if the normal
	 * {@link #stop()} is too slow;
	 */
	public void quickStop() {
		setMotorAccel(_quickAcceleration);
		stop();
		setMotorAccel(_acceleration);
	}

	/**
	 * Moves the NXT robot a specific distance in an (hopefully) straight line.<br>
	 * A positive distance causes forward motion, a negative distance moves
	 * backward. If a drift correction has been specified in the constructor it
	 * will be applied to the left motor. calls travel(distance, false)
	 * 
	 * @param distance
	 *            The distance to move. Unit of measure for distance must be
	 *            same as wheelDiameter and trackWidth.
	 **/
	public void travel(final double distance) {
		travel(distance, false);
	}


	/**
	 * Moves the NXT robot a specific distance in an (hopefully) straight line.<br>
	 * A positive distance causes forward motion, a negative distance moves
	 * backward. If a drift correction has been specified in the constructor it
	 * will be applied to the left motor.
	 * 
	 * @param distance
	 *            The distance to move. Unit of measure for distance must be
	 *            same as wheelDiameter and trackWidth.
	 * @param immediateReturn
	 *            If true this method returns immediately.
	 */
	public void travel(final double distance, final boolean immediateReturn) {
		waitForActiveMove();
		_type = Move.MoveType.TRAVEL;
		_distance = distance;
		_angle = 0;
		movementStart();
		setMotorAccel(_acceleration);
		_leftDirection = 1;
		_rightDirection = 1;
		if (distance < 0) {
			_leftDirection = -1;
			_rightDirection = -1;
		}

		if (distance == Double.POSITIVE_INFINITY) {
			forward();
			return;
		}
		if ((distance == Double.NEGATIVE_INFINITY)) {
			backward();
			return;
		}
		setSpeed(Math.round(_robotTravelSpeed * _leftDegPerDistance),
				Math.round(_robotTravelSpeed * _rightDegPerDistance));
		_left.startSynchronization();
		_left.rotate((int) (_parity * distance * _leftDegPerDistance), true);
		_right.rotate((int) (_parity * distance * _rightDegPerDistance), true);
        _left.endSynchronization();
        movementActive();
		if (!immediateReturn)
			waitComplete();
	}

	public void arcForward(final double radius) {
		waitForActiveMove();
		_type = Move.MoveType.ARC;
		if (radius > 0) {
			_angle = Double.POSITIVE_INFINITY;
			_distance = Double.POSITIVE_INFINITY;
		} else {
			_angle = Double.NEGATIVE_INFINITY;
			_distance = Double.NEGATIVE_INFINITY;
		}
		movementStart();
		double turnRate = turnRate(radius);
		steerPrep(turnRate); // sets motor speeds
		_left.startSynchronization();
		if (_parity > 0)
			_outside.forward();
		else
			_outside.backward();
		if (_parity * _steerRatio > 0)
			_inside.forward();
		else
			_inside.backward();
		_left.endSynchronization();
		movementActive();
	}

	public void arcBackward(final double radius) {
		waitForActiveMove();
		_type = Move.MoveType.ARC;
		if (radius < 0) {
			_angle = Double.POSITIVE_INFINITY;
			_distance = Double.NEGATIVE_INFINITY;
		} else {
			_angle = Double.NEGATIVE_INFINITY;
			_distance = Double.POSITIVE_INFINITY;
		}
		movementStart();
		double turnRate = turnRate(radius);
		steerPrep(turnRate);// sets motor speeds
		_left.startSynchronization();
		if (_parity > 0)
			_outside.backward();
		else
			_outside.forward();
		if (_parity * _steerRatio > 0)
			_inside.backward();
		else
			_inside.forward();
		_left.endSynchronization();
		movementActive();
	}

	public void arc(final double radius, final double angle) {
		arc(radius, angle, false);
	}

	public void arc(final double radius, final double angle,
			final boolean immediateReturn) {
		if (radius == Double.POSITIVE_INFINITY
				|| radius == Double.NEGATIVE_INFINITY) {
			forward();
			return;
		}
		steer(turnRate(radius), angle, immediateReturn);// type and move started
														// called by steer()
	}

	public void travelArc(double radius, double distance) {
		travelArc(radius, distance, false);
	}

	public void travelArc(double radius, double distance,
			boolean immediateReturn) {
		waitForActiveMove();
		if (radius == Double.POSITIVE_INFINITY
				|| radius == Double.NEGATIVE_INFINITY) {
			travel(distance, immediateReturn);
			return;
		}
		if (radius == 0) {
			throw new IllegalArgumentException("Zero arc radius");
		}
		double angle = (distance * 180) / ((float) Math.PI * radius);
		arc(radius, angle, immediateReturn);
	}

	/**
	 * Calculates the turn rate corresponding to the turn radius; <br>
	 * use as the parameter for steer() negative argument means center of turn
	 * is on right, so angle of turn is negative
	 * 
	 * @param radius
	 * @return turnRate to be used in steer()
	 */
	private double turnRate(final double radius) {
		int direction;
		double radiusToUse;
		if (radius < 0) {
			direction = -1;
			radiusToUse = -radius;
		} else {
			direction = 1;
			radiusToUse = radius;
		}
		double ratio = (2 * radiusToUse - _trackWidth)
				/ (2 * radiusToUse + _trackWidth);
		return (direction * 100 * (1 - ratio));
	}

	/**
	 * Returns the radius of the turn made by steer(turnRate) Used in for
	 * planned distance at start of arc and steer moves.
	 * 
	 * @param turnRate
	 * @return radius of the turn.
	 */
	private double radius(double turnRate) {
		double radius = 100 * _trackWidth / turnRate;
		if (turnRate > 0)
			radius -= _trackWidth / 2;
		else
			radius += _trackWidth / 2;
		return radius;
	}

	/**
	 * Starts the robot moving forward along a curved path. This method is
	 * similar to the {@link #arcForward(double radius )} method except it uses
	 * the <code> turnRate</code> parameter do determine the curvature of the
	 * path and therefore has the ability to drive straight. This makes it
	 * useful for line following applications.
	 * <p>
	 * The <code>turnRate</code> specifies the sharpness of the turn. Use values
	 * between -200 and +200.<br>
	 * A positive value means that center of the turn is on the left. If the
	 * robot is traveling toward the top of the page the arc looks like this:
	 * <b>)</b>. <br>
	 * A negative value means that center of the turn is on the right so the arc
	 * looks this: <b>(</b>. <br>
	 * . In this class, this parameter determines the ratio of inner wheel speed
	 * to outer wheel speed <b>as a percent</b>.<br>
	 * <I>Formula:</I> <code>ratio = 100 - abs(turnRate)</code>.<br>
	 * When the ratio is negative, the outer and inner wheels rotate in opposite
	 * directions. Examples of how the formula works:
	 * <UL>
	 * <LI><code>steer(0)</code> -> inner and outer wheels turn at the same
	 * speed, travel straight
	 * <LI><code>steer(25)</code> -> the inner wheel turns at 75% of the speed
	 * of the outer wheel, turn left
	 * <LI><code>steer(100)</code> -> the inner wheel stops and the outer wheel
	 * is at 100 percent, turn left
	 * <LI><code>steer(200)</code> -> the inner wheel turns at the same speed as
	 * the outer wheel - a zero radius turn.
	 * </UL>
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will
	 * not be applied in this method.
	 * 
	 * @param turnRate
	 *            If positive, the left side of the robot is on the inside of
	 *            the turn. If negative, the left side is on the outside.
	 */
	public void steer(double turnRate) {
		waitForActiveMove();
		_type = Move.MoveType.ARC;
		if (turnRate > 0) {
			_angle = Double.POSITIVE_INFINITY;
			_distance = Double.POSITIVE_INFINITY;
		} else {
			_angle = Double.NEGATIVE_INFINITY;
			_distance = Double.NEGATIVE_INFINITY;
		}
		steerPrep(turnRate);
		if (turnRate == 0) {
			forward();
			return;
		}
		movementStart();
		_left.startSynchronization();
		if (_parity > 0)
			_outside.forward();
		else
			_outside.backward();
		if (_parity * _steerRatio > 0)
			_inside.forward();
		else
			_inside.backward();
		_left.endSynchronization();
		movementActive();
	}

	/**
	 * Starts the robot moving backward along a curved path. This method is
	 * essentially the same as {@link #steer(double)} except that the robot
	 * moves backward instead of forward.
	 * 
	 * @param turnRate
	 */
	public void steerBackward(final double turnRate) {
		waitForActiveMove();
		if (turnRate == 0) {
			if (_parity < 0)
				forward();
			else
				backward();
			return;
		}
		steerPrep(turnRate);
		movementStart();
		_left.startSynchronization();
		if (_parity > 0)
			_outside.backward();
		else
			_outside.forward();
		_type = Move.MoveType.ARC;
		if (turnRate < 0) {
			_angle = Double.POSITIVE_INFINITY;
			_distance = Double.NEGATIVE_INFINITY;
		} else {
			_angle = Double.NEGATIVE_INFINITY;
			_distance = Double.POSITIVE_INFINITY;
		}
		if (_parity * _steerRatio > 0)
			_inside.backward();
		else
			_inside.forward();
		_left.endSynchronization();
		movementActive();
	}

	/**
	 * Moves the robot along a curved path through a specified turn angle. This
	 * method is similar to the {@link #arc(double radius , double angle)}
	 * method except it uses a ratio of motor speeds to determine the curvature
	 * of the path and therefore has the ability to drive straight. This makes
	 * it useful for line following applications. This method does not return
	 * until the robot has completed moving <code>angle</code> degrees along the
	 * arc.<br>
	 * The <code>turnRate</code> specifies the sharpness of the turn. Use values
	 * between -200 and +200.<br>
	 * For details about how this parameter works.See
	 * {@link #steer(double turnRate) }
	 * <p>
	 * The robot will stop when its heading has changed by the amount of the
	 * <code>angle</code> parameter.<br>
	 * If <code>angle</code> is positive, the robot will move in the direction
	 * that increases its heading (it turns left).<br>
	 * If <code>angle</code> is negative, the robot will move in the directin
	 * that decreases its heading (turns right).<br>
	 * If <code>angle</code> is zero, the robot will not move and the method
	 * returns immediately.
	 * <p>
	 * The sign of the turn rate and the sign of the angle together determine if
	 * the robot will move forward or backward. Assuming the robot is heading
	 * toward the top of the page. Then a positive turn rate means the arc looks
	 * like this: <b> )</b> . If the angle is positive, the robot moves forward
	 * to increase its heading angle. If negative, it moves backward to decrease
	 * the heading. <br>
	 * But if the turn rate is negative, the arc looks like this: <b> ( </b> .So
	 * a positive angle (increase in heading) means the robot moves backwards,
	 * while a negative angle means the robot moves forward to decrease its
	 * heading.
	 * 
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will
	 * not be applied in this method.
	 * 
	 * @param turnRate
	 *            If positive, the left side of the robot is on the inside of
	 *            the turn. If negative, the left side is on the outside.
	 * @param angle
	 *            The angle through which the robot will rotate. If negative,
	 *            the robot will move in the directin that decreases its
	 *            heading.
	 */
	public void steer(final double turnRate, double angle) {
		steer(turnRate, angle, false);
	}

	/**
	 * Moves the robot along a curved path for a specified angle of rotation.
	 * This method is similar to the
	 * {@link #arc(double radius, double angle, boolean immediateReturn)} method
	 * except it uses the <code> turnRate()</code> parameter to determine the
	 * curvature of the path and therefore has the ability to drive straight.
	 * This makes it useful for line following applications. This method has the
	 * ability to return immediately by using the <code>immediateReturn</code>
	 * parameter set to <b>true</b>.
	 * 
	 * <p>
	 * The <code>turnRate</code> specifies the sharpness of the turn. Use values
	 * between -200 and +200.<br>
	 * For details about how this parameter works, see
	 * {@link #steer(double turnRate) }
	 * <p>
	 * The robot will stop when its heading has changed by the amount of the
	 * <code>angle</code> parameter.<br>
	 * If <code>angle</code> is positive, the robot will move in the direction
	 * that increases its heading (it turns left).<br>
	 * If <code>angle</code> is negative, the robot will move in the direction
	 * that decreases its heading (turns right).<br>
	 * If <code>angle</code> is zero, the robot will not move and the method
	 * returns immediately.<br>
	 * For more details about this parameter, see
	 * {@link #steer(double turnRate, double angle)}
	 * <p>
	 * Note: If you have specified a drift correction in the constructor it will
	 * not be applied in this method.
	 * 
	 * @param turnRate
	 *            If positive, the left side of the robot is on the inside of
	 *            the turn. If negative, the left side is on the outside.
	 * @param angle
	 *            The angle through which the robot will rotate. If negative,
	 *            robot traces the turning circle backwards.
	 * @param immediateReturn
	 *            If immediateReturn is true then the method returns
	 *            immediately.
	 */
	public void steer(final double turnRate, final double angle,
			final boolean immediateReturn) {
		waitForActiveMove();
		if (angle == 0) {
			return;
		}
		movementStart();
		if (turnRate == 0) {
			forward();
			return;
		}
		_type = Move.MoveType.ARC;
		_angle = angle;
		_distance = 2 * Math.toRadians(angle) * radius(turnRate);
		steerPrep(turnRate);
		int side = (int) Math.signum(turnRate);
		int rotAngle = (int) (angle * _trackWidth * 2 / (_leftWheelDiameter * (1 - _steerRatio)));
		_left.startSynchronization();
		_inside.rotate((int) (_parity * side * rotAngle * _steerRatio), true);
		_outside.rotate(_parity * side * rotAngle, immediateReturn);
		_left.endSynchronization();
		movementActive();
		setMotorAccel(_acceleration);
		if (immediateReturn) {
			return;
		}
		waitComplete();
		_inside.setSpeed(_outside.getSpeed());
	}

	/**
	 * helper method used by steer(float) and steer(float,float,boolean) sets
	 * _outsideSpeed, _insideSpeed, _steerRatio set motor acceleration to help
	 * continuous steer and arc moves
	 * 
	 * @param turnRate
	 *            .
	 */
	void steerPrep(final double turnRate) {
		double rate = turnRate;
		if (rate < -200)
			rate = -200;
		if (rate > 200)
			rate = 200;
		float insideDegPerDist = _leftDegPerDistance;
		float outsideDegPerDist = _rightDegPerDistance;
		byte insideDirection = _leftDirection;
		byte outsideDirection = _rightDirection;

		if (turnRate < 0) {
			_inside = _right;
			insideDegPerDist = _rightDegPerDistance;
			insideDirection = _rightDirection;
			_outside = _left;
			outsideDegPerDist = _leftDegPerDistance;
			outsideDirection = _leftDirection;
			rate = -rate;
		} else {
			_inside = _left;
			_outside = _right;
		}
		_leftDirection = 1;
		_rightDirection = 1;
		_steerRatio = (float) (1 - rate / 100.0);
		float outsideSpeed = _robotTravelSpeed * outsideDegPerDist;
		float insideSpeed = _robotTravelSpeed * insideDegPerDist * _steerRatio;
		float inSpeed0 = 0;
		if (_inside.isMoving())
			inSpeed0 = _inside.getSpeed() * insideDirection;
		float insideDV = Math.abs(insideSpeed - inSpeed0);
		float outSpeed0 = 0;
		if (_outside.isMoving())
			outSpeed0 = _outside.getSpeed() * outsideDirection;
		float outsideDV = Math.abs(outsideSpeed - outSpeed0);
		_outside.setSpeed((int) outsideSpeed);
		_inside.setSpeed((int) insideSpeed);
		if (insideDV < outsideDV) {
			float acc = _acceleration * outsideDegPerDist;
			_outside.setAcceleration((int) acc);
			float ratio = insideDV / outsideDV;
			if (ratio < .1)
				_inside.setAcceleration((int) acc);
			else
				_inside.setAcceleration((int) (acc * ratio));
		} else { // outsideDV < insideDV
			float acc = _acceleration * insideDegPerDist;
			_inside.setAcceleration((int) acc);
			float ratio = outsideDV / insideDV;
			if (ratio < .1)
				_outside.setAcceleration((int) acc);
			else
				_outside.setAcceleration((int) (acc * ratio));
		}
		if (_steerRatio < 0) {
			if (_inside == _left)
				_leftDirection = -1;
			else
				_rightDirection = -1;
		}
	}


	/**
	 * called at start of a movement to inform the listeners that a movement has
	 * started. Must be called before the motors start to move to capture the correct
	 * positional information
	 */
	protected void movementStart() {
		Move move = new Move(_type, getMovementIncrement(),
				getAngleIncrement(), _robotTravelSpeed, _robotRotateSpeed,
				isMoving());
		reset();
		for (MoveListener ml : _listeners)
			ml.moveStarted(move, this);
		reset();
	}

	/**
	 * Called to indicate that the motors are now running and should be monitored for movement. Must
	 * be called after the motors are started.
	 */
	protected void movementActive()
	{
        synchronized(_monitor)
        {
            _moveActive = true;
            _monitor.notifyAll();
        }
	}

	/**
	 * called by Arc() ,travel(),rotate(),stop() rotationStopped() calls
	 * moveStopped on listener
	 */
	private void movementStop() {
	    Move move;
    	move = new Move(_type, getMovementIncrement(),
    				getAngleIncrement(), _robotTravelSpeed, _robotRotateSpeed,
    				isMoving());
		for (MoveListener ml : _listeners)
			ml.moveStopped(move, this);
		synchronized(_monitor)
		{
		    _moveActive = false;
		    _monitor.notifyAll();
		}
	}

	/**
	 * @return true if the NXT robot is moving.
	 **/
	public boolean isMoving() {
		return _left.isMoving() || _right.isMoving();
	}

	/**
	 * wait for the current operation on both motors to complete
	 */
	private synchronized void waitComplete() {
		while (isMoving()) {
			_left.waitComplete();
			_right.waitComplete();
		}
	}

	private void waitForActiveMove() {
	    synchronized(_monitor)
	    {
	        if (_moveActive)
	        {
	            if (isMoving()) 
	                stop();
	            while (_moveActive)
                    try
                    {
                        _monitor.wait();
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
	        }
	    }
	}

	public boolean isStalled() {
		return _left.isStalled() || _right.isStalled();
	}

	/**
	 * Resets tacho count for both motors.
	 **/
	public void reset() {
		_leftTC = getLeftCount();
		_rightTC = getRightCount();
	}

	/**
	 * Set the radius of the minimum turning circle. Note: A DifferentialPilot
	 * robot can simulate a SteeringPilot robot by calling
	 * DifferentialPilot.setMinRadius() and setting the value to something
	 * greater than zero (example: 15 cm).
	 * 
	 * @param radius
	 *            in degrees
	 */
	public void setMinRadius(double radius) {
		_turnRadius = (float) radius;
	}

	public double getMinRadius() {
		return _turnRadius;
	}

	/**
	 * 
	 * @return The move distance since it last started moving
	 */
	public float getMovementIncrement() {
		float left = (getLeftCount() - _leftTC) / _leftDegPerDistance;
		float right = (getRightCount() - _rightTC) / _rightDegPerDistance;
		return /* _parity * */(left + right) / 2.0f;
	}

	/**
	 * 
	 * @return The angle rotated since rotation began.
	 * 
	 */
	public float getAngleIncrement() {
		return /* _parity * */(((getRightCount() - _rightTC) / _rightTurnRatio) - ((getLeftCount() - _leftTC) / _leftTurnRatio)) / 2.0f;
	}

	public void addMoveListener(MoveListener m) {
		_listeners.add(m);
	}

	public Move getMovement() {
		return new Move(_type, getMovementIncrement(), getAngleIncrement(),
				isMoving());
	}

	/**
	 * Get the turn rate for arc and steer commands
	 * 
	 * @return the turn rate
	 */
	public double getTurnRate() {
		return _robotRotateSpeed;
	}

	private class Monitor extends Thread {
		public boolean more = true;
		public Monitor()
		{
		    // don't make VM hang for us!
            setDaemon(true);		    
		}
		
		public synchronized void run() {
			while (more) {
				if (_moveActive)
				{
				    if (isStalled())
				        DifferentialPilot.this.stop();
				    if (!isMoving())
					    movementStop();
				}
				// wait for an event
				try
                {
                    wait(_moveActive ? 1 : 100);
                } catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			}
		}
	}

	private float _turnRadius = 0;
	/**
	 * Left motor..
	 */
	protected final RegulatedMotor _left;
	/**
	 * Right motor.
	 */
	protected final RegulatedMotor _right;
	/**
	 * The motor at the inside of the turn. set by steer(turnRate) used by other
	 * steer methodsl
	 */
	private RegulatedMotor _inside;
	/**
	 * The motor at the outside of the turn. set by steer(turnRate) used by
	 * other steer methodsl
	 */
	protected RegulatedMotor _outside;
	/**
	 * ratio of inside/outside motor speeds set by steer(turnRate) used by other
	 * steer methods;
	 */
	private float _steerRatio;
	/**
	 * Left motor degrees per unit of travel.
	 */
	protected final float _leftDegPerDistance;
	/**
	 * Right motor degrees per unit of travel.
	 */
	protected final float _rightDegPerDistance;
	/**
	 * Left motor revolutions for 360 degree rotation of robot (motors running
	 * in opposite directions). Calculated from wheel diameter and track width.
	 * Used by rotate() and steer() methods.
	 **/
	private final float _leftTurnRatio;
	/**
	 * Right motor revolutions for 360 degree rotation of robot (motors running
	 * in opposite directions). Calculated from wheel diameter and track width.
	 * Used by rotate() and steer() methods.
	 **/
	private final float _rightTurnRatio;
	/**
	 * Speed of robot for moving in wheel diameter units per seconds. Set by
	 * setSpeed(), setTravelSpeed()
	 */
	private float _robotTravelSpeed;
	/**
	 * Speed of robot for turning in degree per seconds.
	 */
	private float _robotRotateSpeed;
	/**
	 * Motor rotation forward makes robot move forward if parity == 1.
	 */
	private byte _parity;
	/**
	 * Distance between wheels. Used in steer() and rotate().
	 */
	private final float _trackWidth;
	/**
	 * Diameter of left wheel.
	 */
	private final float _leftWheelDiameter;
	/**
	 * Diameter of right wheel.
	 */
	private final float _rightWheelDiameter;
	private int _leftTC; // left tacho count
	private int _rightTC; // right tacho count
	private ArrayList<MoveListener> _listeners = new ArrayList<MoveListener>();
	/**
   */
	protected Move.MoveType _type;
	/**
	 * Distance about to travel - used by movementStarted
	 */
	private double _distance;
	/**
	 * Angle about to turn - used by movementStopped
	 */
	private double _angle;
	/**
	 * used by travel and rotate methods, and stop()
	 */
	private int _acceleration;
	private int _quickAcceleration = 9999; // used for quick stop.
	/**
	 * direction of rotation of left motor +1 or -1
	 */
	private byte _leftDirection = 1;
	/**
	 * direction of rotation of right motor +1 or -1
	 */
	private byte _rightDirection;

	/**
	 * The monitor thread
	 */
	private Monitor _monitor;
	/**
	 * set by rotatsionStopped() used by Monitor thread to call
	 * movementStopped()
	 */

	private boolean _moveActive;
  @Override
  public void setAngularAcceleration(double acceleration) {
    // TODO Pilot does not take angular acceleration into account. 
    // The method serves as a placeholder to satisfy the interface.
    
  }

  @Override
  public double getAngularAcceleration() {
    // TODO see setAngularAcceleration
    return 0;
  }

}