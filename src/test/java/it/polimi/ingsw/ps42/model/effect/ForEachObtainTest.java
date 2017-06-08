package it.polimi.ingsw.ps42.model.effect;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.ps42.message.RequestInterface;
import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class ForEachObtainTest {

	private Player player;
	private Card cardImmediate;
	
	
	@Before
	public void setup(){
		//Build the player and add resources
		player = new Player("playerTest");
		Packet playerResources = new Packet();
		playerResources.addUnit(new Unit(Resource.MONEY, 6));
		playerResources.addUnit(new Unit(Resource.STONE, 2));
		player.increaseResource(playerResources);
		player.synchResource();
		//Build the cards with a ForEachObtainEffect
		
		//Build the card costs
		Packet cost = new Packet();
		ArrayList<Packet> costs = new ArrayList<>();
		costs.add(cost);
		
		//Build the effects
		ArrayList<Effect> immediateEffects = new ArrayList<>();
		immediateEffects.add(buildEasyEffect());
		immediateEffects.add(buildEasyEffect());
		immediateEffects.add(buildComplexEffect());
		
		//Build the card
		cardImmediate = new Card("cardImmediate", "", CardColor.BLUE, 2 , 2, costs, immediateEffects, 
				null , immediateEffects, immediateEffects);
		
	}
	
	private Effect buildEasyEffect(){
		
		Packet effectRequirements = new Packet();
		effectRequirements.addUnit(new Unit(Resource.MONEY, 2));
		Packet effectGains = new Packet();
		effectGains.addUnit(new Unit(Resource.FAITHPOINT, 1));
		Effect forEachObtain = new ForEachObtain(effectRequirements, effectGains);
		
		return forEachObtain;
		
	}
	
	private Effect buildComplexEffect(){
		
		Packet effectRequirements = new Packet();
		effectRequirements.addUnit(new Unit(Resource.MONEY, 1));
		effectRequirements.addUnit(new Unit(Resource.STONE, 1));
		Packet effectGains = new Packet();
		effectGains.addUnit(new Unit(Resource.FAITHPOINT, 1));
		Effect forEachObtain = new ForEachObtain(effectRequirements, effectGains);
		
		return forEachObtain;
		
	}
	@After
	public void finalCheck(){
		
		//Check the immediate card effect
		assertEquals( 14, player.getResource(Resource.FAITHPOINT));
		assertEquals( 6, player.getResource(Resource.MONEY));
		
		
	}
	
	public void intermediateCheck(){
		
		assertEquals(8, player.getResource(Resource.FAITHPOINT));
	}
	
	@Test
	public void test() {
		
		setup();
		
		player.addCard(cardImmediate);
		cardImmediate.setPlayer(player);
		
		assertEquals(cardImmediate, player.getCardList(CardColor.BLUE).get(0));
		assertEquals( 6, player.getResource(Resource.MONEY));
		
		try {
			cardImmediate.enableImmediateEffect();
			RequestInterface requestImmediate = player.getRequests().get(0);
			requestImmediate.setChoice(2);
			requestImmediate.apply();
			player.synchResource();
			intermediateCheck();
			
			cardImmediate.enableFinalEffect();
			RequestInterface requestFinal = player.getRequests().get(0);
			requestFinal.setChoice(0);
			requestFinal.apply();
			player.synchResource();
			
			cardImmediate.enablePermanentEffect();
			RequestInterface requestPermanent= player.getRequests().get(0);
			requestPermanent.setChoice(1);
			requestPermanent.apply();
			player.synchResource();
			finalCheck();
			
		} catch (NotEnoughResourcesException e) {
		
			e.printStackTrace();
		}
		
	}

}
