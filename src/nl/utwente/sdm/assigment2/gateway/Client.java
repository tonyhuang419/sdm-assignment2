package nl.utwente.sdm.assigment2.gateway;

import java.util.HashMap;

/**
 * The client object, this represents a clients which is registered in the gateway.
 * @author Harmen
 */
public class Client {
	/** The identity of the client. */
	private String _identity;
	/** The collection with trapdoors. */
	private HashMap<String, TrapdoorAction> _trapdoors;
	/** The devices of the client. */
	private HashMap<String, Device> _devices;
	/** The address of the default device of the client. */
	private String _defaultDevice;
	
	/**
	 * Constructor.
	 * @param identity The identity of the client.
	 * @param defaultDeviceAddress The address of the default client.
	 */
	public Client(String identity, String defaultDeviceAddress, int defaultDevicePort) {
		_identity = identity;
		_trapdoors = new HashMap<String, TrapdoorAction>();
		_devices = new HashMap<String, Device>();
		_defaultDevice = defaultDeviceAddress + ":" + defaultDevicePort;
		addDevice(defaultDeviceAddress, defaultDevicePort);
	}
	
	/**
	 * Procedure to add a trapdoor for the client.
	 * @param keywordHash The hashed keyword.
	 * @param trapdoorAction The TrapdoorAction corresponding to the keyword.
	 */
	public void addTrapdoor(String keywordHash, TrapdoorAction trapdoorAction) {
		_trapdoors.put(keywordHash, trapdoorAction);
	}
	
	/**
	 * Check whether there exists a trapdoor for a hashed keyword.
	 * @param keyword The hashed keyword.
	 * @return True when a trapdoor exists, otherwise false.
	 */
	public boolean trapdoorForKeywordExist(String keyword) {
		return _trapdoors.containsKey(keyword);
	}
	
	/**
	 * Get the trapdoor for a certain hashed keyword.
	 * @param keyword The hashed keyword.
	 * @return The TrapdoorAction for the keyword.
	 */
	public TrapdoorAction getTrapdoorForKeyword(String keyword) {
		return _trapdoors.get(keyword);
	}
	
	/**
	 * Add a device for the client.
	 * @param device The device to add.
	 */
	public void addDevice(String address, int port) {
		_devices.put(address + ":" + port, new Device(address, port));
	}
	
	/**
	 * Check whether this client has a device with the given address.
	 * @param address The address of the device.
	 * @return True when it has this device, otherwise false.
	 */
	public boolean hasDevice(String address, int port) {
		return _devices.containsKey(address + ":" + port);
	}
	
	/**
	 * Get a device by address.
	 * @param address The address of the device.
	 * @return The device.
	 */
	public Device getDevice(String address, int port) {
		return _devices.get(address + ":" + port);
	}
	
	/**
	 * Remove a trapdoor for this client/
	 * @param keywordHash The hashed keyword of the trapdoor to remove.
	 */
	public void removeTrapdoor(String keywordHash) {
		if (_trapdoors.containsKey(keywordHash))
			_trapdoors.remove(keywordHash);
	}
	
	/**
	 * Remove a device for this client.
	 * @param device The device to remove.
	 */
	public void removeDevice(String address, int port) {
		if (_devices.containsKey(address + ":" + port))
			_devices.remove(address + ":" + port);
	}
	
	/**
	 * Procedure to set the default device.
	 * @param defaultDeviceAddress
	 */
	public void setDefaultDevice(String defaultDeviceAddress) {
		_defaultDevice = defaultDeviceAddress;
	}
	
	/**
	 * Get the default device, of which the address is _defaultDevice.
	 * @return The default device.
	 */
	public Device getDefaultDevice() {
		return _devices.get(_defaultDevice);
	}
	
	/**
	 * Get the identity of the gateway.
	 * @return The identity of the gateway.
	 */
	public String getIdentity() {
		return _identity;
	}
}
