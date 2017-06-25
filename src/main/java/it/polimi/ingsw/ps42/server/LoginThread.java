package it.polimi.ingsw.ps42.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import it.polimi.ingsw.ps42.message.LoginMessage;
import it.polimi.ingsw.ps42.model.exception.ElementNotFoundException;

public class LoginThread implements Runnable{
	
	//Variables used to control and add the player
	private Server server;
	private Socket socket;
	
	//Object used to send and receive messages from the client
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	//Logger
	private transient Logger logger = Logger.getLogger(LoginThread.class);
	
	public LoginThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		
		//Initialize the input/output streamer
		try {
			
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			
			input = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			logger.error("Error in input/output socket creation");
			socketClose();
		}
	}
	
	private void socketClose() {
		try {
			this.socket.close();
			logger.info("Closing socket: " + socket.toString());
		} catch (IOException e) {
			logger.error("Error in socket closing");
		}
	}
	
	/**
	 * Method used to verify the correctness of the client message
	 * @param message		The login message send by the view
	 * @return				True if the message the chosen user name isn't used yet, false in other cases
	 * @throws IOException	If there is a connection problem
	 */
	private boolean correctMessage(LoginMessage message) throws IOException {
		String playerID = message.getUserName();
		
		if(server.existAnotherPlayer(playerID) && !server.playerWasPlaying(playerID)) {
			message.playerIdYetUsed();
			output.writeObject(message);
			return false;
		}
		output.writeObject(message);
		return true;
	}
	
	@Override
	public void run() {
		//Wait for a client user name and control it
		try {
			logger.info("Start to run a login thread to wait the player's user name");
			LoginMessage message = null;
			//Wait for the client message
			do {
				
				Object inputObject = input.readObject();
				
				if(inputObject instanceof LoginMessage)
					message = (LoginMessage) inputObject;
				
				logger.info("Received a user login");
				
			}while(message == null || !correctMessage(message));
			
			//Add player to the client
			server.addPlayer(message.getUserName(), socket, input, output);
		}
		catch (IOException e) {
			logger.error("Unable to read from the socket");
		} catch (ClassNotFoundException e) {
			logger.error("Unable to find the correct class");
		} catch (ElementNotFoundException e) {
			logger.error("Unable to add the player to a match");
		}
	}

}
