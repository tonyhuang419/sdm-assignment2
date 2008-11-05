package nl.utwente.sdm.assigment2.keyserver;

import nl.utwente.sdm.assigment2.IBEMessageProtocolCommands;
import nuim.cs.crypto.blitz.point.AffinePoint;
import nuim.cs.crypto.ibe.IbePrivateKey;
import nuim.cs.crypto.ibe.IbeSystemParameters;

/**
 * The protocol handler for the messages send and received from the clients to the keyserver.
 * @author Harmen
 */
public class IBEMessageProtocol {
	/** The reference to the gateway, to be able to access client information. */
	private KeyServer _keyServer;
	
	/**
	 * Constructor.
	 */
	public IBEMessageProtocol() {
		_keyServer = KeyServer.getKeyServer();
	}
	
	/**
	 * Process the input, received from a client.
	 * @param input The input received.
	 * @return The output send to the sender (so the client who send the message).
	 */
	public String processInput(String input) {
		// Split the received message on the spaces.
		String[] splitInput = input.split(" ");
		// The first part is the command.
		String cmd = splitInput[0];
		
		if (cmd.equals(IBEMessageProtocolCommands.AUTHENTICATE)) {
			// Return the global parameters.
			IbeSystemParameters systemParameters = _keyServer.getSystemParameters();
			
			// The response should be formatted as follows: [p], [q], [l], [pointPx], [pointPy], [pointPPubx], [pointPPuby], [privateKeyX], [privateKeyY].
			
			// Authenticate the person with the given identity.
			// We first generate the private key for the identity.
			// The private key can be constructed using the x and y of the AffinePoint of the privatekey.
			// So we send these values to the client.
			String identity = splitInput[1];
			IbePrivateKey privateKey = (IbePrivateKey) _keyServer.authenticate(identity);
			AffinePoint ap = privateKey.getPrivateKey();
			System.out.println("Generated private key and sending it to client.");
			
			// Return a string with the P and Ppub from the system parameters.
			return _keyServer.getMap().getCurve().getField().getChar().toString() + " " + 
				   _keyServer.getMap().getQ().toString() + " " + 
				   _keyServer.getMap().getL().toString() + " " +
				   systemParameters.getP().x() + " " + 
				   systemParameters.getP().y() +  " " + 
				   systemParameters.getPpub().x() + " " + 
				   systemParameters.getPpub().y() + " " +
				   ap.x().toBigInteger().toString() + " " + 
				   ap.y().toBigInteger().toString();
		}
		else { return "unable to process input: unknown input"; }
	}
}
