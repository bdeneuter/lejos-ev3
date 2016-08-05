package lejos.hardware;

/**
 * Exception thrown when errors are detected in the Bluetooth classes.
 * @author andy
 *
 */
public class BluetoothException extends RuntimeException
{

    public BluetoothException()
    {
    }

    public BluetoothException(String message)
    {
        super (message);
    }

    public BluetoothException(Throwable cause)
    {
        super (cause);
    }

    public BluetoothException(String message, Throwable cause)
    {
        super (message, cause);
    }
}
