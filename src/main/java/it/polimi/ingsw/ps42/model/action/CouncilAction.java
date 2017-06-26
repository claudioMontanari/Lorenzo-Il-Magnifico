package it.polimi.ingsw.ps42.model.action;

import org.apache.log4j.Logger;

import it.polimi.ingsw.ps42.message.FamiliarUpdateMessage;
import it.polimi.ingsw.ps42.message.Message;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.Response;
import it.polimi.ingsw.ps42.model.exception.FamiliarInWrongPosition;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.player.Familiar;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.position.CouncilPosition;

public class CouncilAction extends Action {

	//Private Variables, the game logic must ask to the table the first free council position
	private CouncilPosition tablePosition;
	
	//Logger
	private transient Logger logger = Logger.getLogger(CouncilAction.class);

	public CouncilAction(ActionType type, Familiar familiar, CouncilPosition tablePosition) throws NotEnoughResourcesException{
		//Constructor for normal action
		super(type, familiar);
		this.tablePosition = tablePosition;
	}
	
	public CouncilAction(ActionType type, Player player, CouncilPosition tablePosition, int actionIncrement) throws NotEnoughResourcesException{
		//Constructor for bonus action
		super(type, player, 1, actionIncrement);
		this.tablePosition = tablePosition;
	}
	
	private boolean checkActionValue(){
		return actionValue >= tablePosition.getLevel();
	}
	@Override
	public Response checkAction() {
		
		if( player.canPlay() ){
			if(familiar != null) {		//If is a normal action check increase effect and position malus
				
				if(familiar.isPositioned())
					return Response.FAILURE;
				
				checkIncreaseEffect();
				addIncrement(-tablePosition.getMalus());
				if( !checkActionValue())
					return Response.LOW_LEVEL;
				else return Response.SUCCESS;
			}
			//If is a bonus action 
			else return Response.SUCCESS;
		}
		else return Response.CANNOT_PLAY;
	}

	@Override
	public void doAction() {
		if(familiar == null)
			tablePosition.applyCouncilPositionBonus(player);
		else {
			try {
				tablePosition.setFamiliar(familiar);
			} catch (FamiliarInWrongPosition e) {
				logger.error("[DEBUG]: There is a wrong familiar in council Positions");
				logger.info(e);
			}
			Message familiarUpdate = new FamiliarUpdateMessage(player.getPlayerID(), familiar.getColor(), getType(), 0);
			setChanged();
			notifyObservers(familiarUpdate);
		}
		player.synchResource();
	}
}
