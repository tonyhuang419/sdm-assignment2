package nl.utwente.sdm.assigment2.client;

/**
 * The Main class for the Client.
 * @author Harmen
 */
public class Client {

	/**
	 * @param args The first argument is the identity of the user.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("There should only be one argument, which is the identity of the client.");
		} else {
			String identity = args[0].toString();
			System.out.println("Identity to start with: " + identity);
		}
	}

}
