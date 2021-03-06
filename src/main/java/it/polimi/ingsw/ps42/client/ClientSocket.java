package it.polimi.ingsw.ps42.client;

import it.polimi.ingsw.ps42.message.GenericMessage;
import it.polimi.ingsw.ps42.view.GUI.GUIView;
import it.polimi.ingsw.ps42.view.View;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

/**
 * Class to load the Client, both from CLI and GUI
 * This class is used only for a Socket-type connection
 * 
 * @author Luca Napoletano, Claudio Montanari
 *
 */
public class ClientSocket extends Observable implements Observer{

	private Socket socket;
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private final static int PORT = 5555;
	private boolean isConnected = false;
	private final String host;
	
	//Logger
	private transient Logger logger = Logger.getLogger(ClientSocket.class);
	
	/**
	 * Constructor of the Socket Client
	 * 
	 * @param host						The IP address of the Server
	 * @throws UnknownHostException		Thrown if the path represent an Unknown IP
	 * @throws IOException				Thrown if there is a network error
	 */
	public ClientSocket(String host) throws UnknownHostException, IOException {
		
		this.host = host;
		this.socket = new Socket(host, PORT);
		writer = new ObjectOutputStream(socket.getOutputStream());
		writer.flush();
		reader = new ObjectInputStream(socket.getInputStream());
		isConnected = true;
		
		//Setting client logger property
		PropertyConfigurator.configure("Logger//Properties//client_log.properties");
	}
	
	/**
	 * Method used to add the View (GUI or CLI) as Observer of the ClientSocket
	 * and vice versa
	 * 
	 * @param view	The view to add
	 */
	public void addView(View view){

		view.addObserver(this);
		this.addObserver(view);
	}
	
	/**
	 * Method used to send a GenericMessage from the User to the Server
	 * 
	 * @param message		The message to send
	 * @throws IOException	Thrown if there is a network error, such as a connection closing
	 */
	public void send(GenericMessage message) throws IOException{
		
		try {
			writer.writeObject(message);
			writer.flush();
		}catch (IOException e) {
			logger.error("Error in sending the new message ");
			logger.info(e);
			isConnected = false;
			throw new IOException();
		}	
	}
	
	/**
	 * Method used to know if the connection is still active
	 * 
	 * @return	True if the Client is connected to the Server, otherwise False
	 */
	public boolean isConnected(){
		if(socket.isInputShutdown() || socket.isOutputShutdown())
			isConnected=false;
		return isConnected;
	}
	
	/**
	 * Method called to start to read messages from the socket.
	 * The client is in waiting of a message from the Server
	 */
	public void readMessage() {
		
		if(isConnected()){
			try{
				Object msg = reader.readObject();
				if(msg instanceof GenericMessage) {
					setChanged();
					notifyObservers((GenericMessage) msg);
				}
			} catch(IOException | ClassNotFoundException e){
				logger.error("Error in reading the input messages");
				logger.info(e);
				isConnected=false;
			}
		}
	}
	
	/**
	 * Method used to close the connection
	 * 
	 * @throws IOException	Thrown if there are some problems in closing
	 */
	public void close() throws IOException {
		logger.info("Closing the reader/writer for the socket");
		reader.close();
		writer.close();
	}
	
	/**
	 * Method that start the communication with the Server.
	 * It is used to read always a message from the socket
	 */
	public void startReading(){
		new Thread( new Runnable(){
			
				public void run(){
					logger.info("Starting Connection Thread in ClientSocket");
					while(isConnected()){
						readMessage();
					}
					
				}
			
		}).start();
			
	}
	
	/**
	 * Method used when the user give an input to send to the Server
	 */
	@Override
	public void update(Observable sender, Object message) {

		if(message instanceof GenericMessage){
			GenericMessage msg = (GenericMessage) message;
			try {
				this.send(msg);
			} catch (IOException e) {
				logger.error("Unknown type of message");
				logger.info(e);
			}
		}
		
	}
	
	/**
	 * Main method to start the ClientSocket Class
	 * 
	 * @throws UnknownHostException		Thrown if the {@code String host = "IP address"} is an Unknown IP address
	 * @throws IOException				Thrown if there is a network problem
	 */
	public void run() throws UnknownHostException, IOException {

		ClientSocket client = new ClientSocket(host);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					View view = new GUIView();
					client.addView(view);
					client.startReading();
				} catch (IOException e) {
					logger.fatal("Unable to load the Graphical User Interface");
					logger.info(e);
				}
			}
		});

	}

}
