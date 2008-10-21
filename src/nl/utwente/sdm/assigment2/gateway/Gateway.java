package nl.utwente.sdm.assigment2.gateway;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * The Main class for the gateway.
 * 
 * @author Harmen
 */
public class Gateway {
	private static final int PORT_NUMBER = 12345;
	
	private static Gateway _gateway;
	
	public static Gateway getGateway() {
		return _gateway;
	}
	
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
	private Gateway(String identity) {
		System.out.println("Starting gateway.");
		_identity = identity;
		_clients = new HashMap<String, Client>();
	}
	
	public boolean clientIsRegistered(String identity) {
		return _clients.containsKey(identity);
	}
	
	public Client getClient(String identity) {
		return _clients.get(identity);
	}

	/**
	 * This procedure should open a socket and start listening on it.
	 * It should be able to process messages send from clients with different content.
	 * It could be the following messages:
	 *  - A client wants to register in the server.
	 *  - A client wants to send a message to another client.
	 *  - A client wants to change the settings in the gateway, for example add a trapdoor or a device.
	 */
	public void start() {
		ServerSocket serverSocket = null;
        try {
        	serverSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT_NUMBER);
            System.exit(-1);
        }

        while (true) {
        	try {
				new GatewayServerThread(serverSocket.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

	/**
	 * The Main procedure initializes the Gateway object to start the Gateway server.
	 * @param args The first argument should be the identity of the gateway.
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			_gateway = new Gateway(args[0].toString());
		} else {
			System.out.println("The first argument should be the identity of the gateway.");
		}
	}

}
