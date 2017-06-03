package it.polimi.ingsw.ps42.model.action;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import it.polimi.ingsw.ps42.model.StaticList;
import it.polimi.ingsw.ps42.model.effect.Effect;
import it.polimi.ingsw.ps42.model.effect.IncreaseAction;
import it.polimi.ingsw.ps42.model.effect.NoMarketBan;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.FamiliarColor;
import it.polimi.ingsw.ps42.model.enumeration.Resource;
import it.polimi.ingsw.ps42.model.enumeration.Response;
import it.polimi.ingsw.ps42.model.exception.FamiliarInWrongPosition;
import it.polimi.ingsw.ps42.model.exception.NotEnoughResourcesException;
import it.polimi.ingsw.ps42.model.player.Familiar;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.model.position.MarketPosition;
import it.polimi.ingsw.ps42.model.resourcepacket.Packet;
import it.polimi.ingsw.ps42.model.resourcepacket.Unit;

public class MarketActionTest {

	private Player player;
	private Familiar familiar;
	private Familiar incrementedFamiliar;
	private StaticList<MarketPosition> tablePosition;
	private Action marketAction;
	private Action marketActionFail;
	private Action marketActionIncremented;
	private Action action;
	
	@Before
	public void setup(){
		
		//Create the player
		player = new Player("playerTest ");
		Packet resources = new Packet();
		resources.addUnit(new Unit(Resource.SLAVE, 5));
		player.increaseResource(resources);
		player.synchResource();
		
		//Setup the familiar
		familiar = player.getFamiliar(FamiliarColor.BLACK);
		familiar.setValue(1);
		
		incrementedFamiliar = player.getFamiliar(FamiliarColor.NEUTRAL);
		incrementedFamiliar.setIncrement(3);
		
		//Create the positions and their bonuses
		Packet gains1 = new Packet();
		gains1.addUnit(new Unit(Resource.MONEY, 2));

		Packet gains2 = new Packet();
		gains2.addUnit(new Unit(Resource.MONEY, 4));
		
		Obtain bonus1 = new Obtain(null, gains1);
		Obtain bonus2 = new Obtain(null, gains2);
		
		MarketPosition position1 = new MarketPosition(0, bonus1, 0);
		MarketPosition position2 = new MarketPosition(0, bonus2, 3);
		
		tablePosition = new StaticList<>(4);
		tablePosition.add(position1);
		tablePosition.add(position2);

		
	}
	
	@Before
	public void setupSimpleAction(){
		
		setup();		
		//Create an action that can be performed
		try {
			marketAction = new MarketAction(ActionType.MARKET, familiar , tablePosition, 0);
		} catch (NotEnoughResourcesException e) {
			e.printStackTrace();
		}
		
	}
	@Before
	public void setupFailAction(){
		
		setup();
		//Create an action that can not be performed
		try {
			marketActionFail = new MarketAction(ActionType.MARKET, familiar, tablePosition, 1);
		} catch (NotEnoughResourcesException e) {
			assertTrue(true);

		}
	}
	
	@Before
	public void setupFamiliarIncrementAction(){
		
		setup();
		//Create an action with a familiar increment that can be performed
		try {
			marketActionIncremented = new MarketAction(ActionType.MARKET, incrementedFamiliar, tablePosition, 1);
		} catch (NotEnoughResourcesException e) {
			
			e.printStackTrace();
		}
		
	}
	
	@Before
	public void setupLowLevelAction(){
		
		setup();
		//Create an action with a position level higher than the familiar
		MarketPosition position3 = new MarketPosition(4, null, 0);
		tablePosition.add(position3);
		try {
			action = new MarketAction(ActionType.MARKET, familiar, tablePosition, 2);
		} catch (NotEnoughResourcesException e) {
			e.printStackTrace();
		}
	}
	
	@Before 
	public void setupOccupiedPositionAction() throws FamiliarInWrongPosition, NotEnoughResourcesException {
		//Create a position occupied and try to perform an action on that position
		setup();
		MarketPosition position3 = new MarketPosition(0, null, 0);
		Familiar tempFamiliar = new Familiar(player, FamiliarColor.BLACK);
		
		position3.setFamiliar( tempFamiliar);
		tablePosition.add(position3);
		action = new MarketAction(ActionType.MARKET, tempFamiliar, tablePosition, 2);
	}
	
	@Before
	public void setupBanAction() throws NotEnoughResourcesException{
		
		//Create an action that can be performed but set the market ban to the player
		setup();
		Effect ban = new NoMarketBan();
		ban.enableEffect(player);
		
		action = new MarketAction(ActionType.MARKET, familiar, tablePosition, 0);
		
	}
	
	@Before
	public void setupBonusAction() throws NotEnoughResourcesException{
		//Create a bonus action
		setup();
		action = new MarketAction(ActionType.MARKET, player, tablePosition, 0, 0);
		
	}
	
	@Before
	public void setupIncreasedAction() throws NotEnoughResourcesException{
		//Add to the player an increase effect and check if the action can be performed
		Effect increaseEffect = new IncreaseAction(ActionType.MARKET, 2, null);
		increaseEffect.enableEffect(player);
		
		action = new MarketAction(ActionType.MARKET, familiar, tablePosition, 1);
		
	}
	
	@Test
	public void test() {
		//Test different kind of market action
		assertEquals(5, player.getResource(Resource.SLAVE));
		
		setupSimpleAction();
		player.synchResource();
		assertEquals(5, player.getResource(Resource.SLAVE));
		
		assertEquals(Response.SUCCESS, marketAction.checkAction());
		try {
			//Do the action and check the position bonus income (2 money)
			marketAction.doAction();
			assertEquals( 2, player.getResource(Resource.MONEY));
		} catch (FamiliarInWrongPosition e1) {
			e1.printStackTrace();
		}
		
		
		setupFailAction();
		player.synchResource();
		assertEquals(5, player.getResource(Resource.SLAVE));
		//Nothings to do since the action fails to create
		
		setupFamiliarIncrementAction();
		player.synchResource();
		assertEquals(2, player.getResource(Resource.SLAVE));
		
		assertEquals(Response.SUCCESS, marketActionIncremented.checkAction() );
		
		try {
			//Do the action and check the position bonus income (4 money)
			marketActionIncremented.doAction();
			assertEquals( 4, player.getResource(Resource.MONEY));
		} catch (FamiliarInWrongPosition e1) {
			e1.printStackTrace();
		}
		
		setupLowLevelAction();
		player.synchResource();
		assertEquals(5, player.getResource(Resource.SLAVE));
		
		assertEquals(Response.LOW_LEVEL , action.checkAction());
		//Nothing to do since the action do not passed the check
		
		try {
			
			setupOccupiedPositionAction();
			player.synchResource();
			assertEquals(5, player.getResource(Resource.SLAVE));
			
			assertEquals(Response.FAILURE, action.checkAction());
			//Nothing to do since the action do not passed the check
			
		} catch (FamiliarInWrongPosition e) {
			e.printStackTrace();
		} catch (NotEnoughResourcesException e) {
			e.printStackTrace();
		}
		
		try {
			setupBanAction();
			player.synchResource();
			assertEquals(5, player.getResource(Resource.SLAVE));
			
			assertEquals( Response.FAILURE, action.checkAction());
			//Nothing to do since the action do not passed the check
			
		} catch (NotEnoughResourcesException e) {
			e.printStackTrace();
		}
		
		try {
			setupBonusAction();
			player.synchResource();
			assertEquals( 5, player.getResource(Resource.SLAVE));
			assertEquals(0 , player.getResource(Resource.MONEY));
			
			assertEquals(Response.SUCCESS, action.checkAction());
			action.doAction();
			assertEquals(2 , player.getResource(Resource.MONEY));
		} catch (NotEnoughResourcesException | FamiliarInWrongPosition e) {
			e.printStackTrace();
		}
	
		try {
			setupIncreasedAction();
			player.synchResource();
			assertEquals( 5, player.getResource(Resource.SLAVE));
			assertEquals( 0, player.getResource(Resource.MONEY));
			
			assertEquals(Response.SUCCESS, action.checkAction() );
			action.doAction();
			assertEquals(4 , player.getResource(Resource.MONEY));
		} catch (NotEnoughResourcesException | FamiliarInWrongPosition e) {
			e.printStackTrace();
		}
		
	}

}
