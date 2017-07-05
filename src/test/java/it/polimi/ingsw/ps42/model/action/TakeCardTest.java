package it.polimi.ingsw.ps42.model.action;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polimi.ingsw.ps42.message.CardRequest;
import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.StaticList;
import it.polimi.ingsw.ps42.model.effect.DoAction;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.FamiliarColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.enumeration.Response;
import it.polimi.ingsw.ps42.model.exception.FamiliarInWrongPosition;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.position.TowerPosition;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class TakeCardTest {
	
	private Player p1;
	Action takeCardAction;
	StaticList<TowerPosition> tower;
	
	@BeforeClass
	public static void classSetUp() {
		PropertyConfigurator.configure("Logger//Properties//test_log.properties");
	}

	@Before
	public void setUp() throws Exception {
		//Create the player with only 5 money
		p1 = new Player("Player 1");
		Packet resource = new Packet();
		resource.addUnit(new Unit(Resource.MONEY, 3));
		p1.increaseResource(resource);
		p1.synchResource();
		//Create one tower
		//With the lasts 2 floor with an 2 money obtain effect, the others empty
		Packet packet = new Packet();
		packet.addUnit(new Unit(Resource.MONEY, 2));
		Obtain bonus = new Obtain(null, packet, null);
		
		TowerPosition first = new TowerPosition(ActionType.TAKE_GREEN, 1, null, 0);
		TowerPosition second = new TowerPosition(ActionType.TAKE_GREEN, 3, null, 0);
		TowerPosition third = new TowerPosition(ActionType.TAKE_GREEN, 5, bonus, 0);
		TowerPosition fourth = new TowerPosition(ActionType.TAKE_GREEN, 7, bonus, 0);
		
		tower = new StaticList<>(4);
		tower.add(first);
		tower.add(second);
		tower.add(third);
		tower.add(fourth);
		
		//Set a familiar to play with value 5
		p1.setFamiliarValue(FamiliarColor.ORANGE, 5);
		
		//Create costs for the card
		Packet cost1 = new Packet();
		cost1.addUnit(new Unit(Resource.MONEY, 2));
		cost1.addUnit(new Unit(Resource.MILITARYPOINT, 2));
		
		Packet cost2 = new Packet();
		cost2.addUnit(new Unit(Resource.MONEY, 2)); 
		
		List<Packet> costs = new ArrayList<>();
		costs.add(cost1);
		costs.add(cost2);
		
		//Create one immediate effect for the card
		Packet effect1 = new Packet();
		effect1.addUnit(new Unit(Resource.MONEY, 2));
		Obtain immediateEffect1 = new Obtain(null, effect1, null);
		
		Packet effect2 = new Packet();
		effect2.addUnit(new Unit(Resource.MILITARYPOINT, 2));
		Obtain immediateEffect2 = new Obtain(effect2, effect2, null);
		
		List<Effect> immediateEffects = new ArrayList<>();
		immediateEffects.add(immediateEffect1);
		immediateEffects.add(immediateEffect2);
		
		//Create a immediate effect for the third card (bonus action with a discount)
		List<Effect> secondImmediateEffects = new ArrayList<>();
		Packet discount = new Packet();
		discount.addUnit(new Unit(Resource.WOOD, 5));
		secondImmediateEffects.add(new DoAction(ActionType.TAKE_GREEN, 5, discount, null));
		
		//Create the cards
		//FistCard: cost: 2Money+2Wood OR 2Money; immEffect: +2Money OR +2MilitaryPoint.
		Card c = new Card("Card", "", CardColor.GREEN, 1, 3, costs, immediateEffects, null, null, null);
		//SecondCard: cost: 0; effect: none.
		Card useless = new Card("", "", CardColor.GREEN, 1, 3, null, null, null, null, null);
		//ThirdCard: cost: 0; immEffect: BonusAction(TakeGreen val 5, discount: 0)
		Card discountedCard = new Card("card3", "Card with discount", CardColor.GREEN, 1, 2, null, secondImmediateEffects, null, null, null);
		
		//Add cards to tower
		first.setCard(discountedCard);
		second.setCard(useless);
		third.setCard(c);
		fourth.setCard(useless);	

	}

	@Test
	public void test() {
		
		try {
			assertEquals(3, p1.getResource(Resource.MONEY));
			//Take the first card, you will receive a bonus action take green of level 5
			takeCardAction = new TakeCardAction(ActionType.TAKE_GREEN, p1.getFamiliar(FamiliarColor.ORANGE), tower, 0);
			Response checker = takeCardAction.checkAction();
			assertTrue(checker == Response.SUCCESS);
		} catch (NotEnoughResourcesException e) {
			System.out.println("Player hasn't enough resources");
		}
		assertEquals(0, p1.getRequests().size());
		
		try {
			takeCardAction.doAction();
			p1.synchResource();
			assertEquals(3, p1.getResource(Resource.MONEY));
			//Now player has a BonusAction to perform
			ActionPrototype bonusAction = p1.getBonusAction();
			takeCardAction = new TakeCardAction(ActionType.TAKE_GREEN, p1, tower, 2, 5, 0);
			takeCardAction.addDiscount(bonusAction.getDiscount());
			assertTrue(bonusAction.checkAction(takeCardAction));
		} catch (FamiliarInWrongPosition | NotEnoughResourcesException e) {
		System.out.println("ERROR");
		}
		 
		Response checker = takeCardAction.checkAction();
		assertEquals(checker , Response.SUCCESS);
		List<CardRequest> requests = p1.getRequests();
		
		//Control request
		if(!requests.isEmpty()) {
			//Now there is one request with only one cost
			//Player can enable it
			assertEquals(1, requests.size());
			for(CardRequest request : requests) {
				//Control if there is only one possible cost
				assertEquals(1, request.showChoice().size());
				request.setChoice(0);
			}
		}
		//Here player has two money from the bonus position 
		assertEquals(3, p1.getResource(Resource.MONEY));
		try {
			takeCardAction.doAction();
			//Now player has 2 money, he payed two money for the card but he has earned two money from the position
			assertEquals(3, p1.getResource(Resource.MONEY));
		} catch (FamiliarInWrongPosition e) {
			System.out.println("ERROR");
		}
	}
	
}
