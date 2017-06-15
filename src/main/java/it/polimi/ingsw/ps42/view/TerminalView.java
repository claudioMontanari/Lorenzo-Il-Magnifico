package it.polimi.ingsw.ps42.view;

import java.util.List;
import java.util.Scanner;

import it.polimi.ingsw.ps42.message.CardRequest;
import it.polimi.ingsw.ps42.message.PlayerMove;
import it.polimi.ingsw.ps42.model.action.ActionPrototype;
import it.polimi.ingsw.ps42.model.effect.Obtain;
import it.polimi.ingsw.ps42.model.enumeration.ActionType;
import it.polimi.ingsw.ps42.model.enumeration.FamiliarColor;
import it.polimi.ingsw.ps42.model.leaderCard.LeaderCard;
import it.polimi.ingsw.ps42.model.player.BonusBar;

public class TerminalView extends View {

	
	private Scanner scanner;
	
	public TerminalView() {
		
		super();
		scanner = new Scanner(System.in);
	}
	
	@Override
	protected int chooseBonusBar(List<BonusBar> bonusBarList) {
		System.out.println("Scegli una BonusBar per la partita [0-"+(bonusBarList.size()-1)+"]");
		return scanner.nextInt();
	}

	@Override
	protected int chooseLeaderCard(List<LeaderCard> leaderCardList) {
		System.out.println("Scegli una Leader Card per la partita [0-"+(leaderCardList.size()-1)+"]");
		return scanner.nextInt();
	}

	@Override
	protected int chooseCouncilConversion(List<Obtain> possibleConversions) {
		System.out.println("Scegli una Conversione per il privilegio del consiglio [0-"+(possibleConversions.size()-1)+"]");
		return scanner.nextInt();
	}

	@Override
	protected PlayerMove choosePlayerMove(ActionPrototype prototype) {
		System.out.println("Nuova mossa per il giocatore corrente");
		if(prototype != null)
			System.out.println("é una mossa bonus del tipo"+ prototype.getType().toString()+ 
								" livello: "+ prototype.getLevel());
		
		System.out.println("tipo mossa?");
		
		System.out.println(ActionType.COUNCIL+"\n "+ActionType.MARKET+"\n "+ActionType.PRODUCE+"\n "+ActionType.TAKE_ALL+"\n "
				+ActionType.TAKE_BLUE+"\n "+ActionType.TAKE_GREEN+"\n "+ActionType.TAKE_VIOLET+"\n "+ActionType.TAKE_YELLOW+
				"\n "+ActionType.YIELD);
		
		ActionType moveType = ActionType.parseInput(scanner.nextLine());
		System.out.println("colore familiare?");

		System.out.println(FamiliarColor.BLACK+"\n "+FamiliarColor.NEUTRAL+"\n "+FamiliarColor.ORANGE+
							"\n "+FamiliarColor.WHITE);
		
		FamiliarColor familiarColor = FamiliarColor.parseInput(scanner.nextLine());
		System.out.println("posizione?");
		int position = scanner.nextInt();
		System.out.println("incremento del familiare?");
		int increaseValue= scanner.nextInt();
		
		
		return new PlayerMove(this.getViewPlayerID(), moveType, familiarColor, position, increaseValue);
	}

	@Override
	protected boolean chooseIfPayBan(int banPeriod) {
		System.out.println("Scegli se pagare una scomunica (si/no)");
		String response = scanner.nextLine();
		
		return response.toUpperCase().equals("SI");
	}

	@Override
	protected int answerCardRequest(CardRequest message) {
		System.out.println("Risolvi una richiesta della carta [0-"+(message.getPossibleChoiceIndex().size()-1)+"]");
		return scanner.nextInt();
	}

	@Override
	protected void notifyLeaderCardActiovation() {
		System.out.println("Il player corrente vuole attivare una carta leader");
		
	}

	@Override
	protected void notifyLeaderCardDiscard() {
		System.out.println("il player corrente vuole scartare una cata leader");
		
	}

}