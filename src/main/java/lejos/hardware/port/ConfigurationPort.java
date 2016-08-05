package lejos.hardware.port;

import lejos.hardware.sensor.EV3SensorConstants;

public interface ConfigurationPort extends IOPort, EV3SensorConstants
{
    /**
     * Get the type classification for the port. If a sensor is attached to the port
     * this will identify the connection type (UART/IIC/Dumb/Output etc.)
     * @return The type of the port. 
     */
    public int getPortType();
    
    /**
     * This function returns information on the sensor/motor that is attached to the
     * specified port. Note that only very basic sensor identification information
     * may be available for some sensor types. It may be necessary to actually open the
     * sensor to allow it to be identified in further detail.
     * @return the sensor type
     */
    public int getDeviceType();
}
