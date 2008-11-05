package nl.utwente.sdm.assigment2.keyserver;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.HashSet;

import nl.utwente.sdm.assigment2.IBEHelper;
import nuim.cs.crypto.bilinear.ModifiedTatePairing;
import nuim.cs.crypto.ibe.IbeSystemParameters;

/**
 * The keyserver is responsible for generating the private 
 * keys for identities who want to authenticate.
 * @author Harmen
 */
public class KeyServer {
	private static final int PORT_NUMBER = 10001;
	
	private static KeyServer _keyServer;
	private HashSet<String> _authenticated;
	public static MessageDigest _hash;
	private ModifiedTatePairing _map;
	private BigInteger _masterKey;
	private IbeSystemParameters _systemParameters;

	/**
	 * The main procedure which starts the key server.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		new KeyServer();
	}
	
	/**
	 * Get the keyserver object from other classes, this should be a singleton.
	 * The object is created in the main method of this class.
	 * @return The KeyServer object.
	 */
	public static KeyServer getKeyServer() {
		return _keyServer;
	}
	
	/**
	 * Constructor.
	 */
	private KeyServer() {
		_keyServer = this;
		System.out.println("Starting KeyServer.");
		
		// Initialize the set with the authenticated identities.
		_authenticated = new HashSet<String>();
		
		// Set the hash method.
		try {
			_hash = IBEHelper.getMessageDigest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		// Gordon's algorithm is used to generate the correct p,q and l values. 
		// This algorithm will not always generate a large enough value p for 
		// the finite field. Repeated attempts may be required. 
		_map = new ModifiedTatePairing();
        boolean fieldTooSmall = false;
        do {
            fieldTooSmall = false;
            // get a count of the maximum possible number of 
            // points that we could use, i.e. p / kappa where
            // p is the upper limit of finite field Fp
            BigInteger maxPts =
                _map.getCurve().getField().getChar().
                    divide( _map.getCurve().getKappa() );
            int bytes = maxPts.bitLength() / 8;
            if( bytes < _hash.getDigestLength() ) {
                _map = new ModifiedTatePairing();
                fieldTooSmall = true;
            }                            
        }
        while( fieldTooSmall );

        // Generate the master key and set the public parameters (systemParameters).		
		_masterKey = new BigInteger(_map.getQ().bitLength() - 1, new SecureRandom());
		_systemParameters = new IbeSystemParameters( _map, _hash, _masterKey );

		/**System.out.println("Testing key generation.");
		String message = "This is a test message.";
		System.out.println("Message: " + message);
		byte[] encryptedMessage = IBEHelper.encryptMessage(message.getBytes(), IBEHelper.getPublicKey("test", _hash), _systemParameters);
		PrivateKey privateKey = IBEHelper.getPrivateKey("test", _map, _hash, _masterKey);
		System.out.println("private key format: " + privateKey.getFormat() + ", algorithm: " + privateKey.getAlgorithm());
		String decryptedMessage = IBEHelper.decryptMessage(privateKey, _systemParameters, encryptedMessage);
		System.out.println("Decrypted message: " + decryptedMessage);
		System.out.println("End of key generation test.\n");*/

		// Start the keyserver.
		start();
	}
	
	/**
	 * This procedure should open a socket and start listening on it.
	 * It should be able to process messages send from clients with different content.
	 * It could be the following messages:
	 *  - A client wants to get the global parameters.
	 *  - A client wants to authenticate and get the private key.
	 */
	public void start() {
		ServerSocket serverSocket = null;
        try {
        	serverSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT_NUMBER);
            System.exit(-1);
        }
        
        System.out.println("KeyServer ready for clients to communicate.");

        while (true) {
        	try {
				new KeyServerThread(serverSocket.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * Procedure to authenticate with the keyserver,
	 * this will generate a private key and send it to the authenticator.
	 * @param identity The identity of the user who is authenticating.
	 * @return Returns the generated private key for the authenticator.
	 */
	public PrivateKey authenticate(String identity) {
		_authenticated.add(identity);
		return IBEHelper.getPrivateKey(identity, _map, _hash, _masterKey);
	}
	
	/**
	 * Get the system parameters for the server (global parameters).
	 * @return The system parameters.
	 */
	public IbeSystemParameters getSystemParameters() {
		return _systemParameters;
	}
	
	public ModifiedTatePairing getMap() {
		return _map;
	}

}
