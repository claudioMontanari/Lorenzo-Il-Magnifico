package it.polimi.ingsw.ps42.model.effect;

import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.EffectType;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class CardForEachObtain extends Effect{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7012927820219406173L;
	private CardColor color;
	private Packet gains;
	
	public CardForEachObtain() {
		super();
	}
	public CardForEachObtain(CardColor color, Packet gains) {
		super(EffectType.CARD_FOR_EACH_OBTAIN);
		this.color = color;
		this.gains = gains;
	}

	@Override
	public void enableEffect(Player player) {
		//For each card player has in his arraylist, he gains the "gains" packet in this object
		
		logger.info("Effect: " + this.getTypeOfEffect() + " activated");
		
		this.player = player;
		
		int cardQuantity = player.getCardList(color).size();
		int quantityToObtain;
		Packet giveToPlayer = new Packet();
		
		for(Unit gain : gains) {
			quantityToObtain = gain.getQuantity() * cardQuantity;
			giveToPlayer.addUnit(new Unit(gain.getResource(), quantityToObtain));
		}
		this.player.increaseResource(giveToPlayer);		
	}

	@Override
	public Effect clone() {
		if(gains != null)
			return new CardForEachObtain(this.color, this.gains);
		else
			return null;
	}
	
	@Override
	public String print() {
		return "For each " + this.color + " card in player, he gains " + this.gains.toString();
	}

}
