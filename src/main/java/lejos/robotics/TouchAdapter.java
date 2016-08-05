package lejos.robotics;

import lejos.hardware.sensor.*;

public class TouchAdapter implements Touch{
	private final float[] buf;
	SensorMode source;
	
	public TouchAdapter(BaseSensor touchSensor) {
		this.source = touchSensor.getMode("Touch");
		buf=new float[source.sampleSize()];
	}

	@Override
	public boolean isPressed() {
		source.fetchSample(buf,0);
		return (buf[0] > 0);
	}
}
