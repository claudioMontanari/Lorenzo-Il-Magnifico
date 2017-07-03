package it.polimi.ingsw.ps42.model.effect;

import org.apache.log4j.Logger;

import it.polimi.ingsw.ps42.model.enumeration.EffectType;
import it.polimi.ingsw.ps42.model.player.Player;

public class NoMarketBan extends Effect {
	//Disable the Market for the player with the boolean variable setted to false

	/**
	 * 
	 */
	private static final long serialVersionUID = -5719113500926713271L;
	
	/**
	 * Simple constructor of this effect
	 */
	public NoMarketBan() {
		
		super(EffectType.NO_MARKET_BAN);
	}

	/**
	 * Method used to enable this effect
	 */
	@Override
	public void enableEffect(Player player) {
		logger = Logger.getLogger(NoMarketBan.class);
		logger.info("Effect: " + this.getTypeOfEffect() + " activated");
		this.player=player;
		player.setNoMarketBan();
		
	}

	/**
	 * Method used to print this effect in View
	 */
	@Override
	public String print() {
		return "Player can't position his familiars in market";
	}

	/**
	 * Method used to copy this effect
	 */
	@Override
	public NoMarketBan clone() {
		return new NoMarketBan();
	}
}
