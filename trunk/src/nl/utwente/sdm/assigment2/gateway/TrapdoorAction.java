package nl.utwente.sdm.assigment2.gateway;

import java.util.HashSet;

/**
 * This represents a trapdooraction for a client.
 * These trapdoors are fired when a messages has been send to a client containing a certain keyword.
 * Different devices can be informed when this trapdoor is fired.
 * @author Harmen
 *
 */
public class TrapdoorAction {
	private HashSet<Device> _devices;

	/**
	 * Constructor for the TrapdoorAction.
	 * @param devices A HashSet with the devices for which this trapdoor is.
	 */
	public TrapdoorAction(HashSet<Device> devices) {
		_devices = devices;
	}
	
	/**
	 * Get the trapdoor devices for this trapdoor.
	 * @return A HashSet with the trapdoor devices.
	 */
	public HashSet<Device> getTrapdoorDevices() {
		return _devices;
	}
}
