package it.polimi.ingsw.ps42.model.effect;

import org.apache.log4j.Logger;

import it.polimi.ingsw.ps42.model.enumeration.EffectType;
import it.polimi.ingsw.ps42.model.player.Player;

public class CanPositioningEverywhereLeader extends Effect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5946490102125140634L;
	
	private transient Logger logger = Logger.getLogger(CanPositioningEverywhereLeader.class);

	public CanPositioningEverywhereLeader() {
		super(EffectType.CAN_POSITIONING_EVERYWHERE);
	}
	
	@Override
	public void enableEffect(Player player) {
		logger.info("Effect: " + this.getTypeOfEffect() + " activated");
		player.setCanPositioningEverywhere();		
	}

	@Override
	public Effect clone() {
		return this;
	}

	@Override
	public String print() {
		return getTypeOfEffect().toString();
	}


}
