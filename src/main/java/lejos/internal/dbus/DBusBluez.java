package lejos.internal.dbus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

public class DBusBluez {
	private Manager dbusManager;
	private DBusConnection dbusConn;
	private Path adapterPath;
	private Adapter adapter;
	
	public DBusBluez() throws DBusException {
		dbusConn = DBusConnection.getConnection(DBusConnection.SYSTEM);
		dbusManager = dbusConn.getRemoteObject("org.bluez", "/", Manager.class);
		selectAdapter(dbusManager.DefaultAdapter());
	}
	
    public void selectAdapter(Path adapterPath) throws DBusException {
        adapter = dbusConn.getRemoteObject("org.bluez", adapterPath.getPath(), Adapter.class);
        this.adapterPath = adapterPath;
    }
    /*
     * (non-Javadoc)
     *
     * @see org.bluez.BlueZAPI#getAdapterAddress()
     */
    public String getAdapterAddress() {
        return DBusProperties.getStringValue(adapter, Adapter.Properties.Address);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.bluez.BlueZAPI#getAdapterDeviceClass()
     */
    public int getAdapterDeviceClass() {
        // Since BlueZ 4.34
        Integer deviceClass = DBusProperties.getIntValue(adapter, Adapter.Properties.Class);
        if (deviceClass == null) {
            return 0; // What should we return?
        } else {
            return deviceClass.intValue();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.bluez.BlueZAPI#getAdapterName()
     */
    public String getAdapterName() {
        return DBusProperties.getStringValue(adapter, Adapter.Properties.Name);
    }
    
    
    public boolean authenticateRemoteDevice(String deviceAddress, final String passkey) throws DBusException {
    	 
        String agentPath = "/org/lejos/authenticate/" + getAdapterID() + "/" + deviceAddress.replace(':', '_');

        dbusConn.exportObject(agentPath, new PinAgent(passkey));

        //System.out.println("Calling CreatedPairedDevive on " + deviceAddress + " using agent: " + agentPath);
        try {
            adapter.CreatePairedDevice(deviceAddress, new Path(agentPath), "");
            return true;
        } finally {
            dbusConn.unExportObject(agentPath);
        }
    }
    
    public String getAdapterID() {
        return hciID(adapterPath.getPath());
    }

    private String hciID(String adapterPath) {
        final String bluezPath = "/org/bluez/";
        String path;
        if (adapterPath.startsWith(bluezPath)) {
            path = adapterPath.substring(bluezPath.length());
        } else {
            path = adapterPath;
        }
        int lastpart = path.lastIndexOf('/');
        if ((lastpart != -1) && (lastpart != path.length() -1)) {
            return path.substring(lastpart + 1);
        } else {
            return path;
        }
    }
    
    public List<String> listAdapters() {
        List<String> a = new ArrayList<String>();
        Path[] adapters = dbusManager.ListAdapters();
        if (adapters != null) {
            for (int i = 0; i < adapters.length; i++) {
                a.add(hciID(adapters[i].getPath()));
            }
        }
        return a;
    }
    
    public List<String> listDevices() {
    	List<String> a = new ArrayList<String>();
    	Path[] devices = adapter.ListDevices();
    	for(Path device: devices) {
    		String dev = device.getPath();
    		int ind = dev.indexOf("dev_");
    		if (ind >= 0) a.add(dev.substring(ind+4).replace('_', ':'));
    	}
    	return a;
    }
    
    /*
     * (non-Javadoc)
     *
     * @see org.bluez.BlueZAPI#retrieveDevices(boolean)
     */
    public List<String> retrieveDevices(boolean preKnown) {
        Path[] devices = adapter.ListDevices();
        List<String> addresses = new Vector<String>();
        if (devices != null) {
            for (Path devicePath : devices) {
                System.out.println("Path " + devicePath.getPath());
                try {
                    Device device = dbusConn.getRemoteObject("org.bluez", devicePath.getPath(), Device.class);
                    Map<String, Variant<?>> properties = device.GetProperties();
                    if (properties != null) {
                        String address = DBusProperties.getStringValue(properties, Device.Properties.Address);
                        boolean paired = DBusProperties.getBooleanValue(properties, Device.Properties.Paired, false);
                        boolean trusted = DBusProperties.getBooleanValue(properties, Device.Properties.Trusted, false);
                        if ((!preKnown) || paired || trusted) {
                            addresses.add(address);
                        }
                    }
                } catch (DBusException e) {
                    System.out.println("can't get device " + devicePath + " exception" + e);
                }
            }
        }
        return addresses;
    }
    
    public void removeAuthenticationWithRemoteDevice(String deviceAddress) throws DBusException {
        Path devicePath = adapter.FindDevice(deviceAddress);
        adapter.RemoveDevice(devicePath);
    }    
    
    public String getDeviceName(String deviceAddress) {
        try {
            Path devicePath = adapter.FindDevice(deviceAddress);
            Device device = dbusConn.getRemoteObject("org.bluez", devicePath.getPath(), Device.class);
            Map<String, Variant<?>> properties = device.GetProperties();
            if (properties != null) {
                String name = DBusProperties.getStringValue(properties, Device.Properties.Name);
                return name;
            }
        } catch (DBusException e) {
            System.out.println("Can't get property for " + deviceAddress + " " + e);
        }        
        return "";
    }
    
    public int getDeviceClass(String deviceAddress) {
        try {
            Path devicePath = adapter.FindDevice(deviceAddress);
            Device device = dbusConn.getRemoteObject("org.bluez", devicePath.getPath(), Device.class);
            Map<String, Variant<?>> properties = device.GetProperties();
            if (properties != null) {
                int cod = DBusProperties.getIntValue(properties, Device.Properties.Class);
                return cod;
            }
        } catch (DBusException e) {
            System.out.println("Can't get property for " + deviceAddress + " " + e);
        }        
        return 0;
    }
    
    public void disconnect()
    {
        dbusConn.disconnect();
    }
    
}
