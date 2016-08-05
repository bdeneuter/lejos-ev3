package lejos.hardware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sun.jna.LastErrorException;

import lejos.internal.dbus.DBusBluez;
import lejos.internal.io.NativeHCI;

public class LocalBTDevice {
	private NativeHCI hci = new NativeHCI();
	private DBusBluez db; 
	
	public LocalBTDevice() {
		try {
			db = new DBusBluez();		
		} catch (Exception e) {
			System.err.println("Failed to create DBusJava: " + e);
			throw(new BluetoothException(e.getMessage(), e));
		}
	}

	/**
	 * Search for and return a list of Bluetooth devices.
	 * @return The found devices.
	 * @throws IOException
	 */
	public Collection<RemoteBTDevice> search() {
		try {
			Collection<RemoteBTDevice> results = hci.hciInquiry();
			for(RemoteBTDevice d: results) {
				System.out.println("Found " + d.getName());
			}
			return results;
		} catch (LastErrorException e) {
            throw(new BluetoothException(e.getMessage(), e));
		}
	}

	/**
	 * Return a list of the devices we are paired with
	 * @return the list of paired devices
	 */
    public Collection<RemoteBTDevice> getPairedDevices() {
        try {
            List<String> devices = db.retrieveDevices(true);
            Collection<RemoteBTDevice> result = new ArrayList<RemoteBTDevice>();        
            for(String d: devices) {
                RemoteBTDevice rd = new RemoteBTDevice(db.getDeviceName(d), Bluetooth.getAddress(d), db.getDeviceClass(d));
                result.add(rd);
            }
            return result;
        } catch (Exception e) {
            throw(new BluetoothException(e.getMessage(), e));
        }
    }

    /**
     * Set the visibility state of the device
     * @param visible new visibility state
     */
	public void setVisibility(boolean visible) {
		try {
			hci.hciSetVisible(visible);
		} catch (LastErrorException e) {
            throw(new BluetoothException(e.getMessage(), e));
		}
	}

	/**
	 * return the current visibility of the device
	 * @return true if the device is visible
	 */
	public boolean getVisibility() {
        try {
            return hci.hcigetVisible();
        } catch (LastErrorException e) {
            throw(new BluetoothException(e.getMessage(), e));
        }
	}

	/**
	 * Check to see if the device is currently powered on
	 * @return true if the device is on
	 */
	public static boolean isPowerOn() {
		return true;
	}

	/**
	 * Return the address of the local device
	 * @return A string version of the Bluetooth device address
	 */
	public String getBluetoothAddress() {
        try {
            return Bluetooth.getAddress(getDeviceInfo().bdaddr);
        } catch (LastErrorException e) {
            throw(new BluetoothException(e.getMessage(), e));
        }
	}

	/**
	 * Return the name of the local device
	 * @return A string containing the device name
	 */
	public String getFriendlyName() {
        try {
            return db.getAdapterName();
        } catch (Exception e) {
            throw(new BluetoothException(e.getMessage(), e));
        }
	}

	/**
	 * Return a structure providing information about the local device 
	 * @return local device information
	 */
	public NativeHCI.DeviceInfo getDeviceInfo() {
        try {
            return hci.hciGetDeviceInfo();
        } catch (LastErrorException e) {
            throw(new BluetoothException(e.getMessage(), e));
        }
	}

	/**
	 * return a structure providing local version information
	 * @return local version information
	 */
	public NativeHCI.LocalVersion getLocalVersion() {
        try {
            return hci.hciGetLocalVersion();
        } catch (LastErrorException e) {
            throw(new BluetoothException(e.getMessage(), e));
        }
	    
	}

	/**
	 * Authenticate/pair the local device with the specified device
	 * @param deviceAddress address of the device to pair with
	 * @param pin Pin to use for pairing with the device
	 */
	public void authenticate(String deviceAddress, String pin) {
		try {
			db.authenticateRemoteDevice(deviceAddress, pin);
		} catch (Exception e) {
			System.err.println("Failed to authenticate remote device: " + e);
            throw(new BluetoothException(e.getMessage(), e));
		}
	}

	/**
	 * Remove the specified device from the known/paired list
	 * @param deviceAddress address of the device to remove
	 */
	public void removeDevice(String deviceAddress) {
        try {
            db.removeAuthenticationWithRemoteDevice(deviceAddress);
        } catch (Exception e) {
            System.err.println("Failed to remove device: " + e);
            throw(new BluetoothException(e.getMessage(), e));
        }
	}
	
	public void disconnect()
	{
	    db.disconnect();
	}
}
