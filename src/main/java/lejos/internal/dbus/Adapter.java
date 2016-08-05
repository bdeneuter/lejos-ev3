package lejos.internal.dbus;
import lejos.internal.dbus.DBusProperties.DBusProperty;
import lejos.internal.dbus.DBusProperties.DBusPropertyAccessType;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;

@DBusInterfaceName("org.bluez.Adapter")
public interface Adapter extends DBusInterface, DBusProperties.PropertiesAccess{
    public static enum Properties implements DBusProperties.PropertyEnum {

        /**
         * The Bluetooth device address. Example: "00:11:22:33:44:55"
         */
        @DBusProperty(type = String.class, access = DBusPropertyAccessType.READONLY)
        Address,

        /**
         * The Bluetooth friendly name. This value can be changed and a PropertyChanged
         * signal will be emitted.
         */
        @DBusProperty(type = String.class)
        Name,

        /**
         * The Bluetooth class of device.
         *
         * @since BlueZ 4.34
         */
        @DBusProperty(type = UInt32.class, access = DBusPropertyAccessType.READONLY)
        Class,

        /**
         * Switch an adapter on or off. This will also set the appropriate connectable
         * state.
         */
        @DBusProperty(type = boolean.class)
        Powered,

        /**
         * Switch an adapter to discoverable or non-discoverable to either make it visible
         * or hide it. This is a global setting and should only be used by the settings
         * application.
         *
         * If the DiscoverableTimeout is set to a non-zero value then the system will set
         * this value back to false after the timer expired.
         *
         * In case the adapter is switched off, setting this value will fail.
         *
         * When changing the Powered property the new state of this property will be
         * updated via a PropertyChanged signal.
         */
        @DBusProperty(type = boolean.class)
        Discoverable,

        /**
         * Switch an adapter to pairable or non-pairable. This is a global setting and
         * should only be used by the settings application.
         *
         * Note that this property only affects incoming pairing requests.
         */
        @DBusProperty(type = boolean.class)
        Pairable,

        /**
         * The pairable timeout in seconds. A value of zero means that the timeout is
         * disabled and it will stay in pairable mode forever.
         */
        @DBusProperty(type = UInt32.class)
        PaireableTimeout,

        /**
         * The discoverable timeout in seconds. A value of zero means that the timeout is
         * disabled and it will stay in discoverable/limited mode forever.
         *
         * The default value for the discoverable timeout should be 180 seconds (3
         * minutes).
         */
        @DBusProperty(type = UInt32.class)
        DiscoverableTimeout,

        /**
         * Indicates that a device discovery procedure is active.
         */
        @DBusProperty(type = boolean.class, access = DBusPropertyAccessType.READONLY)
        Discovering,

        /**
         * List of device object paths.
         */
        @DBusProperty(type = Path[].class, access = DBusPropertyAccessType.READONLY)
        Devices
    }
	
	Path CreatePairedDevice(String address, Path agent, String capability);

	/**
     * Returns list of device object paths.
     */
    Path[] ListDevices();
    Path FindDevice(String address);
    void RemoveDevice(Path device);
}