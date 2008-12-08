package nl.utwente.sdm.assigment2.gateway;

import java.io.IOException;
import java.net.UnknownHostException;

import nl.utwente.sdm.assigment2.IBEHelper;

/**
 * The device object, this is responsible for delivering the messages to a certain address.
 * @author Harmen
 */
public class Device {
	/** The address of the device. */
	private String _address;
	
	/** The port of the device. */
	private int _port;

	/**
	 * Constructor.
	 * @param address The address of the device.
	 */
	public Device(String address, int port) {
		_address = address;
		_port = port;
	}
	
	/**
	 * Send a message to the device.
	 * @param message The message to send.
	 */
	public void send(String fromIdentity, String message) {
		// Connect to the device and deliver the message.
		System.out.println("Delivering message to address " + _address + ":" + _port);
		try {
			IBEHelper.deliverMessageToClient(fromIdentity, message, _address, _port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Succesfully delivered message to address " + _address + ":" + _port);
	}
}
