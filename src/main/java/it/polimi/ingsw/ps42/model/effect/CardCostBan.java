package it.polimi.ingsw.ps42.model.effect;

import java.util.List;

import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.StaticList;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.EffectType;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.exception.WrongColorException;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class CardCostBan extends Effect{
	//At the end of the match, the gamelogic calculate the cost of woods and stones of the indicated cards
	//and remove victory points from the player resources
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7122058016852083837L;
	private CardColor color;

	public CardCostBan(CardColor color) {
		super(EffectType.YELLOW_COST_BAN); //TO-DO:discutere sul nome effetto e controllo sul colore
		this.color=color;
	}

	@Override
	public void enableEffect(Player player) {
		this.player=player;
		int banCost=0;
		try{
			StaticList<Card> deck = player.getCardList(color);	
			for (Card singleCard : deck) {							//For each card of the player with the def. color
				List<Packet> costs=singleCard.getCosts();		//Obtain for every cost the quantity of wood and stone
				for (Packet singleCost : costs) {
					banCost+=defineCost(singleCost);
				}
			}
			Unit u=new Unit(Resource.VICTORYPOINT, banCost);	//Convert the total amount in victory point to be subtracted later
			Packet p=new Packet();
			p.addUnit(u);
			player.decreaseResource(p); 						//Apply the ban to the player
		}
		catch (WrongColorException e) {
			System.out.println("Ban failed beacause of a wrong initialization of the effect");
		} catch (NotEnoughResourcesException e) {
			player.setToZero(Resource.VICTORYPOINT);
		}
	}
	
	private int defineCost (Packet cost){
		//Defines the single cost of a single card in terms of Woods and Stones
		List<Unit> tempUnit = cost.getPacket();
		int quantity=0;
		for (Unit singleUnit : tempUnit) {
			if(singleUnit.getResource()==Resource.WOOD || singleUnit.getResource()==Resource.STONE){
				quantity+=singleUnit.getQuantity();
			}
		}
		return quantity;
		
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CardCostBan clone() {
		CardColor cloneColor = this.color;
		return new CardCostBan(cloneColor);
	}

}
