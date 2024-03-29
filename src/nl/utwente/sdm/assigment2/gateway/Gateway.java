package nl.utwente.sdm.assigment2.gateway;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.HashMap;

import nl.utwente.sdm.assigment2.IBEHelper;
import nuim.cs.crypto.ibe.IbePrivateKey;
import nuim.cs.crypto.ibe.IbeSystemParameters;

/**
 * The Main class for the gateway.
 * 
 * @author Harmen
 */
public class Gateway {
	private static final int PORT_NUMBER = 10002;
	
	/** This is used to create a singleton object of gateway. */
	private static Gateway _gateway;
	
	private IbePrivateKey _privateKey;
	
	private IbeSystemParameters _systemParameters;
	
	/**
	 * Get the gateway object from other classes, this should be a singleton.
	 * The object is created in the main method of this class.
	 * @return The Gateway object.
	 */
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
	private Gateway(String identity, String keyServerAddress) {
		// Set the gateway object, this makes this object a singleton.
		_gateway = this;
		System.out.println("Starting gateway.");
		_identity = identity;
		_clients = new HashMap<String, Client>();
		try {
			System.out.println("Sending private key request to key server.");
			Object[] sysParamsAndPrivateKey = IBEHelper.authenticateWithKeyServer(keyServerAddress, IBEHelper.getMessageDigest(), _identity);
			_systemParameters = (IbeSystemParameters) sysParamsAndPrivateKey[0];
			_privateKey = (IbePrivateKey) sysParamsAndPrivateKey[1];
			System.out.println("Succesfully received private key and global parameters.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		start();
	}
	
	/**
	 * Check whether a client is registered to this gateway.
	 * @param identity The identity of the client.
	 * @return True if the client is registered, otherwise false.
	 */
	public boolean clientIsRegistered(String identity) {
		return _clients.containsKey(identity);
	}
	
	/**
	 * Get the client with a certain identity.
	 * @param identity The identity of the client.
	 * @return The client object.
	 */
	public Client getClient(String identity) {
		return _clients.get(identity);
	}
	
	public void registerClient(String identity, String address, int port) {
		_clients.put(identity, new Client(identity, address, port));
	}
	
	public void unregisterClient(String identity) {
		_clients.remove(identity);
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
        
        System.out.println("Server ready for clients to communicate.");

        while (true) {
        	try {
				new GatewayServerThread(serverSocket.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

	/**
	 * Get the private key of the gateway.
	 * @return The private key of the gateway.
	 */
	public PrivateKey getPrivateKey() {
		return _privateKey;
	}
	
	public IbeSystemParameters getSystemParameters() {
		return _systemParameters;
	}
	
	/**
	 * The Main procedure initializes the Gateway object to start the Gateway server.
	 * @param args The first argument should be the identity of the gateway.
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			new Gateway(args[0].toString(), args[1].toString());
		} else {
			System.out.println("The first argument should be the identity of the gateway and the second the address of the keyserver.\nExample: java Gateway gateway@utwente.nl keyserver.utwente.nl");
		}
	}
}
