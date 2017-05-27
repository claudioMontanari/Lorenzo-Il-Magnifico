package it.polimi.ingsw.ps42.model.action;

import java.util.List;

import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.Response;
import it.polimi.ingsw.ps42.model.player.Familiar;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.position.Position;
import it.polimi.ingsw.ps42.model.position.YieldAndProductPosition;

public class YieldAction extends Action {

	private List<YieldAndProductPosition> tablePosition;
	private YieldAndProductPosition firstPosition;
	
	
	public YieldAction(ActionType type, Familiar familiar, List<YieldAndProductPosition> tablePosition, YieldAndProductPosition firstPosition){
		//Constructor for normal action
		super(type, familiar);
		this.tablePosition = tablePosition;
		this.firstPosition = firstPosition;
	}
	public YieldAction(ActionType type, Player player, List<YieldAndProductPosition> tablePosition, int actionValue){
		//Constructor for bonus action
		super(type, player, actionValue);
		this.tablePosition = tablePosition;
		this.firstPosition = firstPosition;
	}
	@Override
	public Response checkAction() {
		// TODO Auto-generated method stub
		return Response.FAILURE;
	}

	@Override
	public void doAction() {
		// TODO Auto-generated method stub
		
	}

}
