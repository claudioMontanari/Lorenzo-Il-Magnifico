package it.polimi.ingsw.ps42.model.player;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.effect.ForEachObtain;
import it.polimi.ingsw.ps42.model.enumeration.CardColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

/**
 * This Unit case test tries to know if the Player package works great, testing all the mainly
 * functionality, such as the addition of a bonus bar, the addition of cards and the increment of resources
 * @author luca
 *
 */
public class PlayerTest {
	
	@BeforeClass
	public static void classSetUp() {
		PropertyConfigurator.configure("Logger//Properties//test_log.properties");
	}

	/**
	 * First, the test create the player, then it adds a simple bonus bar to the player.
	 * Second the test tries to add some Packet to the player resources
	 * Finally the test adds two cards to the player to try this functionality
	 */
	@Test
	public void test() throws NotEnoughResourcesException {
		Player player = new Player("Player 1");
		BonusBar bonusBar = new BonusBar();
		player.setBonusBar(bonusBar);
		
		Unit requirement = new Unit(Resource.FAITHPOINT, 3);
		Unit gain = new Unit(Resource.MONEY, 5);
		
		Packet requirements = new Packet();
		requirements.addUnit(requirement);
		Packet gains = new Packet();
		gains.addUnit(gain);
		
		Effect effect = new ForEachObtain(requirements, gains);
		
		ArrayList<Effect> effects = new ArrayList<>();
		effects.add(effect);
		
		Card c = new Card("Prova", "description", CardColor.GREEN, 1, 3,
				null, effects, null, null, null);
		
		player.increaseResource(requirements);
		player.increaseResource(requirements);
		player.increaseResource(requirements);
		player.synchResource();
		
		c.setPlayer(player);
		player.addCard(c);
		c.enableImmediateEffect();
		player.synchResource();
		assertEquals("Expected 15 money", 15, player.getResource(Resource.MONEY));
		
		//NOW WITH 2 EFFECTS
		effects.add(effect);
		Card c2 = new Card("Prova", "description", CardColor.GREEN, 1, 3,
				null, effects, null, null, null); 
		player.addCard(c2);
		c2.setPlayer(player);
		c2.enableImmediateEffect();
	}

}
