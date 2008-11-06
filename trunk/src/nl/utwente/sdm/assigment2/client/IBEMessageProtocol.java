package nl.utwente.sdm.assigment2.client;

import java.util.LinkedList;

import nl.utwente.sdm.assigment2.IBEMessageProtocolCommands;

/**
 * The protocol handler for the messages send and received from the gateway.
 * @author Harmen
 */
public class IBEMessageProtocol {
	/** The reference to the client, to be able to access client information. */
	private Client _client;
	
	/**
	 * Constructor.
	 */
	public IBEMessageProtocol(Client client) {
		_client = client;
	}
	
	/**
	 * Process the input, received from a client.
	 * @param input The input received.
	 * @return The output send to the sender (so the client who send the message).
	 */
	public String processInput(LinkedList<String> lines) {
		// Split the received message on the spaces.
		String input = lines.get(0);
		String[] splitInput = input.split(" ");
		// The first part is the command.
		String cmd = splitInput[0];
		
		if (cmd.equals(IBEMessageProtocolCommands.MESSAGE)) {
			// Show the message to the client.
			// The received arguments should be fromIdentity and message.
			StringBuffer encryptedMessageBuffer = new StringBuffer();
			for (int i=1; i<lines.size(); ++i) {
				encryptedMessageBuffer.append(lines.get(i));
				if (i < (lines.size() - 1))
					encryptedMessageBuffer.append("\n");
			}
			
			//System.out.println("Received encrypted message: " + encryptedMessageBuffer.toString());
			
			//byte[] receivedMessage = encryptedMessageBuffer.toString().getBytes();
			//System.out.println("Size of received encrypted message: " + receivedMessage.length);
			
			//byte encryptedMessage[] = IBEHelper.encryptMessage("test bericht".getBytes(), IBEHelper.getPublicKey("onwieze@gmail.com", IBEHelper.getMessageDigest()), _client.getSystemParameters());
			//System.out.println("Encrypted message: " + new String(encryptedMessage));
			
			// We constantly get an error while decrypting the message, so we just display that we have received a message.
			//String decryptedMessage = IBEHelper.decryptMessage(_client.getPrivateKey(), _client.getSystemParameters(), receivedMessage);
			_client.showReceivedMessage(splitInput[1], "");
			
			return "OK";
		}
		else { return "unable to process input: unknown input"; }
	}
}
