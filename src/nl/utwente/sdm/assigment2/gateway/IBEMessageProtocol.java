package nl.utwente.sdm.assigment2.gateway;

import java.util.HashSet;
import java.util.Iterator;

public class IBEMessageProtocol {
	private Gateway _gateway;
	
	public IBEMessageProtocol() {
		_gateway = Gateway.getGateway();
	}
	
	public String processInput(String input) {
		String[] splitInput = input.split(" ");
		String cmd = splitInput[0];
		
		if(cmd.equals("MESSAGE")) {
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
				for (String keyword : keywords) {
					if (client.trapdoorForKeywordExist(keyword)) {
						TrapdoorAction trapdoor = client.getTrapdoorForKeyword(keyword);
						HashSet<Device> devices = trapdoor.getTrapdoorDevices();
						for (Device device : devices) {
							if (!sendToDevices.contains(device)) {
								// Send the message to the device.
								device.send("MESSAGE " + source + " " + message + " " + keywordList.toString());
								sendToDevices.add(device);
							}
						}
						isSend = true;
					}
				}
				if (!isSend) {
					client.getDefaultDevice().send("MESSAGE " + source + " " + message + " " + keywordList.toString());
				}
			} else {
				return "Target client unknown";
			}
		}
		else if(cmd.equals("REGISTER")) {}
		else if(cmd.equals("UNREGISTER")) {}
		else if(cmd.equals("TRAPDOOR")) {
			cmd = splitInput[1];
			
			if(cmd.equals("ADD")) {}
			else if(cmd.equals("REMOVE")) {}
			else { return "unable to process input: unknown TRAPDOOR command"; }
		}
		else if(cmd.equals("DEVICE")) {
			cmd = splitInput[1];

			if(cmd.equals("ADD")) {}
			else if(cmd.equals("REMOVE")) {}
			else { return "unable to process input: unknown DEVICE command"; }
		}
		else { return "unable to process input: unknown input"; }
		return null;
	}
}
