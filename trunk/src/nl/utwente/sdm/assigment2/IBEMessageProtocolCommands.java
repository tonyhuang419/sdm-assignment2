package nl.utwente.sdm.assigment2;

/**
 * This class contains the used command for the IBEMessageProtocol.
 * This can either be used by the server and the client.
 * @author Harmen
 */
public final class IBEMessageProtocolCommands {
	public static final String GETGLOBALS = "GETGLOBALS";
	/** Command send to the keyserver to authenticate and get the private key. */
	public static final String AUTHENTICATE = "AUTHENTICATE";
	
	/** Command to send a message. */
	public static final String MESSAGE = "MESSAGE";
	/** Command to register a client. */
	public static final String REGISTER = "REGISTER";
	/** Command to unregister a client. */
	public static final String UNREGISTER = "UNREGISTER";
	/** Command to perform a trapdoor action. */
	public static final String TRAPDOOR = "TRAPDOOR";
	/** Command to perform a device action. */
	public static final String DEVICE = "DEVICE";
	
	/** Command to perform a add action. */
	public static final String ADD = "ADD";
	/** Command to perform a remove action. */
	public static final String REMOVE = "REMOVE";
	
	public static final String END_OF_MESSAGE = "<<EOM";
}
