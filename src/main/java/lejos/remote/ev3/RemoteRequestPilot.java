package lejos.remote.ev3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;

public class RemoteRequestPilot implements ArcRotateMoveController {
	private ObjectInputStream is;
	private ObjectOutputStream os;

	public RemoteRequestPilot(ObjectInputStream is, ObjectOutputStream os, String leftMotor, String rightMotor, double wheelDiameter, double trackWidth) {
		this.is = is;
		this.os = os;
		
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.CREATE_REGULATED_MOTOR;
		req.str = leftMotor;
		req.ch = 'L';
		sendRequest(req, false);
		
		req = new EV3Request();
		req.request = EV3Request.Request.CREATE_REGULATED_MOTOR;
		req.str = rightMotor;
		req.ch = 'L';
		sendRequest(req, false);
		
		req = new EV3Request();
		req.request = EV3Request.Request.CREATE_PILOT;
		req.doubleValue = wheelDiameter;
		req.doubleValue2 = trackWidth;
		req.str = leftMotor;
		req.str2 = rightMotor;
		sendRequest(req, true);
	}

	@Override
	public double getMinRadius() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_GET_MIN_RADIUS;
		return sendRequest(req, true).doubleReply;
	}

	@Override
	public void setMinRadius(double radius) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_SET_MIN_RADIUS;
		req.doubleValue = radius;
		sendRequest(req, false);
	}

	@Override
	public void arcForward(double radius) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_ARC_FORWARD;
		req.doubleValue = radius;
		sendRequest(req, false);
	}

	@Override
	public void arcBackward(double radius) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_ARC_BACKWARD;
		req.doubleValue = radius;
		sendRequest(req, false);
	}


	@Override
	public void arc(double radius, double angle, boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_ARC_IMMEDIATE;
		req.doubleValue = radius;
		req.doubleValue2 = angle;
		req.flag = immediateReturn;
		sendRequest(req, !immediateReturn);	
	}



	@Override
	public void travelArc(double radius, double distance,
			boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_TRAVEL_ARC_IMMEDIATE;
		req.doubleValue = radius;
		req.doubleValue2 = distance;
		req.flag = immediateReturn;
		sendRequest(req, !immediateReturn);
	}

	@Override
	public void forward() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_FORWARD;
		sendRequest(req, false);
	}

	@Override
	public void backward() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_BACKWARD;
		sendRequest(req, false);
	}

	@Override
	public void stop() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_STOP;
		sendRequest(req, false);
	}

	@Override
	public boolean isMoving() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_IS_MOVING;
		return sendRequest(req, true).result;
	}

	@Override
	public void travel(double distance, boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_TRAVEL_IMMEDIATE;
		req.doubleValue = distance;
		req.flag = immediateReturn;
		sendRequest(req, !immediateReturn);
	}

	@Override
	public void setLinearSpeed(double speed) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_SET_LINEAR_SPEED;
		req.doubleValue = speed;
		sendRequest(req, false);
	}

	@Override
	public double getLinearSpeed() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_GET_LINEAR_SPEED;
		return sendRequest(req, true).doubleReply;
	}

	@Override
	public double getMaxLinearSpeed() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_GET_MAX_LINEAR_SPEED;
		return sendRequest(req, true).doubleReply;
	}

	@Override
	public Move getMovement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void rotate(double angle, boolean immediateReturn) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_ROTATE_IMMEDIATE;
		req.doubleValue = angle;
		req.flag = immediateReturn;
		sendRequest(req, !immediateReturn);
	}
	
	public void steer(double turnRate) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_STEER;
		req.doubleValue = turnRate;
		sendRequest(req, false);
	}

	@Override
	public void setAngularSpeed(double speed) {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_SET_ANGULAR_SPEED;
		req.doubleValue = speed;
		sendRequest(req, false);
	}

	@Override
	public double getAngularSpeed() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_GET_ANGULAR_SPEED;
		return sendRequest(req, true).doubleReply;
	}

	@Override
	public double getMaxAngularSpeed() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.PILOT_GET_MAX_ANGULAR_SPEED;
		return sendRequest(req, true).doubleReply;
	}
	
	public void close() {
		EV3Request req = new EV3Request();
		req.request = EV3Request.Request.CLOSE_PILOT;
		sendRequest(req, true);
	}
	
	private EV3Reply sendRequest(EV3Request req, boolean replyRequired) {
		EV3Reply reply = null;
		req.replyRequired = replyRequired;
		try {
			os.reset();
			os.writeObject(req);
			if (replyRequired) {
				reply = (EV3Reply) is.readObject();
				if (reply.e != null) throw new RemoteRequestException(reply.e);
			}
			return reply;
		} catch (Exception e) {
			throw new RemoteRequestException(e);
		}
	}

	@Override
	public void arc(double radius, double angle) {
		arc(radius, angle, false);
		
	}

	@Override
	public void travel(double distance) {
		travel(distance,false);
		
	}

	@Override
	public void travelArc(double radius, double distance) {
		travelArc(radius,distance,false);
		
	}

	@Override
	public void rotate(double angle) {
		rotate(angle,false);
		
	}

  @Override
  public void setLinearAcceleration(double acceleration) {
    EV3Request req = new EV3Request();
    req.request = EV3Request.Request.PILOT_SET_LINEAR_ACCELERATION;
    req.doubleValue = acceleration;
    sendRequest(req, false);
  }

  @Override
  public double getLinearAcceleration() {
    EV3Request req = new EV3Request();
    req.request = EV3Request.Request.PILOT_GET_LINEAR_ACCELERATION;
    return sendRequest(req, true).doubleReply;
  }

  @Override
  public void rotateRight() {
    rotate(Double.NEGATIVE_INFINITY, true);
  }

  @Override
  public void rotateLeft() {
    rotate(Double.POSITIVE_INFINITY, true);
  }

  @Override
  public void setAngularAcceleration(double acceleration) {
    EV3Request req = new EV3Request();
    req.request = EV3Request.Request.PILOT_SET_ANGULAR_ACCELERATION;
    req.doubleValue = acceleration;
    sendRequest(req, false);
  }

  @Override
  public double getAngularAcceleration() {
    EV3Request req = new EV3Request();
    req.request = EV3Request.Request.PILOT_GET_ANGULAR_ACCELERATION;
    return sendRequest(req, true).doubleReply;
  }

}
