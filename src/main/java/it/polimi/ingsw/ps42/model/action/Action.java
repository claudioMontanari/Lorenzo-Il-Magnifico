package it.polimi.ingsw.ps42.model.action;

import java.util.ArrayList;

import it.polimi.ingsw.ps42.model.enumeration.ActionType;

public abstract class Action {		
	/*Class for basic action, requires the implementation of checkAction,
	* doAction, createRequest
	*/
	private ActionType type;
	private Familiar familiar;
	private Player player;
	private int positionValue;
	private ArrayList<Position> tableLocation;
	private Packet discount;
	
	
	public Action(ActionType type, Player player, Familiar familiar,ArrayList<Position> tablePosition){
		//Constructor for normal action
	}
	public Action(ActionType type, Player player, Familiar familiar,ArrayList<Position> tablePosition){
		//Constructor for bonus action (no familiar involved) 
		
	}
	
	public abstract void checkAction();		//Does all the required checks before the action is applicated 
	
	public abstract void doAction();		//Apply the player action 
	
	public void modifyActionValue(int value){		//Increments the value of the action 
		
		
	}
	
	private void checkIncreaseEffect(){			//Checks if the player has some increase effects active and apply them
		
		
	}
	
	public void setDiscount(Packet discount) {		//Adds a discount at the cost of the action
		this.discount = discount;
	}
	
	public abstract void createRequest();		//Create a request to better define the action and put it in the player 
	
	
	
	
}
