package nl.utwente.sdm.assigment2.gateway;

public class Device {
	private String _address;

	public Device(String address) {
		_address = address;
	}
	
	public void send(String message) {
		// ...
		System.out.println("Message send to " + _address);
	}
}
