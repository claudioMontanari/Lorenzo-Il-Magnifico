package it.polimi.ingsw.ps42.model.action;


import java.util.List;

import it.polimi.ingsw.ps42.model.StaticList;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.FamiliarColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.enumeration.Response;
import it.polimi.ingsw.ps42.model.exception.FamiliarInWrongPosition;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.player.Familiar;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.position.TowerPosition;
import it.polimi.ingsw.ps42.model.request.RequestInterface;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class TakeCardAction extends Action{
	
	private StaticList<TowerPosition> tablePosition;
	private int positionInTableList;

	public TakeCardAction(ActionType type, Familiar familiar, StaticList<TowerPosition> tablePosition, int positionInTableList) throws NotEnoughResourcesException{
		//Constructor for normal action
		super(type, familiar);
		this.tablePosition = tablePosition;
		this.positionInTableList = positionInTableList;
	}
	public TakeCardAction(ActionType type, Player player, StaticList<TowerPosition> tablePosition, int positionInTableList, int actionValue){
		//Constructor for bonus action
		super(type, player, actionValue);
		this.tablePosition = tablePosition;
		this.positionInTableList = positionInTableList;
	}
	
	@Override
	public Response checkAction() {
		//Initial checks for the takeCard action, valid for both normal and bonus action
		
		//First: Check if the player can play
		if(!player.canPlay())
			return Response.CANNOT_PLAY;
		
		//Second: Active the IncreaseEffect in player, control the ban of the tower bonus position
		checkIncreaseEffect();
		
		//Third: Check if the position is free, if there aren't other familiar with
		//the same player, if there isn't the card, if the familiar can't stay in that position
		//and if the player has the requirements of the chosen card
		//Be careful to the neutral familiar
		
		//Take the chosen position
		TowerPosition position = tablePosition.get(positionInTableList);
		
		if(!position.isEmpty())
			return Response.FAILURE;
		
		if(!checkMyFamiliar())
			return Response.FAILURE;
		
		actionValue = actionValue - position.getMalus();
		
		if(position.getLevel() > actionValue)
			return Response.FAILURE;
		
		if(!position.hasCard())
			return Response.FAILURE;
		
		//Fourth: if the position has a bonus, apply it to the player
		if(position.getBonus() != null && familiar != null) {
			try {
				position.setFamiliar(familiar);
			} catch (FamiliarInWrongPosition e) {
				System.out.println("[DEBUG]: familiar can't be positioned here");
				return Response.FAILURE;
			}
		}
		
		//Fifth: verify if there aren't any other player in the tower, else
		//decrease money in player
		if(isAnotherFamiliar()) {
			Packet moneyMalus = new Packet();
			moneyMalus.addUnit(new Unit(Resource.MONEY, 3));
			try {
				player.decreaseResource(moneyMalus);
			} catch (NotEnoughResourcesException e) {
				player.restoreResource();
				position.removeFamiliar();
				return Response.LOW_LEVEL;
			}
		}
		try {
			position.getCard().payCard(player, discount);
		} catch (NotEnoughResourcesException e) {
			player.restoreResource();
			position.removeFamiliar();
			return Response.LOW_LEVEL;
		}
		return Response.SUCCESS;
	}

	@Override
	public void doAction() {
		/*	Method used to apply an Action
		 * 	In this case, control if there is a request in player and apply it
		 * 	Then do some operation for setting the card to the player 
		 * 	Finally, synch all the player resources to apply all the changes 
		 */
		
		List<RequestInterface> requests = player.getRequests();
		if(requests != null && !requests.isEmpty()) {
			for(RequestInterface request : requests)
				request.apply();
		}
		
		TowerPosition position = tablePosition.get(positionInTableList);
		position.getCard().setPlayer(player);
		player.addCard(position.getCard());
		player.synchResource();
		//TODO position.getCard().enableImmediateEffect();
		position.removeCard();
		
	}
	
	private boolean checkMyFamiliar() {
		for(TowerPosition tower : tablePosition) {
			if(tower.getFamiliar().getPlayer() == player) {
				if(familiar.getColor() != FamiliarColor.NEUTRAL)
					return false;
			}
		}
		return true;
	}
	
	private boolean isAnotherFamiliar() {
		for(TowerPosition tower : tablePosition) {
			if(!tower.isEmpty() && tower.getFamiliar().getPlayer() != player) {
				return true;
			}
		}
		return false;
	}
}
