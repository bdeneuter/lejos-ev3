package lejos.hardware;

import java.io.Serializable;

public class RemoteBTDevice implements Serializable {
	
	private static final long serialVersionUID = -1668354353670941450L;
	private String name;
	private byte[] address;
	private int cod;
	
	public RemoteBTDevice(String name, byte[] address, int cod) {
		this.name = name;
		this.address = address;
		this.cod = cod;
	}
	
	public String getName() {
		return name;
	}
	
	public byte[] getDeviceAddress() {
		return address;
	}
	
	public int getDeviceClass() {
		return cod;
	}
	
	public String getAddress() {
	    return Bluetooth.getAddress(address);
	}
	
	public void authenticate(String pin) {
		
	}
}
