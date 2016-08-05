package lejos.robotics;

import lejos.hardware.sensor.*;

public class ColorAdapter implements ColorDetector, ColorIdentifier {
	
	private final float[] bufDetect;
	private final float[] bufID;
	SensorMode colorDetector;
	SensorMode colorIdentifier;
	
	public ColorAdapter(BaseSensor colorSensor) {
		this.colorDetector = colorSensor.getMode("RGB");
		this.colorIdentifier = colorSensor.getMode("ColorID");
		bufDetect=new float[colorDetector.sampleSize()];
		bufID=new float[colorIdentifier.sampleSize()];
	}
	
	public int getColorID() {
		colorIdentifier.fetchSample(bufID, 0);
		return (int)bufID[0];
	}
	 
	@Override
	public Color getColor() {
		colorDetector.fetchSample(bufDetect, 0);
		Color color = new Color((int)(256*bufDetect[0]), (int)(256*bufDetect[1]), (int)(256*bufDetect[2]));
		return color;
	}

}
