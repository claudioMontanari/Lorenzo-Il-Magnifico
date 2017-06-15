package it.polimi.ingsw.ps42.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.polimi.ingsw.ps42.model.Card;
import it.polimi.ingsw.ps42.model.Table;
import it.polimi.ingsw.ps42.model.exception.ElementNotFoundException;
import it.polimi.ingsw.ps42.model.exception.GameLogicError;
import it.polimi.ingsw.ps42.model.exception.NotEnoughPlayersException;
import it.polimi.ingsw.ps42.model.player.Player;
import it.polimi.ingsw.ps42.view.TerminalView;

public class GameLogicTest {

	@Test
	public void test() throws NotEnoughPlayersException, GameLogicError, IOException, ElementNotFoundException {
		GameLogic logic;
		TerminalView view1 = new TerminalView();
		TerminalView view2 = new TerminalView();
		
		view1.addPlayer("Player 1");
		view2.addPlayer("Player 2");
		
		ArrayList<String> players = new ArrayList<>();
		ArrayList<String> players1View = new ArrayList<>();
		ArrayList<String> players2View = new ArrayList<>();
		
		players.add(view1.getViewPlayerID());
		players.add(view2.getViewPlayerID());
		
		players1View.add(view1.getViewPlayerID());
		players1View.add(view2.getViewPlayerID());
		
		players2View.add(view1.getViewPlayerID());
		players2View.add(view2.getViewPlayerID());
		
		view1.createTable(players1View);
		view2.createTable(players2View);
		
		logic = new GameLogic(players);
		logic.addView(view1);
		logic.addView(view2);
		
		logic.initGame();
	}

}
