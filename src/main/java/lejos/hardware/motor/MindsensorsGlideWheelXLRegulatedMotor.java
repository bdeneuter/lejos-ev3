package lejos.hardware.motor;

import lejos.hardware.port.Port;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.sensor.EV3SensorConstants;

/**
 * Abstraction for a Mindsensors Glidewheel-M equipped PF motor.<br>
 * Note: These settings are for the "XL" motor. 
 * TODO: Find some way to make this work with an I term for hold
 * TODO: Can probably be tuned better then this.
 * 
 */
public class MindsensorsGlideWheelXLRegulatedMotor extends BaseRegulatedMotor
{
    static final float MOVE_P = 3.5f;
    static final float MOVE_I = 0.01f;
    static final float MOVE_D = 3f;
    static final float HOLD_P = 2.5f;
    static final float HOLD_I = 0f;
    static final float HOLD_D = 0f;
    static final int OFFSET = 0;
    
    private static final int MAX_SPEED = 220*360/60;

    /**
     * Use this constructor to assign a variable of type motor connected to a particular port.
     * @param port  to which this motor is connected
     */
    public MindsensorsGlideWheelXLRegulatedMotor(TachoMotorPort port)
    {
        super(port, null, EV3SensorConstants.TYPE_MINITACHO, MOVE_P, MOVE_I, MOVE_D,
                HOLD_P, HOLD_I, HOLD_D, OFFSET, MAX_SPEED);
    }
    
    /**
     * Use this constructor to assign a variable of type motor connected to a particular port.
     * @param port  to which this motor is connected
     */
    public MindsensorsGlideWheelXLRegulatedMotor(Port port)
    {
        super(port, null, EV3SensorConstants.TYPE_NEWTACHO, MOVE_P, MOVE_I, MOVE_D,
                HOLD_P, HOLD_I, HOLD_D, OFFSET, MAX_SPEED);
    }


}
