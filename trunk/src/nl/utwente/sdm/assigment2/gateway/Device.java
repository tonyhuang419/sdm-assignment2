package nl.utwente.sdm.assigment2.gateway;

/**
 * The device object, this is responsible for delivering the messages to a certain address.
 * @author Harmen
 */
public class Device {
	/** The address of the device. */
	private String _address;

	/**
	 * Constructor.
	 * @param address The address of the device.
	 */
	public Device(String address) {
		_address = address;
	}
	
	/**
	 * Send a message to the device.
	 * @param message The message to send.
	 */
	public void send(String message) {
		// Connect to the device and deliver the message.
		System.out.println("A message has been send to " + _address);
	}
}
