package lejos.robotics;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.LampController;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

/**
 * 
 * @author Roger Glassey
 *
 */
public class LightDetectorAdaptor {
	/**
	 *         allocates a LightSensorAdaptor ; sets mode to Ambient Accepts as
	 *         a parameter one of EV3ColorSensor, NXTColorSensor, or
	 *         NXTLightSensor
	 */
	public LightDetectorAdaptor(SampleProvider sensor) {
		this.sensor = sensor;

		if (sensor instanceof EV3ColorSensor) {
			ambientMode = ((EV3ColorSensor) sensor).getAmbientMode();
			reflectedMode = ((EV3ColorSensor) sensor).getRedMode();
		} else if (sensor instanceof NXTColorSensor) {
			ambientMode = ((NXTColorSensor) sensor).getAmbientMode();
			reflectedMode = ((NXTColorSensor) sensor).getRedMode();
		} else if (sensor instanceof NXTLightSensor) {
			ambientMode = ((NXTLightSensor) sensor).getAmbientMode();
			reflectedMode = ((NXTLightSensor) sensor).getRedMode();
		}
		mode = ambientMode;
	}

	/**
	 * 
	 * @param useReflected
	 *            if true, reflected mode will be used; otherwise the ambient
	 *            mode will be used;
	 */
	public void setReflected(boolean useReflected) {
		if (useReflected) {
			((LampController) sensor).setFloodlight(true);
			mode = reflectedMode;
		} else {
			((LampController) sensor).setFloodlight(false);
			mode = ambientMode;
		}
	}
	
	public String getMode()
			{
		return mode.getName();
			}

	/**
	 * returns the raw light value using whatever mode has been set.
	 */
	public float getLightValue() {
		Delay.msDelay(10);
		mode.fetchSample(sample, 0);
		return sample[0];
	}

	/**
	 * call setHigh() and setLow() before using this method. returns 0.0 when
	 * measured light value is low, 1.0 when it is high
	 * 
	 * @return value between 0.0 and 1.0
	 */

	public float getNormalizedLightValue() {
		float light = getLightValue();
		return (light - low) / (high - low);
	}

	/**
	 * set the value that will return 1.0 from getNormalizedLightValue()
	 * 
	 * @param highValue
	 */
	public void setHigh(float highValue) {
		high = highValue;
	}

	public float getHigh() {
		return high;
	}

	/**
	 * set the value that will return 0.0 from getNormalizedLightValue()
	 * 
	 * @param lowValue
	 */
	public void setLow(float  lowValue) {
		low = lowValue;
	}

	public float getLow() {
		return low;
	}

	private SensorMode ambientMode;
	private SensorMode reflectedMode;
	private SensorMode mode;
	private SampleProvider sensor;
	private float[] sample = new float[3];
	private float high, low;
	private boolean reflected = false;

}
