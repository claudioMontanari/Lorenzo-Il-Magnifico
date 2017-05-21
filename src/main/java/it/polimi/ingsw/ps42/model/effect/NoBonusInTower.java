package it.polimi.ingsw.ps42.model.effect;

import it.polimi.ingsw.ps42.model.enumeration.EffectType;
import it.polimi.ingsw.ps42.model.player.Player;

public class NoBonusInTower extends Effect{
	//This method set a player variable to disable the Tower Bonus

	public NoBonusInTower(EffectType typeOfEffect) {
		super(typeOfEffect);
		
	}

	@Override
	public void enableEffect(Player player) {
		
		
	}

}
