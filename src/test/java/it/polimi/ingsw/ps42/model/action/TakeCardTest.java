package it.polimi.ingsw.ps42.model.action;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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

/**
 * This class tests the TakeCardAction, so it tries to create different kinds of TakeCard actions
 * and verify that the checkAction() and doAction() methods are right
 * 
 * @author Luca Napoletano, Claudio Montanari
 *
 */
public class TakeCardTest {
	
	private Player p1;
	private Player p2;
	private Action takeCardAction;
	private StaticList<TowerPosition> tower;
	
	private static Logger logger;
	
	@BeforeClass
	public static void classSetUp() {
		PropertyConfigurator.configure("Logger//Properties//test_log.properties");

		logger = Logger.getLogger(TakeCardTest.class);
	}

	@Before
	public void setUp() throws Exception {
		//Create the player with only 3 money
		p1 = new Player("Player 1");
		Packet resource = new Packet();
		resource.addUnit(new Unit(Resource.MONEY, 3));
		p1.increaseResource(resource);
		p1.synchResource();
		
		//Create one tower
		//With the lasts 2 floor with a 2 money obtain effect, the others empty
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
		cost2.addUnit(new Unit(Resource.MONEY, 3)); 
		
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
		discount.addUnit(new Unit(Resource.STONE, 5));
		secondImmediateEffects.add(new DoAction(ActionType.TAKE_GREEN, 5, discount, null));
		
		//Create the cards
		//FistCard: cost: 2Money+2Military OR 3Money; immEffect: +2Money OR +2MilitaryPoint.
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

	/**
	 * Tests two action: with the first action the player takes a green card, from the first position, for free and obtains a bonus action, so then
	 * tests the action obtained 
	 */
	@Test
	public void test() {
		
		assertEquals(3, p1.getResource(Resource.MONEY));
		takeCardAction = new TakeCardAction(ActionType.TAKE_GREEN, p1.getFamiliar(FamiliarColor.ORANGE), tower, 0);
		Response checker = takeCardAction.checkAction();
		assertTrue(checker == Response.SUCCESS);
		assertEquals(0, p1.getRequests().size());
		
		//Do the action and later verify the player resources and if the player received the bonus action
		try {
			takeCardAction.doAction();
			p1.synchResource();
			assertEquals(3, p1.getResource(Resource.MONEY));
			//Now player has a BonusAction to perform, so he tries to a take the third green card  
			ActionPrototype bonusAction = p1.getBonusAction();
			takeCardAction = new TakeCardAction(ActionType.TAKE_GREEN, p1, tower, 2, 5, 0);
			takeCardAction.addDiscount(bonusAction.getDiscount());
			assertTrue(bonusAction.checkAction(takeCardAction));
			p1.getRequests();
		} catch (FamiliarInWrongPosition e) {
		System.out.println("ERROR");
		}
		
		//Check the new action given by the bonus action effect (must verify the action prototype first)
		checker = takeCardAction.checkAction();
		assertEquals(checker , Response.SUCCESS);
		List<CardRequest> requests = p1.getRequests();
		
		//Control request since there are two costs to choose from (2Money+2Wood OR 3Money) but only the second affordable
		if(!requests.isEmpty()) {
			//Now there is one request with only one cost
			//Player can enable it
			assertEquals(1, requests.size());
			for(CardRequest request : requests) {
				//Control if there is only one possible cost
				assertEquals(1, request.showChoice().size());
				request.setChoice(0);
				p1.addRequest(request);
			}
		}
		
		try {
			takeCardAction.doAction();
			p1.synchResource();
			//Now player has 2 money, he payed two money for the card but he has earned two money from the position
			assertEquals(0 , p1.getResource(Resource.MONEY));
		} catch (FamiliarInWrongPosition e) {
			System.out.println("ERROR");
		}
	}
	
	/**
	 * Method used to test when the player cannot play
	 */
	@Test
	public void negativeTest1() {
		
		p2 = new Player("P2");
		p2.setCanPlay(false);
		
		takeCardAction = new TakeCardAction(ActionType.TAKE_GREEN, p2.getFamiliar(FamiliarColor.ORANGE), tower, 0);
		assertTrue(Response.CANNOT_PLAY == takeCardAction.checkAction());
	}
	
	/**
	 * Method used to test if the player cannot do an action because his familiar value is too low
	 */
	@Test
	public void negativeTest2() {
		
		p2 = new Player("P2");
		
		takeCardAction = new TakeCardAction(ActionType.TAKE_GREEN, p2.getFamiliar(FamiliarColor.ORANGE), tower, 0);
		assertTrue(Response.FAILURE == takeCardAction.checkAction());

	}
	
	/**
	 * Player p1 tries to take a green card when he hasn't enough military points so then test the rollBack of the action
	 */
	@Test
	public void negativeTest3() {
		
		//Create a green card to add 2 times to the player
		Card uselessCard = new Card("Useless Card", "", CardColor.GREEN, 1, 1, null, null, null, null, null);
		
		assertEquals(3, p1.getResource(Resource.MONEY));
		p1.addCard(uselessCard);
		p1.addCard(uselessCard);
		
		//Control how many cards the player has
		assertEquals(2, p1.getCardList(CardColor.GREEN).size());
		
		takeCardAction = new TakeCardAction(ActionType.TAKE_GREEN, p1.getFamiliar(FamiliarColor.ORANGE), tower, 0);
		assertTrue(Response.FAILURE == takeCardAction.checkAction());
		
		//Try to rollback the action and check player resources has not changed
		takeCardAction.rollBackAction();
		assertEquals(3, p1.getResource(Resource.MONEY));
		
	}
	
	@Test
	public void negativeTest4(){
		
		Packet temp = new Packet();
		temp.addUnit(new Unit(Resource.MONEY, 3));
		//Decrease the resources of the player so that he cannot afford the third green card
		try {
			p1.decreaseResource(temp);
		
		} catch (NotEnoughResourcesException e) {
			logger.error("Problems in decreasing resources for negative test 4");
			logger.info(e);
		}
		p1.synchResource();
		assertEquals(0, p1.getResource(Resource.MONEY));
		TakeCardAction action = new TakeCardAction(ActionType.TAKE_GREEN, p1.getFamiliar(FamiliarColor.ORANGE), tower, 2);
		
		//Check an action you cannot afford
		assertEquals( Response.LOW_LEVEL ,action.checkAction());
		
		assertEquals(0, p1.getResource(Resource.MONEY));
		
	}
	
}
