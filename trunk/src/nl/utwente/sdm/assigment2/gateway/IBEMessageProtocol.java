package nl.utwente.sdm.assigment2.gateway;

import java.util.HashSet;

import nl.utwente.sdm.assigment2.IBEMessageProtocolCommands;

/**
 * The protocol handler for the messages send and received from the clients.
 * @author Harmen
 */
public class IBEMessageProtocol {
	/** The reference to the gateway, to be able to access client information. */
	private Gateway _gateway;
	
	/**
	 * Constructor.
	 */
	public IBEMessageProtocol() {
		_gateway = Gateway.getGateway();
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
		
		if(cmd.equals(IBEMessageProtocolCommands.MESSAGE)) {
			String source = splitInput[1];
			String target = splitInput[2];
			String message = splitInput[3];
			HashSet<String> keywords = new HashSet<String>();
			StringBuffer keywordList = new StringBuffer();
			for (int i=4; i<splitInput.length; ++i) {
				keywords.add(splitInput[i]);
				keywordList.append(splitInput[i]);
			}
			
			// Now connect to the target and deliver the message.
			// We have to check whether the clients has a trapdoor for a certain keyword.
			if (_gateway.clientIsRegistered(target)) {
				Client client = _gateway.getClient(target);
				HashSet<Device> sendToDevices = new HashSet<Device>();
				boolean isSend = false;
				String messageToReceiver = IBEMessageProtocolCommands.MESSAGE + " " + source + " " + message + " " + keywordList.toString();
				for (String keyword : keywords) {
					if (client.trapdoorForKeywordExist(keyword)) {
						TrapdoorAction trapdoor = client.getTrapdoorForKeyword(keyword);
						HashSet<Device> devices = trapdoor.getTrapdoorDevices();
						for (Device device : devices) {
							if (!sendToDevices.contains(device)) {
								// Send the message to the device.
								device.send(messageToReceiver);
								sendToDevices.add(device);
							}
						}
						isSend = true;
					}
				}
				if (!isSend) {
					client.getDefaultDevice().send(messageToReceiver);
				}
			} else {
				return "Target client unknown";
			}
		}
		else if(cmd.equals(IBEMessageProtocolCommands.REGISTER)) {}
		else if(cmd.equals(IBEMessageProtocolCommands.UNREGISTER)) {}
		else if(cmd.equals(IBEMessageProtocolCommands.TRAPDOOR)) {
			cmd = splitInput[1];
			
			if(cmd.equals(IBEMessageProtocolCommands.ADD)) {}
			else if(cmd.equals(IBEMessageProtocolCommands.REMOVE)) {}
			else { return "unable to process input: unknown TRAPDOOR command"; }
		}
		else if(cmd.equals(IBEMessageProtocolCommands.DEVICE)) {
			cmd = splitInput[1];

			if(cmd.equals(IBEMessageProtocolCommands.ADD)) {}
			else if(cmd.equals(IBEMessageProtocolCommands.REMOVE)) {}
			else { return "unable to process input: unknown DEVICE command"; }
		}
		else { return "unable to process input: unknown input"; }
		return null;
	}
}
