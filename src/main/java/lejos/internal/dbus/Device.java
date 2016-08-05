package lejos.internal.dbus;
import lejos.internal.dbus.DBusProperties.DBusProperty;
import lejos.internal.dbus.DBusProperties.DBusPropertyAccessType;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
@DBusInterfaceName("org.bluez.Device")
public interface Device extends DBusInterface, DBusProperties.PropertiesAccess {

    public static enum Properties implements DBusProperties.PropertyEnum {
        /**
         * The Bluetooth device address of the remote device.
         */
        @DBusProperty(type = String.class, access = DBusPropertyAccessType.READONLY)
        Address,

        /**
         * The Bluetooth remote name. This value can not be changed. Use the
         * Alias property instead.
         */
        @DBusProperty(type = String.class, access = DBusPropertyAccessType.READONLY)
        Name,

        /**
         * Proposed icon name according to the freedesktop.org icon naming
         * specification.
         */
        @DBusProperty(type = String.class, access = DBusPropertyAccessType.READONLY)
        Icon,

        /** The Bluetooth class of device of the remote device. */
        @DBusProperty(type = UInt32.class, access = DBusPropertyAccessType.READONLY)
        Class,

        /**
         * List of 128-bit UUIDs that represents the available remote services.
         */
        @DBusProperty(type = String[].class, access = DBusPropertyAccessType.READONLY)
        UUIDs,

        /**
         * Indicates if the remote device is paired.
         */
        @DBusProperty(type = boolean.class, access = DBusPropertyAccessType.READONLY)
        Paired,
        /**
         * Indicates if the remote device is currently connected. A
         * PropertyChanged signal indicate changes to this status.
         */
        @DBusProperty(type = boolean.class, access = DBusPropertyAccessType.READONLY)
        Connected,

        /**
         * Indicates if the remote is seen as trusted. This setting can be
         * changed by the application.
         */
        @DBusProperty(type = boolean.class)
        Trusted,

        /**
         * The name alias for the remote device. The alias can be used to have a
         * different friendly name for the remote device.
         * 
         * In case no alias is set, it will return the remote device name.
         * Setting an empty string as alias will convert it back to the remote
         * device name.
         * 
         * When reseting the alias with an empty string, the emitted
         * PropertyChanged signal will show the remote name again.
         */
        @DBusProperty(type = String.class)
        Alias,

        /**
         * List of device node object paths.
         */
        @DBusProperty(type = Path[].class, access = DBusPropertyAccessType.READONLY)
        Nodes,

        /**
         * The object path of the adapter the device belongs to.
         */
        @DBusProperty(type = Path.class, access = DBusPropertyAccessType.READONLY)
        Adapter,
        /**
         * Set to true if the device only supports the pre-2.1 pairing
         * mechanism. This property is useful in the Adapter.DeviceFound signal
         * to anticipate whether legacy or simple pairing will occur.
         * 
         * Note that this property can exhibit false-positives in the case of
         * Bluetooth 2.1 (or newer) devices that have disabled Extended Inquiry
         * Response support.
         */
        @DBusProperty(type = boolean.class, access = DBusPropertyAccessType.READONLY)
        LegacyPairing

    }
}