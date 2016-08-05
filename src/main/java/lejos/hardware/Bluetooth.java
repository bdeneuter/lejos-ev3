package lejos.hardware;

import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTCommConnector;

public class Bluetooth {
	public static NXTCommConnector getNXTCommConnector() {
		return new BTConnector();
	}
	
	public static LocalBTDevice getLocalDevice() {
		return new LocalBTDevice();
	}
	
	// Utility methods
	

	/**
	 * Return a Bluetooth binary address given a String version of the address
	 * @param address String address
	 * @return Binary address
	 */
    public static byte[] getAddress(String address) {
        byte[] bdaddr = new byte[6];
        
        for(int i=0;i<address.length();i += 3) {
            // Note we must use Integer below as Byte expects a signed value,
            // we have unsigned hex values.
            byte b = (byte)Integer.parseInt(address.substring(i,i+2), 16);
            bdaddr[5 - (i/3)] = b;
        }
        
        return bdaddr;
    }

    /**
     * Return a String version of a Bluetooth address given a binary address
     * @param address
     * @return the Bluetooth address
     */
    public static String getAddress(byte[] address) {
        StringBuilder sb = new StringBuilder();
        for(int j=5;j>=0;j--) {
            String hex = Integer.toHexString(address[j] & 0xFF).toUpperCase();
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
            if (j>0) sb.append(':');
        }
        return sb.toString();
    }


}
