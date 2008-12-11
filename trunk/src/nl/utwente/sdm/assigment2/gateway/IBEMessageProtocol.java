package nl.utwente.sdm.assigment2.gateway;

import java.util.HashSet;
import java.util.LinkedList;

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
	public String processInput(LinkedList<String> lines) {
		// Split the received message on the spaces.
		String input = lines.get(0);
		String[] splitInput = input.split(" ");
		// The first part is the command.
		String cmd = splitInput[0];
		
		if(cmd.equals(IBEMessageProtocolCommands.MESSAGE)) {
			// Handle the message command.
			String source = splitInput[1];
			String target = splitInput[2];
			int nrOfKeywords = new Integer(splitInput[3]).intValue();
			// Read the keywords from the inputstream.
			HashSet<String> keywords = new HashSet<String>();
			StringBuffer keywordList = new StringBuffer();
			int maxNr = (4+nrOfKeywords < splitInput.length) ? 4+nrOfKeywords : splitInput.length;
			for (int i=4; i<maxNr; ++i) {
				keywords.add(splitInput[i]);
				keywordList.append(splitInput[i]);
			}
			StringBuffer encryptedMessage = new StringBuffer();
			for (int i=1; i<lines.size(); ++i) {
				encryptedMessage.append(lines.get(i));
				if (i < (lines.size() - 1))
					encryptedMessage.append("\n");
			}
			
			// Now connect to the target and deliver the message.
			// We have to check whether the clients has a trapdoor for a certain keyword.
			if (_gateway.clientIsRegistered(target)) {
				Client client = _gateway.getClient(target);
				// Keep a list of devices which already received the message, so we can check wether a message is already send to a certain device.
				HashSet<Device> sendToDevices = new HashSet<Device>();
				boolean isSend = false;
				String messageToReceiver = IBEMessageProtocolCommands.MESSAGE + " " + source + "\n" + encryptedMessage;
				for (String keyword : keywords) {
					if (client.trapdoorForKeywordExist(keyword)) {
						TrapdoorAction trapdoor = client.getTrapdoorForKeyword(keyword);
						HashSet<Device> devices = trapdoor.getTrapdoorDevices();
						for (Device device : devices) {
							if (!sendToDevices.contains(device)) {
								// Send the message to the device.
								device.send(source, messageToReceiver);
								sendToDevices.add(device);
							}
						}
						isSend = true;
					}
				}
				if (!isSend) {
					Device defaultDevice = client.getDefaultDevice();
					defaultDevice.send(source, messageToReceiver);
				}
				return "Send";
			} else {
				return "Target client unknown";
			}
		}
		else if(cmd.equals(IBEMessageProtocolCommands.REGISTER)) {
			// Handle the register command.
			if (splitInput.length == 4) {
				String identity = splitInput[1];
				String address = splitInput[2];
				int port = new Integer(splitInput[3]).intValue();
				System.out.println("Received REGISTER message, identity=" + identity + ", address=" + address + " port=" + port);
				
				if (!_gateway.clientIsRegistered(identity)) {
					_gateway.registerClient(identity, address, port);
					System.out.println("Client " + identity + " is registered.");
					return "Client succesfully registered.";
				} else {
					Client registeredClient = _gateway.getClient(identity);
					if (registeredClient.hasDevice(address, port)) {
						System.out.println("Client " + identity + " already registered with this device and port.");
						return "Client already registered with this device and port.";
					} else {
						registeredClient.addDevice(address, port);
						System.out.println("Client " + identity + " already registered, but added device.");
						return "Client already registered, but added device.";
					}
				}
			} else {
				return "Messageformat incorrect: message contains an incorrect amount of arguments.";
			}
		}
		else if(cmd.equals(IBEMessageProtocolCommands.UNREGISTER)) {
			// Handle the unregister command.
			if (splitInput.length == 2) {
				String identity = splitInput[1];
				
				if (_gateway.clientIsRegistered(identity)) {
					_gateway.unregisterClient(identity);
				} else {
					return "Client is not registered, cannot unregister.";
				}
			} else {
				return "Message contains too much arguments.";
			}
		}
		else if(cmd.equals(IBEMessageProtocolCommands.TRAPDOOR)) {
			// Handle the trapdoor command.
			String subCmd = splitInput[1];
			String clientIdentity = splitInput[2];
			String hashedKeyword = splitInput[3];

			if(subCmd.equals(IBEMessageProtocolCommands.ADD)) {
				// Read the devices from the inputstream.
				if (_gateway.clientIsRegistered(clientIdentity)) {
					Client client = _gateway.getClient(clientIdentity);
					
					HashSet<Device> devices = new HashSet<Device>();
					for (int i=4; i<splitInput.length; ++i) {
						String address = splitInput[i];
						Integer portInteger = new Integer(splitInput[i+1]);
						int port = portInteger.intValue();
						if (client.hasDevice(address, port)) {
							devices.add(client.getDevice(address, port));
						} else {
							return "Device for client '" + clientIdentity + "' with address '" + address + ":" + port + "' does not exist.";
						}
						i++;
					}
					
					TrapdoorAction trapdoor = new TrapdoorAction(devices);
					_gateway.getClient(clientIdentity).addTrapdoor(hashedKeyword, trapdoor);
					return "Trapdoor succesfully added.";
				} else {
					return "Client with identity '" + clientIdentity + "' is not registered.";
				}
			}
			else if(subCmd.equals(IBEMessageProtocolCommands.REMOVE)) {
				if (_gateway.getClient(clientIdentity).trapdoorForKeywordExist(hashedKeyword)) {
					_gateway.getClient(clientIdentity).removeTrapdoor(hashedKeyword);
					return "Trapdoor succesfully removed.";
				} else {
					return "Trapdoor doesn't exist for the given keyword.";
				}
			}
			else {
				return "unable to process input: unknown TRAPDOOR command";
			}
		}
		else if(cmd.equals(IBEMessageProtocolCommands.DEVICE)) {
			cmd = splitInput[1];
			
			// Handle the device add command.
			if (splitInput.length == 3) {
				String clientIdentity = splitInput[1];
				if (!_gateway.clientIsRegistered(clientIdentity))
					return "Client with identity '" + clientIdentity + "' is not registered at this gateway.";
				
				String deviceAddress = splitInput[2];
				int devicePort = new Integer(splitInput[3]).intValue();
				if(cmd.equals(IBEMessageProtocolCommands.ADD)) {
					if (!_gateway.getClient(clientIdentity).hasDevice(deviceAddress, devicePort))
						_gateway.getClient(clientIdentity).addDevice(deviceAddress, devicePort);
					else
						return "Device with address '" + deviceAddress + ":" + devicePort + "' for client with identity '" + clientIdentity + "' is already registered at this gateway.";
				}
				else if(cmd.equals(IBEMessageProtocolCommands.REMOVE)) {
					if (_gateway.getClient(clientIdentity).hasDevice(deviceAddress, devicePort))
						_gateway.getClient(clientIdentity).removeDevice(deviceAddress, devicePort);
					else
						return "Device with address '" + deviceAddress + ":" + devicePort + "' for client with identity '" + clientIdentity + "' is not registered at this gateway.";
				}
				else { return "Unable to process input: unknown DEVICE command"; }
			} else {
				return "Message contains too much arguments.";
			}
		}
		else { return "unable to process input: unknown input"; }
		return null;
	}
}
