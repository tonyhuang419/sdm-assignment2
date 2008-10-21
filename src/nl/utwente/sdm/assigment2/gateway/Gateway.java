package nl.utwente.sdm.assigment2.gateway;

import java.util.HashMap;

/**
 * The Main class for the gateway.
 * 
 * @author Harmen
 */
public class Gateway {
	/**
	 * The identity for the gateway is used to generate the public and private key.
	 * These are needed when verifying that the messages from the client to the server
	 * for registering and changing settings are actually from the client.
	 */
	private String _identity;
	
	/**
	 * The list with clients who are registered at this gateway.
	 */
	private HashMap<String, Client> _clients;

	/**
	 * Constructor for the gateway.
	 * This will initialize and start the gateway server.
	 * @param identity The identity of the gateway.
	 */
	public Gateway(String identity) {
		System.out.println("Starting gateway.");
		_identity = identity;
		_clients = new HashMap<String, Client>();
	}

	/**
	 * This procedure should open a socket and start listening on it.
	 * It should be able to process messages send from clients with different content.
	 * It could be the following messages:
	 *  - A client wants to register in the server.
	 *  - A client wants to send a message to another client.
	 *  - A client wants to change the settings in the gateway, for example add a trapdoor or a device.
	 */
	public void start() {}

	/**
	 * The Main procedure initializes the Gateway object to start the Gateway server.
	 * @param args The first argument should be the identity of the gateway.
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			new Gateway(args[0].toString());
		} else {
			System.out.println("The first argument should be the identity of the gateway.");
		}
	}

}
