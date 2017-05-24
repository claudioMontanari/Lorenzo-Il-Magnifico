package it.polimi.ingsw.ps42.model.action;

import java.util.List;

import it.polimi.ingsw.ps42.model.Card;

public class Request {
	/*Class for asking a request to the player, 
	 * e.g. choose between two effects or two costs or
	 * if wants to pay for a effect
	 */
	private Card card;
	private int choice;
	private List<Integer> possibleChoice;
	
	public Request(Card card, List<Integer> possibleChoice){
		
	}
	
	public void setChoice(int choice) {
		this.choice = choice;
	}
	
	public int getChoice() {
		return choice;
	}
}
