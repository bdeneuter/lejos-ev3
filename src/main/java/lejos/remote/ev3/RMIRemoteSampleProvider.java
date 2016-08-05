package lejos.remote.ev3;

import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import lejos.hardware.DeviceException;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.BaseSensor;
import lejos.robotics.SampleProvider;

public class RMIRemoteSampleProvider extends UnicastRemoteObject implements RMISampleProvider {

	private static final long serialVersionUID = -8432755905878519147L;
	private SampleProvider provider;
	private BaseSensor sensor;
	private int sampleSize;

	protected RMIRemoteSampleProvider(String portName, String sensorName, String modeName) throws RemoteException {
		super(0);
    	try {
			Class<?> c = Class.forName(sensorName);
			Class<?>[] params = new Class<?>[1];
			params[0] = Port.class;
			Constructor<?> con = c.getConstructor(params);
			Object[] args = new Object[1];
			args[0] = LocalEV3.get().getPort(portName);
			sensor = (BaseSensor) con.newInstance(args);
			if (modeName == null) provider = (SampleProvider) sensor;
			else provider = sensor.getMode(modeName);
			sampleSize = provider.sampleSize();
		} catch (Exception e) {
			if (sensor != null) sensor.close();
			throw new DeviceException("Failed to create sample provider", e);
		}
	}

	@Override
	public float[] fetchSample() throws RemoteException {
		float[] sample = new float[sampleSize];
		provider.fetchSample(sample, 0);
		return sample;
	}

	@Override
	public void close() throws RemoteException {
		if (sensor != null) sensor.close();
	}
}
