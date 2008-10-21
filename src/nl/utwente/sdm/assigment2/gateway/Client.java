package nl.utwente.sdm.assigment2.gateway;

import java.util.HashMap;
import java.util.HashSet;

public class Client {
	private String _identity;
	private HashMap<String, TrapdoorAction> _trapdoors;
	private HashSet<Device> _devices;
	private Device _defaultDevice;
	
	public Client(String identity, String defaultDeviceAddress) {
		_identity = identity;
		_trapdoors = new HashMap<String, TrapdoorAction>();
		_devices = new HashSet<Device>();
		_defaultDevice = new Device(defaultDeviceAddress);
	}
	
	public void addTrapdoor(String keywordHash, TrapdoorAction trapdoorAction) {
		_trapdoors.put(keywordHash, trapdoorAction);
	}
	
	public boolean trapdoorForKeywordExist(String keyword) {
		return _trapdoors.containsKey(keyword);
	}
	
	public TrapdoorAction getTrapdoorForKeyword(String keyword) {
		return _trapdoors.get(keyword);
	}
	
	public void addDevice(Device device) {
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
	
	public void setDefaultDevice(String defaultDeviceAddress) {
		_defaultDevice = new Device(defaultDeviceAddress);
	}
	
	public Device getDefaultDevice() {
		return _defaultDevice;
	}
	
	public String getIdentity() {
		return _identity;
	}
}
