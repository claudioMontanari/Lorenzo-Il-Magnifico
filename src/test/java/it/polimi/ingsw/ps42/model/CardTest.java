package it.polimi.ingsw.ps42.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polimi.ingsw.ps42.message.CardRequest;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.effect.ForEachObtain;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class CardTest {
	
	private Packet cost1;
	private Packet cost2;
	
	private ArrayList<Packet> costs;
	
	private Packet requirement1;
	private Packet requirement2;
	
	private ArrayList<Packet> requirements;
	
	private Effect immediateEffect1;
	private Effect immediateEffect2;
	
	private ArrayList<Effect> immediateEffects;
	
	//Player with exactly resources to take the card
	private Player canTakeCard;
	
	//Player cannot afford the card cost
	private Player cannotPay;
	
	//Player cannot afford the card requirements
	private Player hasNotRequirements;
	
	//Player cannot enable an effect
	private Player cannotEnableObtainEffect;
	
	//The cards created for testing
	private Card firstCard;
	private Card secondCard;
	private Card thirdCard;

	@BeforeClass
	public static void classSetUp() {
		PropertyConfigurator.configure("Logger//Properties//test_log.properties");
	}
	
	@Before
	public void setUp() throws Exception {
		//Create possible Packet/Effect arrays for various card
		
		//Create the 4 players
		canTakeCard = new Player("I can take all the card");
		cannotPay = new Player("I can't pay the card");
		hasNotRequirements = new Player("I can't take the card because I haven't the requirements");
		cannotEnableObtainEffect = new Player("I've not enough resources to enable the card effect");
		
		assignResources();
		
		createRequirements();
		
		createCost();
		
		createImmediateEffects();
		
		
	}
	
	private void assignResources() {
		//Resources for the player
			Packet moneyForFirstPlayer = new Packet();
			moneyForFirstPlayer.addUnit(new Unit(Resource.MONEY, 5));
		
			Packet money = new Packet();
			money.addUnit(new Unit(Resource.MONEY, 3));
			
			Packet wood = new Packet();
			wood.addUnit(new Unit(Resource.WOOD, 3));
			
			Packet stone = new Packet();
			stone.addUnit(new Unit(Resource.STONE, 3));
			
			Packet faithPoint = new Packet();
			faithPoint.addUnit(new Unit(Resource.FAITHPOINT, 1));
			
			//Players resources
			//First has 3 wood, 3 money and 3 stone
			canTakeCard.increaseResource(moneyForFirstPlayer);
			canTakeCard.increaseResource(stone);
			canTakeCard.increaseResource(wood);
			canTakeCard.synchResource();
			
			//Second has 3 wood and 3 stone
			cannotPay.increaseResource(wood);
			cannotPay.increaseResource(stone);
			cannotPay.synchResource();
			
			//Third has 3 money, 3 stone
			hasNotRequirements.increaseResource(stone);
			hasNotRequirements.increaseResource(money);
			hasNotRequirements.synchResource();
			
			//Fourth has 3 money, 3 stone, 3 wood and 1 faithPoint
			cannotEnableObtainEffect.increaseResource(money);
			cannotEnableObtainEffect.increaseResource(stone);
			cannotEnableObtainEffect.increaseResource(wood);
			cannotEnableObtainEffect.increaseResource(faithPoint);
			cannotEnableObtainEffect.synchResource();
			
	}
	
	private void createCost() {
		//Create 2 possible costs
			cost1 = new Packet();
			cost1.addUnit(new Unit(Resource.STONE, 3));
			cost1.addUnit(new Unit(Resource.MONEY, 2));
			
			cost2 = new Packet();
			cost2.addUnit(new Unit(Resource.WOOD, 3));
			cost2.addUnit(new Unit(Resource.MONEY, 2));
			
			//Create card costs
			costs = new ArrayList<>();
			costs.add(cost1);
			costs.add(cost2);
	}
	
	private void createRequirements() {
		//The first requirement is verified only for the canTakeCard player
		requirement1 = new Packet();
		requirement1.addUnit(new Unit(Resource.STONE, 3));
		requirement1.addUnit(new Unit(Resource.WOOD, 3));
		
		//The second requirement isn't verified for no one
		requirement2 = new Packet();
		requirement2.addUnit(new Unit(Resource.MILITARYPOINT, 3));
		
		requirements = new ArrayList<>();
		
		requirements.add(requirement1);
		requirements.add(requirement2);
	}
	
	private void createImmediateEffects() {
		
		Packet requirement1 = new Packet();
		requirement1.addUnit(new Unit(Resource.FAITHPOINT, 1));
		
		Packet gain1 = new Packet();
		gain1.addUnit(new Unit(Resource.MILITARYPOINT, 1));
		
		immediateEffect1 = new ForEachObtain(requirement1, gain1);
		
		Packet cost2 = new Packet();
		cost2.addUnit(new Unit(Resource.MONEY, 2));
		
		Packet gain2 = new Packet();
		gain2.addUnit(new Unit(Resource.FAITHPOINT, 5));
		
		immediateEffect2 = new Obtain(cost2, gain2, null);
		
		immediateEffects = new ArrayList<>();
		
		immediateEffects.add(immediateEffect1);
		immediateEffects.add(immediateEffect2);
		
	}
	
	@Before
	public void constructCard() {
		firstCard = new Card("First", "Something", CardColor.YELLOW, 2, 4, costs, immediateEffects, requirements, null, null);
		secondCard = new Card("First", "Something", CardColor.YELLOW, 2, 4, costs, immediateEffects, requirements, null, null);
		thirdCard = new Card("First", "Something", CardColor.YELLOW, 2, 4, costs, immediateEffects, null, null, null);
	}

	@Test
	public void test1() throws NotEnoughResourcesException {
		
		//canTakeCard player has resources to activate the Obtain Effect
		//while cannotEnableObtainEffect can only enable the For Each Obtain
		
		firstCard.payCard(canTakeCard, null);
		thirdCard.payCard(cannotEnableObtainEffect, null);
		
		assertEquals(5, canTakeCard.getResource(Resource.MONEY));
		assertEquals(3, canTakeCard.getResource(Resource.STONE));
		assertEquals(3, canTakeCard.getResource(Resource.WOOD));
		assertEquals(0, canTakeCard.getResource(Resource.FAITHPOINT));
		
		assertEquals(3, cannotEnableObtainEffect.getResource(Resource.MONEY));
		assertEquals(3, cannotEnableObtainEffect.getResource(Resource.STONE));
		assertEquals(3, cannotEnableObtainEffect.getResource(Resource.WOOD));
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.FAITHPOINT));
		
		List<CardRequest> requests = canTakeCard.getRequests();
		
		for(CardRequest request : requests) {
			request.setChoice(0);
			request.apply(canTakeCard);
		}
		canTakeCard.synchResource();
		
		requests = cannotEnableObtainEffect.getRequests();
		
		for(CardRequest request : requests) {
			request.setChoice(0);
			request.apply(cannotEnableObtainEffect);
			cannotEnableObtainEffect.synchResource();
		}
		
		assertEquals(3, canTakeCard.getResource(Resource.MONEY));
		assertEquals(0, canTakeCard.getResource(Resource.STONE));
		assertEquals(3, canTakeCard.getResource(Resource.WOOD));
		assertEquals(0, canTakeCard.getResource(Resource.FAITHPOINT));
		
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.MONEY));
		assertEquals(0, cannotEnableObtainEffect.getResource(Resource.STONE));
		assertEquals(3, cannotEnableObtainEffect.getResource(Resource.WOOD));
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.FAITHPOINT));
		
		//Now gamelogic enables the card immediateEffects
		firstCard.setPlayer(canTakeCard);
		canTakeCard.addCard(firstCard);
		firstCard.enableImmediateEffect();
		
		secondCard.setPlayer(cannotEnableObtainEffect);
		cannotEnableObtainEffect.addCard(secondCard);
		secondCard.enableImmediateEffect();
		
		//cannotEnableObtainEffect can't afford the cost of the first effect
		//But the second effect is a ForEachObtain, so it will be applied immediately
		
		//Before the player has all the resources
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.MONEY));
		assertEquals(0, cannotEnableObtainEffect.getResource(Resource.STONE));
		assertEquals(3, cannotEnableObtainEffect.getResource(Resource.WOOD));
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.FAITHPOINT));
		
		requests = canTakeCard.getRequests();
		
		//Apply the Obtain Effect
		requests.get(0).setChoice(1);
		requests.get(0).apply(canTakeCard);
		
		canTakeCard.synchResource();		
		
		cannotEnableObtainEffect.synchResource();
		
		//After, the canTakeCard player has only one money, but 5 faithpoint, and before he has had 0
		assertEquals(1, canTakeCard.getResource(Resource.MONEY));
		assertEquals(0, canTakeCard.getResource(Resource.STONE));
		assertEquals(3, canTakeCard.getResource(Resource.WOOD));
		assertEquals(5, canTakeCard.getResource(Resource.FAITHPOINT));
		
		//After the cannotEnableObtainEffect player has also one military point, because he has one faithpoint
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.MONEY));
		assertEquals(0, cannotEnableObtainEffect.getResource(Resource.STONE));
		assertEquals(3, cannotEnableObtainEffect.getResource(Resource.WOOD));
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.FAITHPOINT));
		assertEquals(1, cannotEnableObtainEffect.getResource(Resource.MILITARYPOINT));
		
		}
	
	@Test
	public void test2() {
		//The player cannotPay can't pay the card costs
		try {
			firstCard.payCard(cannotPay, null);
			fail();
		} catch (NotEnoughResourcesException e) {
			assertTrue(true);
		}
		
		//While the player canTakeCard can pay
		
		try {
			firstCard.payCard(canTakeCard, null);
			
			//Select the second cost for example
			
			List<CardRequest> requests = canTakeCard.getRequests();
			assertEquals(1, requests.size());
			
			CardRequest request = requests.get(0);
			request.setChoice(1);
			request.apply(canTakeCard);
			canTakeCard.synchResource();
			
			//Verify if player requests list is empty
			assertEquals(0, canTakeCard.getRequests().size());
			
			//Verify if card has been payed
			assertEquals(3, canTakeCard.getResource(Resource.MONEY));
			assertEquals(3, canTakeCard.getResource(Resource.STONE));
			assertEquals(0, canTakeCard.getResource(Resource.WOOD));
			
		} catch (NotEnoughResourcesException e) {
			fail();
		}
	}
	
	@Test
	public void test3() {
		//hasNotRequirements player cannot take the card because he hasn't the correct
		//requirement
		
		//Card has two costs: 2 Money and 3 Stone or 2 Money and 3 Wood
		//hasNotRequirements player has the resources to satisfy one cost, but he hasn't the requirements of the card
		//so the payment doesn't continue
		assertEquals(3, hasNotRequirements.getResource(Resource.MONEY));
		assertEquals(3, hasNotRequirements.getResource(Resource.STONE));
		assertEquals(0, hasNotRequirements.getResource(Resource.WOOD));
		
		try {
			firstCard.payCard(hasNotRequirements, null);
			fail();
		} catch (NotEnoughResourcesException e) {
			assertTrue(true);
		}
	}
	
}
