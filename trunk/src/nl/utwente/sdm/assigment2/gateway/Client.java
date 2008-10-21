package nl.utwente.sdm.assigment2.gateway;

import java.util.HashMap;
import java.util.HashSet;

public class Client {
	private String _identity;
	private HashMap<String, TrapdoorAction> _trapdoors;
	private HashSet<String> _devices;
	private String _defaultDevice;
	
	public Client(String identity, String defaultDevice) {
		_identity = identity;
		_trapdoors = new HashMap<String, TrapdoorAction>();
		_devices = new HashSet<String>();
		_defaultDevice = defaultDevice;
	}
	
	public void addTrapdoor(String keywordHash, TrapdoorAction trapdoorAction) {
		_trapdoors.put(keywordHash, trapdoorAction);
	}
	
	public void addDevice(String device) {
		_devices.add(device);
	}
	
	public void removeTrapdoor(String keywordHash) {
		if (_trapdoors.containsKey(keywordHash))
			_trapdoors.remove(keywordHash);
	}
	
	public void removeDevice(String device) {
		if (_devices.contains(device))
			_devices.remove(device);
	}
	
	public void setDefaultDevice(String defaultDevice) {
		if (_devices.contains(defaultDevice))
			_defaultDevice = defaultDevice;
	}
	
	public String getDefaultDevice() {
		return _defaultDevice;
	}
	
	public String getIdentity() {
		return _identity;
	}
}
